package com.chefmind.recipe.controller;

import com.chefmind.common.result.Result;
import com.chefmind.recipe.ai.RecipeAiService;
import com.chefmind.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/recipe/ai")
@RequiredArgsConstructor
public class RecipeAiController {

    private final RecipeService recipeService;
    private final RecipeAiService recipeAiService;

    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(@RequestBody Map<String, String> request) {
        String dishName = request.get("dishName");
        if (dishName == null || dishName.trim().isEmpty())
            return Result.error(400, "菜名不能为空");

        var existing = recipeService.searchRecipes(dishName);
        if (!existing.isEmpty()) {
            return Result.success(Map.of("source", "database", "recipes", existing));
        }

        try {
            var aiRecipes = recipeAiService.generateByIngredients(List.of(dishName));
            return Result.success(Map.of("source", "ai", "recipes", aiRecipes));
        } catch (Exception e) {
            return Result.success(Map.of("source", "none", "message", "未找到「" + dishName + "」的相关菜谱"));
        }
    }

    @PostMapping("/by-ingredients")
    public Result<Map<String, Object>> byIngredients(@RequestBody Map<String, List<String>> request) {
        List<String> ingredients = request.getOrDefault("ingredients", List.of());
        if (ingredients.isEmpty()) return Result.error(400, "请提供食材");

        // 先查数据库
        List<Long> ingredientIds = new ArrayList<>();
        var allIngredients = recipeService.getAllIngredients();
        Map<String, Long> nameToId = new HashMap<>();
        for (var ing : allIngredients) {
            nameToId.put((String) ing.get("name"), ((Number) ing.get("id")).longValue());
        }
        for (String name : ingredients) {
            if (nameToId.containsKey(name)) ingredientIds.add(nameToId.get(name));
        }

        List<Map<String, Object>> dbResults = new ArrayList<>();
        if (!ingredientIds.isEmpty()) {
            dbResults = recipeService.searchByIngredients(ingredientIds, "any");
        }

        if (!dbResults.isEmpty()) {
            return Result.success(Map.of("source", "database", "recipes", dbResults));
        }

        // DB无结果 → AI生成
        try {
            var aiRecipes = recipeAiService.generateByIngredients(ingredients);
            return Result.success(Map.of("source", "ai", "recipes", aiRecipes));
        } catch (Exception e) {
            var fallback = recipeAiService.generateFallbackRecipes(ingredients);
            return Result.success(Map.of("source", "fallback", "recipes", fallback));
        }
    }
}
