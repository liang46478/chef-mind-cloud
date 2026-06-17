package com.chefmind.recipe.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.recipe.entity.Recipe;
import com.chefmind.recipe.entity.RecipeCategory;
import com.chefmind.recipe.entity.RecipeIngredient;
import com.chefmind.recipe.entity.RecipeStep;
import com.chefmind.recipe.entity.Ingredient;

import java.util.List;

/**
 * 菜谱服务接口
 */
public interface RecipeService {

    /**
     * 分页查询菜谱
     */
    IPage<Recipe> pageRecipes(Page<Recipe> page, String keyword, String cuisineType, String status);

    /**
     * 获取菜谱详情
     */
    Recipe getRecipeDetail(Long id);

    /**
     * 创建菜谱
     */
    Recipe createRecipe(Recipe recipe);

    /**
     * 更新菜谱
     */
    Recipe updateRecipe(Recipe recipe);

    /**
     * 删除菜谱
     */
    void deleteRecipe(Long id);

    /**
     * 更新菜谱状态
     */
    void updateStatus(Long id, String status);

    /**
     * 获取菜谱步骤
     */
    List<RecipeStep> getSteps(Long recipeId);

    /**
     * 获取菜谱食材
     */
    List<RecipeIngredient> getIngredients(Long recipeId);

    /**
     * 获取所有分类
     */
    List<RecipeCategory> listCategories();

    /**
     * 搜索菜谱
     */
    List<Recipe> searchRecipes(String keyword);
}
