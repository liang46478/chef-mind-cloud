package com.chefmind.mealplan.service;

import com.chefmind.mealplan.entity.MealPlan;
import com.chefmind.mealplan.entity.MealPlanItem;

import java.util.List;

public interface MealPlanService {

    MealPlan getCurrentPlan(Long userId, String planType);

    List<MealPlanItem> getPlanItems(Long planId);

    MealPlan generatePlan(Long userId, String preferences, String allergens, String healthGoal);

    void addRecord(Long userId, Long recipeId, String mealType, Integer rating, String feedback);

    List<MealPlanItem> getMealRecords(Long userId);

    MealPlan createPlan(MealPlan plan);

    void updateItemStatus(Long itemId, String status);
}
