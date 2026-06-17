package com.chefmind.mealplan.controller;

import com.chefmind.common.result.Result;
import com.chefmind.mealplan.ai.AiMealPlanService;
import com.chefmind.mealplan.entity.MealPlan;
import com.chefmind.mealplan.entity.MealPlanItem;
import com.chefmind.mealplan.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meal-plan")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final AiMealPlanService aiMealPlanService;

    @GetMapping("/current")
    public Result<MealPlan> getCurrentPlan(
            @RequestParam(required = false, defaultValue = "1") Long userId,
            @RequestParam(required = false) String type) {
        MealPlan plan = mealPlanService.getCurrentPlan(userId, type);
        if (plan == null) {
            return Result.notFound("暂无用餐计划，请先生成");
        }
        return Result.success(plan);
    }

    @GetMapping("/{planId}/items")
    public Result<List<MealPlanItem>> getItems(@PathVariable Long planId) {
        return Result.success(mealPlanService.getPlanItems(planId));
    }

    @PostMapping("/generate")
    public Result<MealPlan> generate(
            @RequestParam(required = false, defaultValue = "1") Long userId,
            @RequestParam(required = false) String preferences,
            @RequestParam(required = false) String allergens,
            @RequestParam(required = false) String healthGoal) {
        MealPlan plan = mealPlanService.generatePlan(userId, preferences, allergens, healthGoal);
        return Result.success("AI 用餐计划生成成功", plan);
    }

    /**
     * 根据就餐记录调整用餐计划
     */
    @PostMapping("/adjust")
    public Result<List<Map<String, Object>>> adjustPlan(
            @RequestParam(required = false) String preferences,
            @RequestParam(required = false) String allergens,
            @RequestParam(required = false) String healthGoal,
            @RequestBody(required = false) String mealHistory) {
        List<Map<String, Object>> adjusted = aiMealPlanService.adjustPlanWithHistory(
                preferences, allergens, healthGoal, mealHistory);
        return Result.success(adjusted);
    }

    /**
     * 食材替换建议
     */
    @PostMapping("/substitution")
    public Result<List<Map<String, Object>>> suggestSubstitution(
            @RequestParam String dishName,
            @RequestParam String ingredient,
            @RequestParam(required = false) String reason) {
        return Result.success(aiMealPlanService.suggestSubstitutions(dishName, ingredient, reason));
    }

    /**
     * 营养分析
     */
    @PostMapping("/nutrition-analysis")
    public Result<Map<String, Object>> analyzeNutrition(@RequestBody String mealPlanJson) {
        return Result.success(aiMealPlanService.analyzeNutrition(mealPlanJson));
    }

    @GetMapping("/records")
    public Result<List<MealPlanItem>> getRecords(@RequestParam(required = false, defaultValue = "1") Long userId) {
        return Result.success(mealPlanService.getMealRecords(userId));
    }

    @PostMapping("/record")
    public Result<Void> addRecord(@RequestParam Long userId, @RequestParam Long recipeId,
                                   @RequestParam String mealType,
                                   @RequestParam(required = false) Integer rating,
                                   @RequestParam(required = false) String feedback) {
        mealPlanService.addRecord(userId, recipeId, mealType, rating, feedback);
        return Result.success("记录成功", null);
    }

    @PutMapping("/items/{itemId}/status")
    public Result<Void> updateItemStatus(@PathVariable Long itemId, @RequestParam String status) {
        mealPlanService.updateItemStatus(itemId, status);
        return Result.success("更新成功", null);
    }

    @GetMapping("/shopping-list")
    public Result<List<Map<String, Object>>> getShoppingList(
            @RequestParam(required = false, defaultValue = "1") Long userId,
            @RequestParam(required = false) String planType) {
        return Result.success(mealPlanService.generateShoppingList(userId, planType));
    }
}
