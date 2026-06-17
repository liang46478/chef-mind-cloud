package com.chefmind.recipe.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.common.result.Result;
import com.chefmind.recipe.entity.Recipe;
import com.chefmind.recipe.entity.RecipeCategory;
import com.chefmind.recipe.entity.RecipeStep;
import com.chefmind.recipe.entity.RecipeIngredient;
import com.chefmind.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 菜谱 REST 控制器
 */
@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/page")
    public Result<IPage<Recipe>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String status) {
        return Result.success(recipeService.pageRecipes(new Page<>(page, size), keyword, cuisineType, status));
    }

    @GetMapping("/{id}")
    public Result<Recipe> detail(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeDetail(id);
        if (recipe == null) {
            return Result.notFound("菜谱不存在");
        }
        return Result.success(recipe);
    }

    @PostMapping
    public Result<Recipe> create(@RequestBody Recipe recipe) {
        return Result.success("创建成功", recipeService.createRecipe(recipe));
    }

    @PutMapping("/{id}")
    public Result<Recipe> update(@PathVariable Long id, @RequestBody Recipe recipe) {
        recipe.setId(id);
        return Result.success("更新成功", recipeService.updateRecipe(recipe));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody String status) {
        recipeService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    @GetMapping("/{id}/steps")
    public Result<?> getSteps(@PathVariable Long id) {
        return Result.success(recipeService.getSteps(id));
    }

    @GetMapping("/{id}/ingredients")
    public Result<?> getIngredients(@PathVariable Long id) {
        return Result.success(recipeService.getIngredients(id));
    }

    @GetMapping("/categories")
    public Result<?> categories() {
        return Result.success(recipeService.listCategories());
    }

    @GetMapping("/search")
    public Result<?> search(@RequestParam String q) {
        return Result.success(recipeService.searchRecipes(q));
    }
}
