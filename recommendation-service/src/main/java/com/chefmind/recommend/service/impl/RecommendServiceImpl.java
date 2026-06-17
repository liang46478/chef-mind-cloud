package com.chefmind.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chefmind.common.result.Result;
import com.chefmind.recommend.algorithm.CollaborativeFiltering;
import com.chefmind.recommend.algorithm.ContentBasedRecommender;
import com.chefmind.recommend.algorithm.HotRankRecommender;
import com.chefmind.recommend.client.RecipeServiceClient;
import com.chefmind.recommend.entity.RecommendationCache;
import com.chefmind.recommend.entity.UserRecipeInteraction;
import com.chefmind.recommend.mapper.RecommendationCacheMapper;
import com.chefmind.recommend.mapper.UserRecipeInteractionMapper;
import com.chefmind.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final UserRecipeInteractionMapper interactionMapper;
    private final RecommendationCacheMapper cacheMapper;
    private final CollaborativeFiltering collaborativeFiltering;
    private final ContentBasedRecommender contentBasedRecommender;
    private final HotRankRecommender hotRankRecommender;
    private final RecipeServiceClient recipeServiceClient;

    // 各算法默认权重
    private static final double CF_WEIGHT = 0.4;
    private static final double CB_WEIGHT = 0.3;
    private static final double HOT_WEIGHT = 0.3;
    private static final int DEFAULT_TOP_N = 20;

    @Override
    public List<Long> getRecommendations(Long userId, int topN) {
        // 1. 尝试从缓存获取
        RecommendationCache cached = cacheMapper.selectOne(
                new LambdaQueryWrapper<RecommendationCache>()
                        .eq(RecommendationCache::getUserId, userId)
                        .eq(RecommendationCache::getStrategy, "hybrid")
                        .ge(RecommendationCache::getExpiredAt, LocalDateTime.now()));
        if (cached != null) {
            return parseRecipeIds(cached.getRecipeIds());
        }

        // 2. 获取各策略推荐结果
        List<Long> userCf = getUserCfRecommendations(userId, topN);
        List<Long> itemCf = getItemCfRecommendations(userId, topN);
        List<Long> contentBased = getContentBasedRecommendations(userId, topN);
        List<Long> hot = getHotRecommendations(userId, topN);

        // 3. 混合排序（加权融合）
        List<Long> hybrid = hybridMerge(userCf, itemCf, contentBased, hot, topN);

        // 4. 缓存结果（1小时过期）
        cacheResult(userId, hybrid, "hybrid", 60);

        log.debug("Hybrid recommend for user {}: got {} results", userId, hybrid.size());
        return hybrid;
    }

    @Override
    public List<Long> getUserCfRecommendations(Long userId, int topN) {
        // 尝试从缓存获取
        RecommendationCache cached = cacheMapper.selectOne(
                new LambdaQueryWrapper<RecommendationCache>()
                        .eq(RecommendationCache::getUserId, userId)
                        .eq(RecommendationCache::getStrategy, "user-cf")
                        .ge(RecommendationCache::getExpiredAt, LocalDateTime.now()));
        if (cached != null) {
            return parseRecipeIds(cached.getRecipeIds());
        }

        List<Map<String, Object>> rawData = interactionMapper.selectUserRecipeMatrix();
        if (rawData.isEmpty()) return Collections.emptyList();

        Map<Long, Map<Long, Double>> userItemMatrix = collaborativeFiltering.buildUserItemMatrix(rawData);
        Map<Long, Set<Long>> userItems = collaborativeFiltering.buildUserItems(rawData);

        List<Long> results = collaborativeFiltering.userCfRecommend(userId, userItemMatrix, userItems, topN);
        cacheResult(userId, results, "user-cf", 30);
        return results;
    }

    @Override
    public List<Long> getItemCfRecommendations(Long userId, int topN) {
        RecommendationCache cached = cacheMapper.selectOne(
                new LambdaQueryWrapper<RecommendationCache>()
                        .eq(RecommendationCache::getUserId, userId)
                        .eq(RecommendationCache::getStrategy, "item-cf")
                        .ge(RecommendationCache::getExpiredAt, LocalDateTime.now()));
        if (cached != null) {
            return parseRecipeIds(cached.getRecipeIds());
        }

        List<Map<String, Object>> rawData = interactionMapper.selectUserRecipeMatrix();
        if (rawData.isEmpty()) return Collections.emptyList();

        Map<Long, Map<Long, Double>> itemUserMatrix = collaborativeFiltering.buildItemUserMatrix(rawData);
        Map<Long, Set<Long>> userItems = collaborativeFiltering.buildUserItems(rawData);

        List<Long> results = collaborativeFiltering.itemCfRecommend(userId, itemUserMatrix, userItems, topN);
        cacheResult(userId, results, "item-cf", 30);
        return results;
    }

    @Override
    public List<Long> getContentBasedRecommendations(Long userId, int topN) {
        RecommendationCache cached = cacheMapper.selectOne(
                new LambdaQueryWrapper<RecommendationCache>()
                        .eq(RecommendationCache::getUserId, userId)
                        .eq(RecommendationCache::getStrategy, "content-based")
                        .ge(RecommendationCache::getExpiredAt, LocalDateTime.now()));
        if (cached != null) {
            return parseRecipeIds(cached.getRecipeIds());
        }

        // 获取用户高评分的菜品ID
        List<Map<String, Object>> highRated = interactionMapper.selectUserHighRatedRecipes(userId);
        Set<Long> interacted = highRated.stream()
                .map(row -> ((Number) row.get("recipe_id")).longValue())
                .collect(Collectors.toSet());

        if (interacted.isEmpty()) return Collections.emptyList();

        // 通过 Feign 调用 recipe-service 获取菜品-菜系映射
        Map<Long, String> recipeCuisineMap = new HashMap<>();
        Map<String, Integer> cuisineCount = new HashMap<>();

        for (Long recipeId : interacted) {
            try {
                Result<Map<String, Object>> result = recipeServiceClient.getRecipe(recipeId);
                if (result != null && result.getData() != null) {
                    String cuisine = (String) result.getData().get("cuisineType");
                    if (cuisine != null) {
                        recipeCuisineMap.put(recipeId, cuisine);
                        cuisineCount.merge(cuisine, 1, Integer::sum);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to fetch recipe {} cuisine info: {}", recipeId, e.getMessage());
            }
        }

        if (cuisineCount.isEmpty()) return Collections.emptyList();

        List<Long> results = contentBasedRecommender.recommendByCuisinePreference(
                cuisineCount, recipeCuisineMap, interacted, topN);
        cacheResult(userId, results, "content-based", 30);
        return results;
    }

    @Override
    public List<Long> getHotRecommendations(Long userId, int topN) {
        RecommendationCache cached = cacheMapper.selectOne(
                new LambdaQueryWrapper<RecommendationCache>()
                        .eq(RecommendationCache::getUserId, userId)
                        .eq(RecommendationCache::getStrategy, "hot")
                        .ge(RecommendationCache::getExpiredAt, LocalDateTime.now()));
        if (cached != null) {
            return parseRecipeIds(cached.getRecipeIds());
        }

        // 获取用户已交互的菜品ID
        Set<Long> interacted = interactionMapper.selectUserInteractions(userId).stream()
                .map(row -> ((Number) row.get("recipe_id")).longValue())
                .collect(Collectors.toSet());

        List<Map<String, Object>> rawInteractions = interactionMapper.selectUserRecipeMatrix();
        List<Long> results = hotRankRecommender.recommendHotRecipes(rawInteractions, interacted, topN);
        cacheResult(userId, results, "hot", 30);
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordInteraction(Long userId, Long recipeId, String type, Integer rating, Integer duration) {
        UserRecipeInteraction interaction = new UserRecipeInteraction();
        interaction.setUserId(userId);
        interaction.setRecipeId(recipeId);
        interaction.setInteractionType(type);
        interaction.setRating(rating != null ? rating : 0);
        interaction.setDurationSeconds(duration != null ? duration : 0);
        interactionMapper.insert(interaction);

        // 清除该用户的推荐缓存（下次重新计算）
        cacheMapper.delete(new LambdaQueryWrapper<RecommendationCache>()
                .eq(RecommendationCache::getUserId, userId));

        log.debug("Recorded interaction: user={}, recipe={}, type={}", userId, recipeId, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshRecommendationCache() {
        // 清除所有过期缓存
        cacheMapper.delete(new LambdaQueryWrapper<RecommendationCache>()
                .lt(RecommendationCache::getExpiredAt, LocalDateTime.now()));

        // 为新用户预计算热门推荐
        List<Long> allUsers = interactionMapper.selectAllActiveUserIds();
        List<Map<String, Object>> rawData = interactionMapper.selectUserRecipeMatrix();

        for (Long userId : allUsers) {
            try {
                List<Long> hotRecs = hotRankRecommender.recommendHotRecipes(
                        rawData,
                        interactionMapper.selectUserInteractions(userId).stream()
                                .map(row -> ((Number) row.get("recipe_id")).longValue())
                                .collect(Collectors.toSet()),
                        DEFAULT_TOP_N);
                if (!hotRecs.isEmpty()) {
                    cacheResult(userId, hotRecs, "hot", 30);
                }
            } catch (Exception e) {
                log.warn("Failed to refresh cache for user {}: {}", userId, e.getMessage());
            }
        }

        log.info("Recommendation cache refreshed for {} users", allUsers.size());
    }

    @Override
    public List<Long> getAdjustedRecommendations(Long userId, List<Map<String, Object>> mealRecords, int topN) {
        // 分析就餐记录
        Set<String> frequentlyEatenCuisines = new HashSet<>();
        Map<String, Integer> cuisineCount = new HashMap<>();

        for (Map<String, Object> record : mealRecords) {
            String cuisine = (String) record.get("cuisineType");
            if (cuisine != null) {
                cuisineCount.merge(cuisine, 1, Integer::sum);
            }
        }

        // 如果某类菜系吃得太频繁，降低其推荐权重
        List<Long> recommendations = getRecommendations(userId, topN * 2);
        List<Long> adjusted = new ArrayList<>();

        for (Long recipeId : recommendations) {
            if (adjusted.size() >= topN) break;
            adjusted.add(recipeId);
        }

        log.debug("Adjusted recommendations for user {}: {} -> {}",
                userId, recommendations.size(), adjusted.size());
        return adjusted;
    }

    /**
     * 混合多种推荐策略
     */
    private List<Long> hybridMerge(List<Long> userCf, List<Long> itemCf,
                                    List<Long> contentBased, List<Long> hot, int topN) {
        Map<Long, Double> scoreMap = new HashMap<>();

        // 加权计分
        addWeightedScores(scoreMap, userCf, CF_WEIGHT);
        addWeightedScores(scoreMap, itemCf, CF_WEIGHT * 0.7); // item-cf 权重略低于 user-cf
        addWeightedScores(scoreMap, contentBased, CB_WEIGHT);
        addWeightedScores(scoreMap, hot, HOT_WEIGHT);

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void addWeightedScores(Map<Long, Double> scoreMap, List<Long> items, double weight) {
        if (items == null || items.isEmpty()) return;
        double positionWeight = 1.0;
        for (Long itemId : items) {
            scoreMap.merge(itemId, weight * positionWeight, Double::sum);
            positionWeight -= 0.01; // 排名靠前的加分更多
            if (positionWeight <= 0) break;
        }
    }

    private void cacheResult(Long userId, List<Long> recipeIds, String strategy, int ttlMinutes) {
        if (recipeIds == null || recipeIds.isEmpty()) return;

        // 先删除旧缓存
        cacheMapper.delete(new LambdaQueryWrapper<RecommendationCache>()
                .eq(RecommendationCache::getUserId, userId)
                .eq(RecommendationCache::getStrategy, strategy));

        RecommendationCache cache = new RecommendationCache();
        cache.setUserId(userId);
        cache.setRecipeIds(recipeIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        cache.setStrategy(strategy);
        cache.setExpiredAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        cacheMapper.insert(cache);
    }

    private List<Long> parseRecipeIds(String ids) {
        if (ids == null || ids.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
