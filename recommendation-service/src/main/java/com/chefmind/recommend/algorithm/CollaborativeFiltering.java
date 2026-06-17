package com.chefmind.recommend.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐算法
 * 实现 User-CF（基于用户的协同过滤）和 Item-CF（基于物品的协同过滤）
 */
@Slf4j
@Component
public class CollaborativeFiltering {

    /**
     * User-CF：基于用户的协同过滤推荐
     * 1. 计算用户之间的相似度（余弦相似度）
     * 2. 找到目标用户最相似的 K 个用户
     * 3. 推荐相似用户喜欢的菜品
     */
    public List<Long> userCfRecommend(Long targetUserId,
                                       Map<Long, Map<Long, Double>> userItemMatrix,
                                       Map<Long, Set<Long>> userItems,
                                       int topN) {
        // 目标用户没有交互数据，无法推荐
        if (!userItems.containsKey(targetUserId) || userItems.get(targetUserId).isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 计算目标用户与其他所有用户的相似度
        Map<Long, Double> similarities = new HashMap<>();
        Map<Long, Double> targetVector = userItemMatrix.getOrDefault(targetUserId, Collections.emptyMap());

        for (Map.Entry<Long, Map<Long, Double>> entry : userItemMatrix.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(targetUserId)) continue;

            double similarity = cosineSimilarity(targetVector, entry.getValue());
            if (similarity > 0) {
                similarities.put(otherUserId, similarity);
            }
        }

        // 2. 找到最相似的 K 个用户
        int k = Math.min(10, similarities.size());
        List<Long> similarUsers = similarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.debug("User-CF: Top {} similar users for user {}: {}", k, targetUserId, similarUsers);

        // 3. 推荐相似用户喜欢的但目标用户未交互的菜品
        Set<Long> targetItems = userItems.getOrDefault(targetUserId, Collections.emptySet());
        Map<Long, Double> candidateScores = new HashMap<>();

        for (Long similarUser : similarUsers) {
            double simScore = similarities.get(similarUser);
            Set<Long> similarUserItems = userItems.getOrDefault(similarUser, Collections.emptySet());

            for (Long item : similarUserItems) {
                if (!targetItems.contains(item)) {
                    candidateScores.merge(item, simScore, Double::sum);
                }
            }
        }

        // 按得分排序取 TopN
        return candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Item-CF：基于物品的协同过滤推荐
     * 1. 计算菜品之间的相似度
     * 2. 找到目标用户交互过的菜品的相似菜品
     */
    public List<Long> itemCfRecommend(Long targetUserId,
                                       Map<Long, Map<Long, Double>> itemUserMatrix,
                                       Map<Long, Set<Long>> userItems,
                                       int topN) {
        Set<Long> targetItems = userItems.getOrDefault(targetUserId, Collections.emptySet());
        if (targetItems.isEmpty()) {
            return Collections.emptyList();
        }

        // 计算所有与用户交互过的菜品相似的菜品
        Map<Long, Double> candidateScores = new HashMap<>();

        for (Long itemId : targetItems) {
            Map<Long, Double> itemVector = itemUserMatrix.getOrDefault(itemId, Collections.emptyMap());

            for (Map.Entry<Long, Map<Long, Double>> entry : itemUserMatrix.entrySet()) {
                Long candidateItemId = entry.getKey();
                if (candidateItemId.equals(itemId) || targetItems.contains(candidateItemId)) continue;

                double similarity = cosineSimilarity(itemVector, entry.getValue());
                if (similarity > 0) {
                    candidateScores.merge(candidateItemId, similarity, Double::sum);
                }
            }
        }

        return candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 计算两个向量之间的余弦相似度
     */
    private double cosineSimilarity(Map<Long, Double> vectorA, Map<Long, Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // 计算点积
        for (Map.Entry<Long, Double> entry : vectorA.entrySet()) {
            Long key = entry.getKey();
            Double valueA = entry.getValue();
            Double valueB = vectorB.get(key);
            if (valueB != null) {
                dotProduct += valueA * valueB;
            }
            normA += valueA * valueA;
        }

        for (Double value : vectorB.values()) {
            normB += value * value;
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominator == 0) {
            return 0.0;
        }

        return dotProduct / denominator;
    }

    /**
     * 构建用户-菜品评分矩阵
     * key: userId, value: (recipeId -> score)
     */
    public Map<Long, Map<Long, Double>> buildUserItemMatrix(List<Map<String, Object>> rawData) {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            Long userId = ((Number) row.get("user_id")).longValue();
            Long recipeId = ((Number) row.get("recipe_id")).longValue();
            Double score = ((Number) row.get("score")).doubleValue();
            matrix.computeIfAbsent(userId, k -> new HashMap<>()).put(recipeId, score);
        }
        return matrix;
    }

    /**
     * 构建菜品-用户评分矩阵（用于 Item-CF）
     */
    public Map<Long, Map<Long, Double>> buildItemUserMatrix(List<Map<String, Object>> rawData) {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            Long userId = ((Number) row.get("user_id")).longValue();
            Long recipeId = ((Number) row.get("recipe_id")).longValue();
            Double score = ((Number) row.get("score")).doubleValue();
            matrix.computeIfAbsent(recipeId, k -> new HashMap<>()).put(userId, score);
        }
        return matrix;
    }

    /**
     * 构建用户-菜品集合（用户交互过的所有菜品）
     */
    public Map<Long, Set<Long>> buildUserItems(List<Map<String, Object>> rawData) {
        Map<Long, Set<Long>> userItems = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            Long userId = ((Number) row.get("user_id")).longValue();
            Long recipeId = ((Number) row.get("recipe_id")).longValue();
            userItems.computeIfAbsent(userId, k -> new HashSet<>()).add(recipeId);
        }
        return userItems;
    }
}
