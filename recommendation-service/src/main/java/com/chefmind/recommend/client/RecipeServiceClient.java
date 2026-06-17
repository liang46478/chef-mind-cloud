package com.chefmind.recommend.client;

import com.chefmind.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * 调用 recipe-service 获取菜品信息
 */
@FeignClient(name = "recipe-service", path = "/api/recipe")
public interface RecipeServiceClient {

    @GetMapping("/{id}")
    Result<Map<String, Object>> getRecipe(@PathVariable("id") Long id);

    @GetMapping("/categories")
    Result<List<Map<String, Object>>> getCategories();
}
