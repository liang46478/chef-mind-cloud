package com.chefmind.recipe.controller;

import com.chefmind.common.result.Result;
import com.chefmind.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 菜谱生成控制器 - 输入菜名/食材，返回 AI 生成的菜谱
 */
@RestController
@RequestMapping("/api/recipe/ai")
@RequiredArgsConstructor
public class RecipeAiController {

    private final RecipeService recipeService;

    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(
            @RequestBody Map<String, String> request) {
        String dishName = request.get("dishName");
        String ingredients = request.get("availableIngredients");

        if (dishName == null || dishName.trim().isEmpty()) {
            return Result.error(400, "菜名不能为空");
        }

        // 先查询数据库中是否已有该菜谱
        var existing = recipeService.searchRecipes(dishName);
        if (!existing.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("source", "database");
            result.put("recipes", existing);
            return Result.success(result);
        }

        // 数据库中没有，返回提示让用户添加
        return Result.success(new HashMap<>() {{
            put("source", "none");
            put("message", "未找到「" + dishName + "」的相关菜谱，请在管理后台添加");
        }});
    }
}
