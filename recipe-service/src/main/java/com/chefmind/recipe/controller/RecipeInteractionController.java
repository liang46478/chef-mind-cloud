package com.chefmind.recipe.controller;

import com.chefmind.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipe/interact")
@RequiredArgsConstructor
public class RecipeInteractionController {

    private final JdbcTemplate jdbc;

    // ========== 收藏 ==========
    @PostMapping("/favorite")
    public Result<Void> favorite(@RequestParam Long userId, @RequestParam Long recipeId) {
        jdbc.update("INSERT INTO recipe_favorites (user_id, recipe_id, created_at) VALUES (?, ?, NOW()) ON CONFLICT DO NOTHING", userId, recipeId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/favorite")
    public Result<Void> unfavorite(@RequestParam Long userId, @RequestParam Long recipeId) {
        jdbc.update("DELETE FROM recipe_favorites WHERE user_id=? AND recipe_id=?", userId, recipeId);
        return Result.success("取消收藏", null);
    }

    @GetMapping("/favorite/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long recipeId) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM recipe_favorites WHERE user_id=? AND recipe_id=?", Integer.class, userId, recipeId);
        return Result.success(c != null && c > 0);
    }

    @GetMapping("/favorite/count")
    public Result<Integer> favoriteCount(@RequestParam Long recipeId) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM recipe_favorites WHERE recipe_id=?", Integer.class, recipeId);
        return Result.success(c != null ? c : 0);
    }

    @GetMapping("/favorite/list")
    public Result<List<Long>> getUserFavorites(@RequestParam Long userId) {
        return Result.success(jdbc.queryForList("SELECT recipe_id FROM recipe_favorites WHERE user_id=?", Long.class, userId));
    }

    // ========== 评论 ==========
    @PostMapping("/comment")
    public Result<Void> addComment(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO recipe_comments (user_id, recipe_id, content, parent_id, created_at) VALUES (?, ?, ?, ?, NOW())",
                body.get("userId"), body.get("recipeId"), body.get("content"), body.get("parentId"));
        return Result.success("评论成功", null);
    }

    @GetMapping("/comments")
    public Result<List<Map<String, Object>>> getComments(@RequestParam Long recipeId) {
        return Result.success(jdbc.queryForList("SELECT * FROM recipe_comments WHERE recipe_id=? ORDER BY created_at DESC", recipeId));
    }
}
