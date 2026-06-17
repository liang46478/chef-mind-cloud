package com.chefmind.recommend.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内容的推荐算法
 * 根据用户的饮食偏好（菜系、口味等）推荐相似菜品
 */
@Slf4j
@Component
public class ContentBasedRecommender {

    /**
     * 基于用户偏好的菜系标签进行推荐
     */
    public List<Long> recommendByCuisinePreference(
            Map<String, Integer> cuisinePreferences,
            Map<Long, String> recipeCuisineMap,
            Set<Long> interactedRecipes,
            int topN) {

        if (cuisinePreferences.isEmpty()) {
            return Collections.emptyList();
        }

        // 按偏好菜系排序
        List<String> preferredCuisines = cuisinePreferences.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 推荐用户偏好菜系中未交互过的菜品
        List<Long> recommendations = recipeCuisineMap.entrySet().stream()
                .filter(entry -> !interactedRecipes.contains(entry.getKey()))
                .filter(entry -> {
                    String cuisine = entry.getValue();
                    return preferredCuisines.contains(cuisine);
                })
                .sorted((e1, e2) -> {
                    String c1 = e1.getValue();
                    String c2 = e2.getValue();
                    int idx1 = preferredCuisines.indexOf(c1);
                    int idx2 = preferredCuisines.indexOf(c2);
                    return Integer.compare(idx1, idx2);
                })
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.debug("Content-Based: Recommended {} recipes based on cuisine preference", recommendations.size());
        return recommendations;
    }

    /**
     * 基于可用食材的推荐（冷启动场景）
     */
    public List<Long> recommendByIngredients(
            String[] availableIngredients,
            Map<Long, String[]> recipeIngredientsMap,
            int topN) {

        if (availableIngredients == null || availableIngredients.length == 0) {
            return Collections.emptyList();
        }

        Set<String> ingredientSet = new HashSet<>(Arrays.asList(availableIngredients));
        Map<Long, Integer> matchScores = new HashMap<>();

        for (Map.Entry<Long, String[]> entry : recipeIngredientsMap.entrySet()) {
            int matchCount = 0;
            for (String ingredient : entry.getValue()) {
                if (ingredientSet.contains(ingredient)) {
                    matchCount++;
                }
            }
            if (matchCount > 0) {
                matchScores.put(entry.getKey(), matchCount);
            }
        }

        return matchScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
