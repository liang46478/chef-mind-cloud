package com.chefmind.recipe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chefmind.recipe.entity.Recipe;
import com.chefmind.recipe.entity.RecipeCategory;
import com.chefmind.recipe.entity.RecipeIngredient;
import com.chefmind.recipe.entity.RecipeStep;
import com.chefmind.recipe.mapper.RecipeMapper;
import com.chefmind.recipe.mapper.RecipeCategoryMapper;
import com.chefmind.recipe.mapper.RecipeStepMapper;
import com.chefmind.recipe.mapper.RecipeIngredientMapper;
import com.chefmind.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

/**
 * 菜谱服务实现
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {

    private final RecipeMapper recipeMapper;
    private final RecipeCategoryMapper categoryMapper;
    private final RecipeStepMapper stepMapper;
    private final RecipeIngredientMapper ingredientMapper;

    @Override
    public IPage<Recipe> pageRecipes(Page<Recipe> page, String keyword, String cuisineType, String status) {
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<Recipe>()
                .like(isNotBlank(keyword), Recipe::getTitle, keyword)
                .eq(isNotBlank(cuisineType), Recipe::getCuisineType, cuisineType)
                .eq(isNotBlank(status), Recipe::getStatus, status)
                .orderByDesc(Recipe::getCreatedAt);
        return recipeMapper.selectPage(page, wrapper);
    }

    @Override
    public Recipe getRecipeDetail(Long id) {
        return recipeMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Recipe createRecipe(Recipe recipe) {
        recipeMapper.insert(recipe);
        return recipe;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Recipe updateRecipe(Recipe recipe) {
        recipeMapper.updateById(recipe);
        return recipe;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecipe(Long id) {
        // 删除关联的子表数据
        stepMapper.delete(new LambdaQueryWrapper<RecipeStep>().eq(RecipeStep::getRecipeId, id));
        ingredientMapper.delete(new LambdaQueryWrapper<RecipeIngredient>().eq(RecipeIngredient::getRecipeId, id));
        recipeMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setStatus(status);
        recipeMapper.updateById(recipe);
    }

    @Override
    public List<RecipeStep> getSteps(Long recipeId) {
        return stepMapper.selectList(
                new LambdaQueryWrapper<RecipeStep>()
                        .eq(RecipeStep::getRecipeId, recipeId)
                        .orderByAsc(RecipeStep::getStepNumber));
    }

    @Override
    public List<RecipeIngredient> getIngredients(Long recipeId) {
        return ingredientMapper.selectList(
                new LambdaQueryWrapper<RecipeIngredient>()
                        .eq(RecipeIngredient::getRecipeId, recipeId)
                        .orderByAsc(RecipeIngredient::getSortOrder));
    }

    @Override
    public List<RecipeCategory> listCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<RecipeCategory>()
                        .orderByAsc(RecipeCategory::getSortOrder));
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        return recipeMapper.selectList(
                new LambdaQueryWrapper<Recipe>()
                        .like(Recipe::getTitle, keyword)
                        .or()
                        .like(Recipe::getDescription, keyword)
                        .eq(Recipe::getStatus, "published")
                        .last("LIMIT 20"));
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
