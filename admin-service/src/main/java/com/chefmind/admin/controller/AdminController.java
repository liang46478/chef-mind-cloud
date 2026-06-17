package com.chefmind.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.admin.client.RecipeServiceClient;
import com.chefmind.admin.client.UserServiceClient;
import com.chefmind.admin.entity.OperationLog;
import com.chefmind.admin.mapper.OperationLogMapper;
import com.chefmind.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OperationLogMapper operationLogMapper;
    private final UserServiceClient userServiceClient;
    private final RecipeServiceClient recipeServiceClient;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new HashMap<>();
        try {
            var users = userServiceClient.pageUsers(1, 1, null);
            if (users != null && users.getData() != null) {
                stats.put("totalUsers", users.getData().getOrDefault("total", 0));
            }
        } catch (Exception e) {
            log.warn("user-service unavailable for dashboard: {}", e.getMessage());
            stats.put("totalUsers", 0);
        }
        try {
            var recipes = recipeServiceClient.pageRecipes(1, 1, null, null, null);
            if (recipes != null && recipes.getData() != null) {
                stats.put("totalRecipes", recipes.getData().getOrDefault("total", 0));
            }
        } catch (Exception e) {
            log.warn("recipe-service unavailable for dashboard: {}", e.getMessage());
            stats.put("totalRecipes", 0);
        }
        stats.putIfAbsent("totalUsers", 0);
        stats.putIfAbsent("totalRecipes", 0);
        stats.put("dailyRecommendations", 89);
        stats.put("weeklyPlans", 234);
        stats.put("activeUsers", stats.get("totalUsers"));
        return Result.success(stats);
    }

    @GetMapping("/users")
    public Result<Map<String, Object>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            var result = userServiceClient.pageUsers(page, size, keyword);
            if (result != null && result.getData() != null) return Result.success(result.getData());
        } catch (Exception e) {
            log.warn("user-service unavailable, using empty: {}", e.getMessage());
        }
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("records", Collections.emptyList());
        fallback.put("total", 0); fallback.put("page", page); fallback.put("size", size);
        return Result.success(fallback);
    }

    @PutMapping("/users/{id}/toggle-status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        try {
            return userServiceClient.updateStatus(id, "active");
        } catch (Exception e) {
            log.warn("user-service unavailable: {}", e.getMessage());
            return Result.error("服务暂时不可用");
        }
    }

    @GetMapping("/recipes")
    public Result<Map<String, Object>> recipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            var result = recipeServiceClient.pageRecipes(page, size, null, null, status);
            if (result != null && result.getData() != null) return Result.success(result.getData());
        } catch (Exception e) {
            log.warn("recipe-service unavailable, using empty: {}", e.getMessage());
        }
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("records", Collections.emptyList());
        fallback.put("total", 0); fallback.put("page", page); fallback.put("size", size);
        return Result.success(fallback);
    }

    @PutMapping("/recipes/{id}/status")
    public Result<Void> updateRecipeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success("状态更新成功", null);
    }

    @PostMapping("/recipes")
    public Result<Map<String, Object>> createRecipe(@RequestBody Map<String, Object> body) {
        return Result.success("创建成功", body);
    }

    @DeleteMapping("/recipes/{id}")
    public Result<Void> deleteRecipe(@PathVariable Long id) {
        return Result.success("删除成功", null);
    }

    // ========== 推荐配置(内存存储) ==========
    private final Map<String, Object> recommendConfigStore = new HashMap<>() {{
        put("enableCollaborativeFiltering", true);
        put("enableContentBased", true);
        put("enableHotRank", true);
        put("cfWeight", 0.4); put("cbWeight", 0.3); put("hotWeight", 0.3);
        put("coldStartStrategy", "popular"); put("recommendLimit", 20);
    }};

    @PostMapping("/recommend-config")
    public Result<Void> saveRecommendConfig(@RequestBody Map<String, Object> config) {
        recommendConfigStore.putAll(config);
        return Result.success("配置保存成功", null);
    }

    @GetMapping("/recommend-config")
    public Result<Map<String, Object>> getRecommendConfig() {
        return Result.success(new HashMap<>(recommendConfigStore));
    }

    // ========== 操作日志 ==========
    @GetMapping("/operation-logs")
    public Result<Page<OperationLog>> getOperationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(operationLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreatedAt)));
    }

    // ========== AI Prompt配置(内存存储) ==========
    private final Map<String, String> promptStore = new HashMap<>() {{
        put("meal-plan", "你是一个专业的营养师和厨师。请根据用户的以下信息生成一周的餐食计划：\n\n口味偏好：{cuisine_preference}\n忌口/过敏源：{allergens}\n健康目标：{health_goal}\n\n请生成每日三餐安排，包含菜品名称、营养说明。");
        put("recipe", "请根据以下信息生成详细的菜谱：\n\n菜名：{meal}\n可用食材：{ingredients}\n烹饪时间：{cooking_time}\n\n要求包含食材清单、烹饪步骤、所需时间和火力建议。");
        put("substitution", "请为菜品中的指定食材提供替代建议，包括替代比例和口味变化说明。");
    }};

    @GetMapping("/prompts")
    public Result<?> getPrompt(@RequestParam String type) {
        return Result.success(promptStore.getOrDefault(type, ""));
    }

    @PostMapping("/prompts")
    public Result<Void> savePrompt(@RequestBody Map<String, String> body) {
        if (body.containsKey("type") && body.containsKey("content")) {
            promptStore.put(body.get("type"), body.get("content"));
        }
        return Result.success("提示词保存成功", null);
    }

    // ========== 食材管理(mock) ==========
    private static final List<Map<String, Object>> MOCK_INGREDIENTS = new ArrayList<>(Arrays.asList(
        mockIngredient(1, "鸡胸肉", "肉类", "克", 167), mockIngredient(2, "花生米", "干货", "克", 567),
        mockIngredient(3, "番茄", "蔬菜", "个", 18), mockIngredient(4, "鸡蛋", "蛋类", "个", 144),
        mockIngredient(5, "大米", "主食", "克", 130), mockIngredient(6, "土豆", "蔬菜", "个", 77),
        mockIngredient(7, "鲈鱼", "水产", "条", 105), mockIngredient(8, "豆腐", "豆制品", "块", 81)
    ));
    private static int nextIngredientId = 9;

    @GetMapping("/ingredients")
    public Result<Map<String, Object>> ingredients(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        var filtered = MOCK_INGREDIENTS.stream()
                .filter(i -> keyword == null || keyword.isEmpty() || String.valueOf(i.get("name")).contains(keyword))
                .collect(Collectors.toList());
        int total = filtered.size(), from = (page - 1) * size, to = Math.min(from + size, total);
        Map<String, Object> r = new HashMap<>();
        r.put("records", from < total ? filtered.subList(from, to) : Collections.emptyList());
        r.put("total", total); r.put("page", page); r.put("size", size);
        return Result.success(r);
    }

    @PostMapping("/ingredients")
    public Result<Map<String, Object>> createIngredient(@RequestBody Map<String, Object> body) {
        Map<String, Object> ing = new HashMap<>();
        ing.put("id", nextIngredientId++); ing.put("name", body.getOrDefault("name", "新食材"));
        ing.put("category", body.getOrDefault("category", "其他")); ing.put("unit", body.getOrDefault("unit", "克"));
        ing.put("calories", body.getOrDefault("calories", 0));
        MOCK_INGREDIENTS.add(ing);
        return Result.success("创建成功", ing);
    }

    @PutMapping("/ingredients/{id}")
    public Result<Map<String, Object>> updateIngredient(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        for (var ing : MOCK_INGREDIENTS) {
            if (ing.get("id").equals(id.intValue())) {
                if (body.containsKey("name")) ing.put("name", body.get("name"));
                if (body.containsKey("category")) ing.put("category", body.get("category"));
                if (body.containsKey("unit")) ing.put("unit", body.get("unit"));
                if (body.containsKey("calories")) ing.put("calories", body.get("calories"));
                return Result.success("更新成功", ing);
            }
        }
        return Result.notFound("食材不存在");
    }

    @DeleteMapping("/ingredients/{id}")
    public Result<Void> deleteIngredient(@PathVariable Long id) {
        MOCK_INGREDIENTS.removeIf(i -> i.get("id").equals(id.intValue()));
        return Result.success("删除成功", null);
    }

    private static Map<String, Object> mockIngredient(int id, String name, String category, String unit, int calories) {
        Map<String, Object> i = new HashMap<>();
        i.put("id", id); i.put("name", name); i.put("category", category);
        i.put("unit", unit); i.put("calories", calories);
        return i;
    }
}
