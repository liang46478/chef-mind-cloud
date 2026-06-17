package com.chefmind.recipe.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.recipe.entity.Recipe;
import com.chefmind.recipe.entity.RecipeCategory;
import com.chefmind.recipe.entity.RecipeIngredient;
import com.chefmind.recipe.entity.RecipeStep;

import java.util.List;
import java.util.Map;

public interface RecipeService {

    IPage<Recipe> pageRecipes(Page<Recipe> page, String keyword, String cuisineType, String status);
    Recipe getRecipeDetail(Long id);
    Recipe createRecipe(Recipe recipe);
    Recipe updateRecipe(Recipe recipe);
    void deleteRecipe(Long id);
    void updateStatus(Long id, String status);
    List<RecipeStep> getSteps(Long recipeId);
    List<RecipeIngredient> getIngredients(Long recipeId);
    List<RecipeCategory> listCategories();
    List<Recipe> searchRecipes(String keyword);
    List<Map<String, Object>> searchByIngredients(List<Long> ingredientIds, String matchMode);
    List<Map<String, Object>> getAllIngredients();
}
