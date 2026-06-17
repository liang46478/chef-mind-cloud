package com.chefmind.recipe.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeAiService {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据食材列表AI生成菜谱
     */
    public List<Map<String, Object>> generateByIngredients(List<String> ingredientNames) {
        String prompt = String.format(
            "你是一个经验丰富的大厨。请根据以下食材推荐3-5道家常菜：\n食材：%s\n\n" +
            "请确保每道菜都尽量使用这些食材。返回JSON数组，每个元素包含：\n" +
            "name(菜名), difficulty(难度:初级/中级/高级), cuisineType(菜系), " +
            "cookTime(烹饪分钟数), description(简短描述), " +
            "ingredients(食材数组,每个元素包含name和amount), " +
            "steps(步骤数组,每个元素包含stepNumber和instruction)",
            String.join("、", ingredientNames)
        );

        String jsonFormat = """
            严格按照以下JSON格式返回，不要包含其他文本：
            [
              {
                "name": "菜名",
                "difficulty": "初级",
                "cuisineType": "川菜",
                "cookTime": 30,
                "description": "简短描述",
                "ingredients": [{"name": "食材名", "amount": "用量"}],
                "steps": [{"stepNumber": 1, "instruction": "步骤描述"}]
              }
            ]
            """;

        String response = callAi(jsonFormat + "\n" + prompt);
        try {
            return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("AI解析失败: {}", e.getMessage());
            return generateFallbackRecipes(ingredientNames);
        }
    }

    /**
     * 当AI不可用时使用备用菜谱
     */
    public List<Map<String, Object>> generateFallbackRecipes(List<String> ingredients) {
        List<Map<String, Object>> recipes = new ArrayList<>();
        String first = ingredients.isEmpty() ? "食材" : ingredients.get(0);

        Map<String, Object> r1 = new HashMap<>();
        r1.put("name", first + "炒蛋");
        r1.put("difficulty", "初级");
        r1.put("cuisineType", "家常菜");
        r1.put("cookTime", 10);
        r1.put("description", "简单快手的家常菜，" + first + "搭配鸡蛋，营养美味。");
        r1.put("ingredients", List.of(Map.of("name", first, "amount", "适量"), Map.of("name", "鸡蛋", "amount", "2个")));
        r1.put("steps", List.of(
            Map.of("stepNumber", 1, "instruction", first + "洗净切好备用"),
            Map.of("stepNumber", 2, "instruction", "鸡蛋打散加盐"),
            Map.of("stepNumber", 3, "instruction", "锅中热油，先炒鸡蛋盛出"),
            Map.of("stepNumber", 4, "instruction", "再炒" + first + "，加入鸡蛋翻炒均匀即可")
        ));
        recipes.add(r1);

        if (ingredients.size() > 1) {
            Map<String, Object> r2 = new HashMap<>();
            r2.put("name", ingredients.get(0) + "炒" + ingredients.get(1));
            r2.put("difficulty", "初级");
            r2.put("cuisineType", "家常菜");
            r2.put("cookTime", 15);
            r2.put("description", "清爽可口的搭配，简单易做。");
            r2.put("ingredients", List.of(
                Map.of("name", ingredients.get(0), "amount", "适量"),
                Map.of("name", ingredients.get(1), "amount", "适量")
            ));
            r2.put("steps", List.of(
                Map.of("stepNumber", 1, "instruction", "食材洗净切好"),
                Map.of("stepNumber", 2, "instruction", "锅中热油，爆香蒜末"),
                Map.of("stepNumber", 3, "instruction", "加入食材翻炒至熟"),
                Map.of("stepNumber", 4, "instruction", "加盐调味即可出锅")
            ));
            recipes.add(r2);
        }

        Map<String, Object> r3 = new HashMap<>();
        r3.put("name", "清炒时蔬");
        r3.put("difficulty", "初级");
        r3.put("cuisineType", "家常菜");
        r3.put("cookTime", 8);
        r3.put("description", "保留食材原味，简单清淡。");
        r3.put("ingredients", List.of(Map.of("name", first, "amount", "300克"), Map.of("name", "蒜", "amount", "3瓣")));
        r3.put("steps", List.of(
            Map.of("stepNumber", 1, "instruction", "食材洗净切好"),
            Map.of("stepNumber", 2, "instruction", "热锅冷油爆香蒜末"),
            Map.of("stepNumber", 3, "instruction", "大火快炒至断生"),
            Map.of("stepNumber", 4, "instruction", "加盐调味即可")
        ));
        recipes.add(r3);
        return recipes;
    }

    private String callAi(String prompt) {
        try {
            ChatClient client = chatClientBuilder.build();
            String response = client.prompt()
                .system("你是一个专业厨师，生成家常菜谱。只返回JSON，不要任何其他文字。")
                .user(prompt)
                .call()
                .content();
            return cleanJson(response);
        } catch (Exception e) {
            log.warn("AI调用失败: {}", e.getMessage());
            throw e;
        }
    }

    private String cleanJson(String content) {
        if (content == null) return "[]";
        return content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
    }
}
