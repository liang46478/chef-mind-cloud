package com.chefmind.admin.client;

import com.chefmind.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "recipe-service", path = "/api/recipe")
public interface RecipeServiceClient {

    @GetMapping("/page")
    Result<Map<String, Object>> pageRecipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String status);
}
