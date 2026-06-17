package com.chefmind.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.admin.entity.OperationLog;
import com.chefmind.admin.mapper.OperationLogMapper;
import com.chefmind.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OperationLogMapper operationLogMapper;

    // 模拟数据存储
    private static final List<Map<String, Object>> MOCK_USERS = new ArrayList<>(Arrays.asList(
        mockUser(1, "admin", "管理员", "admin@chefmind.com", "active"),
        mockUser(2, "user2024", "Chef Fan", "user@test.com", "active"),
        mockUser(3, "xiaoming", "美食家小明", "xm@test.com", "active"),
        mockUser(4, "test_user", "测试用户", "test@test.com", "disabled")
    ));

    private static final List<Map<String, Object>> MOCK_RECIPES = new ArrayList<>(Arrays.asList(
        mockRecipe(1, "宫保鸡丁", "川菜", "中级", 30, "published"),
        mockRecipe(2, "清蒸鲈鱼", "粤菜", "高级", 25, "published"),
        mockRecipe(3, "番茄炒蛋", "家常菜", "初级", 15, "draft"),
        mockRecipe(4, "红烧肉", "湘菜", "中级", 60, "published"),
        mockRecipe(5, "麻婆豆腐", "川菜", "初级", 20, "published")
    ));

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        long activeUsers = MOCK_USERS.stream().filter(u -> "active".equals(u.get("status"))).count();
        long pubRecipes = MOCK_RECIPES.stream().filter(r -> "published".equals(r.get("status"))).count();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", MOCK_USERS.size());
        stats.put("totalRecipes", MOCK_RECIPES.size());
        stats.put("dailyRecommendations", 89);
        stats.put("weeklyPlans", 234);
        stats.put("activeUsers", (int) activeUsers);
        return Result.success(stats);
    }

    @GetMapping("/users")
    public Result<Map<String, Object>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> filtered = MOCK_USERS.stream()
                .filter(u -> keyword == null || keyword.isEmpty() ||
                        String.valueOf(u.get("username")).contains(keyword) ||
                        String.valueOf(u.get("nickname")).contains(keyword))
                .collect(Collectors.toList());

        int total = filtered.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<Map<String, Object>> records = from < total ? filtered.subList(from, to) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Result.success(result);
    }

    @PutMapping("/users/{id}/toggle-status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        for (Map<String, Object> user : MOCK_USERS) {
            if (user.get("id").equals(id.intValue())) {
                String status = "active".equals(user.get("status")) ? "disabled" : "active";
                user.put("status", status);
                return Result.success("状态已切换为: " + status, null);
            }
        }
        return Result.notFound("用户不存在");
    }

    @GetMapping("/recipes")
    public Result<Map<String, Object>> recipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<Map<String, Object>> filtered = MOCK_RECIPES.stream()
                .filter(r -> status == null || status.isEmpty() || status.equals(r.get("status")))
                .collect(Collectors.toList());

        int total = filtered.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<Map<String, Object>> records = from < total ? filtered.subList(from, to) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Result.success(result);
    }

    @PutMapping("/recipes/{id}/status")
    public Result<Void> updateRecipeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        for (Map<String, Object> recipe : MOCK_RECIPES) {
            if (recipe.get("id").equals(id.intValue())) {
                recipe.put("status", status);
                return Result.success("状态更新成功", null);
            }
        }
        return Result.notFound("菜品不存在");
    }

    @PostMapping("/recipes")
    public Result<Map<String, Object>> createRecipe(@RequestBody Map<String, Object> body) {
        int newId = MOCK_RECIPES.stream().mapToInt(r -> (int) r.get("id")).max().orElse(0) + 1;
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("id", newId);
        recipe.put("title", body.getOrDefault("title", "新菜品"));
        recipe.put("category", body.getOrDefault("category", "家常菜"));
        recipe.put("difficulty", body.getOrDefault("difficulty", "初级"));
        recipe.put("cookTime", body.getOrDefault("cookTime", 30));
        recipe.put("status", "draft");
        MOCK_RECIPES.add(recipe);
        return Result.success("创建成功", recipe);
    }

    @DeleteMapping("/recipes/{id}")
    public Result<Void> deleteRecipe(@PathVariable Long id) {
        MOCK_RECIPES.removeIf(r -> r.get("id").equals(id.intValue()));
        return Result.success("删除成功", null);
    }

    // ========== 推荐配置(内存存储) ==========
    private final Map<String, Object> recommendConfigStore = new HashMap<>() {{
        put("enableCollaborativeFiltering", true);
        put("enableContentBased", true);
        put("enableHotRank", true);
        put("cfWeight", 0.4);
        put("cbWeight", 0.3);
        put("hotWeight", 0.3);
        put("coldStartStrategy", "popular");
        put("recommendLimit", 20);
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
        Page<OperationLog> result = operationLogMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreatedAt));
        return Result.success(result);
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

    // ========== 食材管理(mock数据) ==========
    private static final List<Map<String, Object>> MOCK_INGREDIENTS = new ArrayList<>(Arrays.asList(
        mockIngredient(1, "鸡胸肉", "肉类", "克", 167),
        mockIngredient(2, "花生米", "干货", "克", 567),
        mockIngredient(3, "番茄", "蔬菜", "个", 18),
        mockIngredient(4, "鸡蛋", "蛋类", "个", 144),
        mockIngredient(5, "大米", "主食", "克", 130),
        mockIngredient(6, "土豆", "蔬菜", "个", 77),
        mockIngredient(7, "鲈鱼", "水产", "条", 105),
        mockIngredient(8, "豆腐", "豆制品", "块", 81)
    ));
    private static int nextIngredientId = 9;

    @GetMapping("/ingredients")
    public Result<Map<String, Object>> ingredients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> filtered = MOCK_INGREDIENTS.stream()
                .filter(i -> keyword == null || keyword.isEmpty() ||
                        String.valueOf(i.get("name")).contains(keyword))
                .collect(Collectors.toList());
        int total = filtered.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        Map<String, Object> result = new HashMap<>();
        result.put("records", from < total ? filtered.subList(from, to) : Collections.emptyList());
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Result.success(result);
    }

    @PostMapping("/ingredients")
    public Result<Map<String, Object>> createIngredient(@RequestBody Map<String, Object> body) {
        Map<String, Object> ing = new HashMap<>();
        ing.put("id", nextIngredientId++);
        ing.put("name", body.getOrDefault("name", "新食材"));
        ing.put("category", body.getOrDefault("category", "其他"));
        ing.put("unit", body.getOrDefault("unit", "克"));
        ing.put("calories", body.getOrDefault("calories", 0));
        MOCK_INGREDIENTS.add(ing);
        return Result.success("创建成功", ing);
    }

    @PutMapping("/ingredients/{id}")
    public Result<Map<String, Object>> updateIngredient(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        for (Map<String, Object> ing : MOCK_INGREDIENTS) {
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

    private static Map<String, Object> mockUser(int id, String username, String nickname, String email, String status) {
        Map<String, Object> u = new HashMap<>();
        u.put("id", id); u.put("username", username); u.put("nickname", nickname);
        u.put("email", email); u.put("status", status);
        u.put("created", "2026-06-" + (10 + id));
        return u;
    }

    private static Map<String, Object> mockRecipe(int id, String title, String category, String difficulty, int cookTime, String status) {
        Map<String, Object> r = new HashMap<>();
        r.put("id", id); r.put("title", title); r.put("category", category);
        r.put("difficulty", difficulty); r.put("cookTime", cookTime); r.put("status", status);
        return r;
    }

    private static Map<String, Object> mockIngredient(int id, String name, String category, String unit, int calories) {
        Map<String, Object> i = new HashMap<>();
        i.put("id", id); i.put("name", name); i.put("category", category);
        i.put("unit", unit); i.put("calories", calories);
        return i;
    }
}
