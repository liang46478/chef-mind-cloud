package com.chefmind.recommend.service;

import java.util.List;
import java.util.Map;

/**
 * 推荐服务接口
 */
public interface RecommendService {

    /**
     * 为用户获取个性化推荐（混合策略）
     */
    List<Long> getRecommendations(Long userId, int topN);

    /**
     * User-CF 推荐
     */
    List<Long> getUserCfRecommendations(Long userId, int topN);

    /**
     * Item-CF 推荐
     */
    List<Long> getItemCfRecommendations(Long userId, int topN);

    /**
     * 基于内容的推荐
     */
    List<Long> getContentBasedRecommendations(Long userId, int topN);

    /**
     * 热门推荐
     */
    List<Long> getHotRecommendations(Long userId, int topN);

    /**
     * 记录用户交互行为
     */
    void recordInteraction(Long userId, Long recipeId, String type, Integer rating, Integer duration);

    /**
     * 刷新推荐缓存（定时任务调用）
     */
    void refreshRecommendationCache();

    /**
     * 根据就餐记录调整推荐
     */
    List<Long> getAdjustedRecommendations(Long userId, List<Map<String, Object>> mealRecords, int topN);
}
