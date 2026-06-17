package com.chefmind.recommend.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 热门排行推荐
 * 基于用户交互热度（浏览量、收藏量、评分等）进行推荐
 */
@Slf4j
@Component
public class HotRankRecommender {

    /**
     * 基于交互热度推荐热门菜品
     * 使用加权评分：view(1) + favorite(5) + rate(3) + cook(4) + complete(3)
     */
    public List<Long> recommendHotRecipes(List<Map<String, Object>> rawInteractions,
                                           Set<Long> excludeRecipeIds,
                                           int topN) {
        // 统计每个菜品的热度得分
        Map<Long, Double> hotScores = new HashMap<>();

        for (Map<String, Object> row : rawInteractions) {
            Long recipeId = ((Number) row.get("recipe_id")).longValue();
            String type = (String) row.get("interaction_type");

            double score = switch (type != null ? type : "") {
                case "favorite" -> 5.0;
                case "cook" -> 4.0;
                case "rate" -> {
                    Number rating = (Number) row.get("rating");
                    yield rating != null ? rating.doubleValue() * 2 : 3.0;
                }
                case "complete" -> 3.0;
                default -> 1.0; // view
            };

            hotScores.merge(recipeId, score, Double::sum);
        }

        // 排除已交互的菜品，按热度排序
        return hotScores.entrySet().stream()
                .filter(entry -> !excludeRecipeIds.contains(entry.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 直接返回热门菜品列表（用于冷启动-新用户无交互数据时）
     */
    public List<Long> getGlobalHotRecipes(List<Map<String, Object>> hotScoresRaw,
                                           int topN) {
        return hotScoresRaw.stream()
                .limit(topN)
                .map(row -> ((Number) row.get("recipe_id")).longValue())
                .collect(Collectors.toList());
    }
}
