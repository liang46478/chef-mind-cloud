package com.chefmind.mealplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chefmind.mealplan.ai.AiMealPlanService;
import com.chefmind.mealplan.entity.MealPlan;
import com.chefmind.mealplan.entity.MealPlanItem;
import com.chefmind.mealplan.mapper.MealPlanMapper;
import com.chefmind.mealplan.mapper.MealPlanItemMapper;
import com.chefmind.mealplan.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {

    private final MealPlanMapper mealPlanMapper;
    private final MealPlanItemMapper mealPlanItemMapper;
    private final AiMealPlanService aiMealPlanService;

    @Override
    public MealPlan getCurrentPlan(Long userId, String planType) {
        LocalDate today = LocalDate.now();
        return mealPlanMapper.selectOne(
                new LambdaQueryWrapper<MealPlan>()
                        .eq(MealPlan::getUserId, userId)
                        .eq(MealPlan::getPlanType, planType != null ? planType : "weekly")
                        .le(MealPlan::getStartDate, today)
                        .ge(MealPlan::getEndDate, today)
                        .orderByDesc(MealPlan::getCreatedAt)
                        .last("LIMIT 1"));
    }

    @Override
    public List<MealPlanItem> getPlanItems(Long planId) {
        return mealPlanItemMapper.selectList(
                new LambdaQueryWrapper<MealPlanItem>()
                        .eq(MealPlanItem::getMealPlanId, planId)
                        .orderByAsc(MealPlanItem::getDate)
                        .orderByAsc(MealPlanItem::getSortOrder));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealPlan generatePlan(Long userId, String preferences, String allergens, String healthGoal) {
        List<Map<String, Object>> aiPlan;
        try {
            aiPlan = aiMealPlanService.generateWeeklyPlan(preferences, allergens, healthGoal);
        } catch (Exception e) {
            log.warn("AI meal plan generation failed, using fallback data: {}", e.getMessage());
            aiPlan = generateFallbackPlan();
        }

        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(6);

        MealPlan plan = new MealPlan();
        plan.setUserId(userId);
        plan.setName("本周餐食计划");
        plan.setStartDate(today);
        plan.setEndDate(weekEnd);
        plan.setPlanType("weekly");
        plan.setStatus("active");
        mealPlanMapper.insert(plan);

        int sortOrder = 0;
        for (Map<String, Object> dayMeal : aiPlan) {
            String[] meals = {"breakfast", "lunch", "dinner"};
            for (String meal : meals) {
                Object mealValue = dayMeal.get(meal);
                if (mealValue != null) {
                    MealPlanItem item = new MealPlanItem();
                    item.setMealPlanId(plan.getId());
                    item.setDate(LocalDate.parse(String.valueOf(dayMeal.get("date"))));
                    item.setMealType(meal);
                    item.setRecipeName(String.valueOf(mealValue));
                    item.setSortOrder(sortOrder++);
                    item.setStatus("pending");
                    mealPlanItemMapper.insert(item);
                }
            }
        }

        return plan;
    }

    @Override
    public void addRecord(Long userId, Long recipeId, String mealType, Integer rating, String feedback) {
        // 记录用户就餐（简化版 - 完整版需创建独立的食事记录表）
        MealPlanItem item = new MealPlanItem();
        item.setRecipeId(recipeId);
        item.setStatus("completed");
        mealPlanItemMapper.update(item,
                new LambdaQueryWrapper<MealPlanItem>()
                        .eq(MealPlanItem::getRecipeId, recipeId)
                        .eq(MealPlanItem::getMealType, mealType)
                        .eq(MealPlanItem::getStatus, "pending")
                        .last("LIMIT 1"));
    }

    @Override
    public List<MealPlanItem> getMealRecords(Long userId) {
        return mealPlanItemMapper.selectList(
                new LambdaQueryWrapper<MealPlanItem>()
                        .eq(MealPlanItem::getStatus, "completed")
                        .orderByDesc(MealPlanItem::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealPlan createPlan(MealPlan plan) {
        mealPlanMapper.insert(plan);
        return plan;
    }

    @Override
    public void updateItemStatus(Long itemId, String status) {
        MealPlanItem item = new MealPlanItem();
        item.setId(itemId);
        item.setStatus(status);
        mealPlanItemMapper.updateById(item);
    }

    @Override
    public List<Map<String, Object>> generateShoppingList(Long userId, String planType) {
        MealPlan plan = getCurrentPlan(userId, planType);
        Map<String, List<String>> categoryItems = new LinkedHashMap<>();

        if (plan != null) {
            List<MealPlanItem> items = getPlanItems(plan.getId());
            for (MealPlanItem item : items) {
                String name = item.getRecipeName();
                if (name == null) continue;
                if (name.contains("鸡") || name.contains("鸭")) {
                    categoryItems.computeIfAbsent("肉类", k -> new ArrayList<>());
                    addIfNotContains(categoryItems.get("肉类"), name.contains("鸡") ? "鸡肉" : "鸭肉");
                }
                if (name.contains("鱼") || name.contains("虾")) {
                    categoryItems.computeIfAbsent("水产", k -> new ArrayList<>());
                    addIfNotContains(categoryItems.get("水产"), "鱼/虾");
                }
                if (name.contains("蛋")) {
                    categoryItems.computeIfAbsent("蛋类", k -> new ArrayList<>());
                    addIfNotContains(categoryItems.get("蛋类"), "鸡蛋");
                }
                if (name.contains("豆腐")) {
                    categoryItems.computeIfAbsent("豆制品", k -> new ArrayList<>());
                    addIfNotContains(categoryItems.get("豆制品"), "豆腐");
                }
                if (name.contains("米") || name.contains("饭")) {
                    categoryItems.computeIfAbsent("主食", k -> new ArrayList<>());
                    addIfNotContains(categoryItems.get("主食"), "大米");
                }
                if (name.contains("菜") || name.contains("蔬")) {
                    categoryItems.computeIfAbsent("蔬菜", k -> new ArrayList<>());
                    addIfNotContains(categoryItems.get("蔬菜"), "时令蔬菜");
                }
            }
            categoryItems.computeIfAbsent("调味料", k -> new ArrayList<>());
            addIfNotContains(categoryItems.get("调味料"), "盐、油、酱油");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : categoryItems.entrySet()) {
            Map<String, Object> group = new HashMap<>();
            group.put("category", entry.getKey());
            group.put("items", entry.getValue());
            result.add(group);
        }
        return result;
    }

    private void addIfNotContains(List<String> list, String item) {
        if (!list.contains(item)) list.add(item);
    }

    private List<Map<String, Object>> generateFallbackPlan() {
        List<Map<String, Object>> plan = new ArrayList<>();
        String[] days = {"周一","周二","周三","周四","周五","周六","周日"};
        LocalDate today = LocalDate.now();
        String[][] meals = {
            {"小米粥+鸡蛋","全麦面包+牛奶","豆浆+包子","燕麦片+水果","皮蛋瘦肉粥","煎蛋+吐司","豆腐脑+油条"},
            {"宫保鸡丁+米饭","番茄炒蛋+米饭","红烧肉+米饭","清蒸鲈鱼+米饭","麻婆豆腐+米饭","糖醋里脊+米饭","鱼香肉丝+米饭"},
            {"清炒时蔬+杂粮饭","凉拌黄瓜+粥","紫菜蛋花汤+馒头","西红柿面","青椒炒肉+米饭","清炖排骨+米饭","素炒青菜+粥"}
        };
        for (int i = 0; i < 7; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("day", days[i]);
            day.put("date", today.plusDays(i).toString());
            day.put("breakfast", meals[0][i]);
            day.put("lunch", meals[1][i]);
            day.put("dinner", meals[2][i]);
            day.put("notes", "营养均衡");
            plan.add(day);
        }
        return plan;
    }
}
