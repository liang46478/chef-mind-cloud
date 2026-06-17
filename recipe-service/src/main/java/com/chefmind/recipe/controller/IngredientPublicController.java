package com.chefmind.recipe.controller;

import com.chefmind.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientPublicController {

    private final JdbcTemplate jdbc;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listAll() {
        List<Map<String, Object>> list = jdbc.queryForList(
            "SELECT id, name, category, unit, calories_per_unit FROM ingredients ORDER BY category, name");
        return Result.success(list);
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> categories() {
        List<Map<String, Object>> list = jdbc.queryForList(
            "SELECT category, COUNT(*) as count FROM ingredients GROUP BY category ORDER BY category");
        return Result.success(list);
    }
}
