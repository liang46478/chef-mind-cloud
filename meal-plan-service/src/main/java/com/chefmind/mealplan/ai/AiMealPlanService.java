package com.chefmind.mealplan.ai;

import com.chefmind.mealplan.entity.PromptConfig;
import com.chefmind.mealplan.mapper.PromptConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 餐食规划服务（迁移自 ChefMind 的 AiChatService + 增强功能）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiMealPlanService {

    private final ChatClient.Builder chatClientBuilder;
    private final PromptConfigMapper promptConfigMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成一周用餐计划
     */
    public List<Map<String, Object>> generateWeeklyPlan(String preferences, String allergens, String healthGoal) {
        String systemPrompt = getPromptContent("meal-plan");
        Map<String, String> variables = new HashMap<>();
        variables.put("cuisine_preference", preferences != null ? preferences : "不限制");
        variables.put("allergens", allergens != null ? allergens : "无");
        variables.put("health_goal", healthGoal != null ? healthGoal : "均衡营养");

        String userPrompt = assemblePrompt(systemPrompt, variables);

        String jsonFormat = """
                请严格按照以下JSON格式返回数据，不要包含任何其他文本：
                [
                  {
                    "day": "周一",
                    "date": "2026-06-17",
                    "breakfast": "早餐名称",
                    "lunch": "午餐名称",
                    "dinner": "晚餐名称",
                    "notes": "营养说明"
                  }
                ]
                """;

        String response = callAi(jsonFormat, userPrompt);

        try {
            return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Failed to parse AI weekly plan response", e);
            throw new RuntimeException("AI 生成用餐计划失败: " + e.getMessage());
        }
    }

    /**
     * 根据就餐记录调整用餐计划
     */
    public List<Map<String, Object>> adjustPlanWithHistory(String preferences, String allergens,
                                                            String healthGoal, String mealHistory) {
        String systemPrompt = getPromptContent("meal-plan");
        Map<String, String> variables = new HashMap<>();
        variables.put("cuisine_preference", preferences != null ? preferences : "不限制");
        variables.put("allergens", allergens != null ? allergens : "无");
        variables.put("health_goal", healthGoal != null ? healthGoal : "均衡营养");

        String userPrompt = assemblePrompt(systemPrompt, variables);

        String historyContext = String.format(
                "用户的近期就餐记录如下：\n%s\n\n请根据这些记录调整饮食计划，" +
                "避免重复推荐用户已经吃过的菜品，增加多样性。", mealHistory);

        String jsonFormat = """
                请严格按照以下JSON格式返回数据：
                [
                  {
                    "day": "周一",
                    "date": "2026-06-17",
                    "breakfast": "早餐名称",
                    "lunch": "午餐名称",
                    "dinner": "晚餐名称",
                    "notes": "调整说明（说明与上次的不同之处）"
                  }
                ]
                """;

        String response = callAi(jsonFormat, userPrompt + "\n\n" + historyContext);

        try {
            return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Failed to parse adjusted plan response", e);
            throw new RuntimeException("AI 调整用餐计划失败: " + e.getMessage());
        }
    }

    /**
     * 生成食材替换建议
     */
    public List<Map<String, Object>> suggestSubstitutions(String dishName, String ingredient, String reason) {
        String prompt = String.format(
                "请为菜品「%s」中的食材「%s」提供替代建议，原因：%s。" +
                "请推荐3-5种可行的替代食材，并说明替换后的口味变化。",
                dishName, ingredient, reason != null ? reason : "无特殊原因");

        String jsonFormat = """
                请严格按照以下JSON格式返回数据：
                [
                  {
                    "original": "原食材",
                    "substitute": "替代食材",
                    "ratio": "替代比例",
                    "taste_change": "口味变化说明",
                    "difficulty": "难度"
                  }
                ]
                """;

        String response = callAi(jsonFormat, prompt);

        try {
            return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Failed to parse substitution response", e);
            throw new RuntimeException("AI 生成替换建议失败: " + e.getMessage());
        }
    }

    /**
     * 营养均衡分析 - 分析一日或一周的饮食营养
     */
    public Map<String, Object> analyzeNutrition(String mealPlanJson) {
        String prompt = String.format(
                "请对以下餐食计划进行营养分析：\n%s\n\n" +
                "请分析：1. 总热量估算 2. 蛋白质/碳水/脂肪比例 3. 营养均衡性评价 4. 改进建议。",
                mealPlanJson);

        String jsonFormat = """
                请严格按照以下JSON格式返回数据：
                {
                  "totalCalories": 1800,
                  "protein": "65g",
                  "carbs": "200g",
                  "fat": "50g",
                  "assessment": "均衡/偏油腻/偏清淡",
                  "suggestions": ["建议1", "建议2"]
                }
                """;

        String response = callAi(jsonFormat, prompt);

        try {
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Failed to parse nutrition analysis", e);
            throw new RuntimeException("AI 营养分析失败: " + e.getMessage());
        }
    }

    /**
     * 生成单个菜谱
     */
    public Map<String, Object> generateRecipe(String dishName, String ingredients, String cookingTime) {
        String systemPrompt = getPromptContent("recipe");
        Map<String, String> variables = new HashMap<>();
        variables.put("meal", dishName);
        variables.put("ingredients", ingredients != null ? ingredients : "无");
        variables.put("cooking_time", cookingTime != null ? cookingTime : "30分钟");

        String userPrompt = assemblePrompt(systemPrompt, variables);

        String jsonFormat = """
                请严格按照以下JSON格式返回数据，不要包含任何其他文本：
                {
                  "name": "菜名",
                  "primaryIngredients": [{"name": "食材名", "amount": "用量"}],
                  "steps": [{"name": "步骤名", "description": "详细描述", "time": "所需时间", "heat": "火力"}]
                }
                """;

        String response = callAi(jsonFormat, userPrompt);

        try {
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Failed to parse recipe response", e);
            throw new RuntimeException("AI 生成菜谱失败: " + e.getMessage());
        }
    }

    /**
     * 统一的 AI 调用方法
     */
    private String callAi(String systemMessage, String userMessage) {
        ChatClient chatClient = chatClientBuilder.build();
        String response = chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .content();

        response = cleanJsonResponse(response);
        log.debug("AI response received: {} chars", response != null ? response.length() : 0);
        return response;
    }

    private String getPromptContent(String type) {
        PromptConfig config = promptConfigMapper.selectOne(
                new LambdaQueryWrapper<PromptConfig>()
                        .eq(PromptConfig::getPromptType, type));
        if (config != null) {
            return config.getContent();
        }
        return "";
    }

    private String assemblePrompt(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private String cleanJsonResponse(String content) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }
        return content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
    }
}
