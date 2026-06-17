package com.chefmind.recipe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chefmind.recipe.entity.Recipe;
import com.chefmind.recipe.entity.RecipeCategory;
import com.chefmind.recipe.entity.RecipeIngredient;
import com.chefmind.recipe.entity.RecipeStep;
import com.chefmind.recipe.entity.Ingredient;
import com.chefmind.recipe.mapper.RecipeMapper;
import com.chefmind.recipe.mapper.RecipeCategoryMapper;
import com.chefmind.recipe.mapper.RecipeStepMapper;
import com.chefmind.recipe.mapper.RecipeIngredientMapper;
import com.chefmind.recipe.mapper.IngredientMapper;
import com.chefmind.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {

    private final RecipeMapper recipeMapper;
    private final RecipeCategoryMapper categoryMapper;
    private final RecipeStepMapper stepMapper;
    private final RecipeIngredientMapper ingredientMapper;
    private final IngredientMapper simpleIngredientMapper;

    @Override
    public IPage<Recipe> pageRecipes(Page<Recipe> page, String keyword, String cuisineType, String status) {
        return recipeMapper.selectPage(page, new LambdaQueryWrapper<Recipe>()
                .like(isNotBlank(keyword), Recipe::getTitle, keyword)
                .eq(isNotBlank(cuisineType), Recipe::getCuisineType, cuisineType)
                .eq(isNotBlank(status), Recipe::getStatus, status)
                .orderByDesc(Recipe::getCreatedAt));
    }

    @Override
    public Recipe getRecipeDetail(Long id) {
        return recipeMapper.selectById(id);
    }

    @Override
    @Transactional
    public Recipe createRecipe(Recipe recipe) {
        recipeMapper.insert(recipe);
        return recipe;
    }

    @Override
    @Transactional
    public Recipe updateRecipe(Recipe recipe) {
        recipeMapper.updateById(recipe);
        return recipe;
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id) {
        stepMapper.delete(new LambdaQueryWrapper<RecipeStep>().eq(RecipeStep::getRecipeId, id));
        ingredientMapper.delete(new LambdaQueryWrapper<RecipeIngredient>().eq(RecipeIngredient::getRecipeId, id));
        recipeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        Recipe r = new Recipe(); r.setId(id); r.setStatus(status);
        recipeMapper.updateById(r);
    }

    @Override
    public List<RecipeStep> getSteps(Long recipeId) {
        return stepMapper.selectList(new LambdaQueryWrapper<RecipeStep>()
                .eq(RecipeStep::getRecipeId, recipeId).orderByAsc(RecipeStep::getStepNumber));
    }

    @Override
    public List<RecipeIngredient> getIngredients(Long recipeId) {
        return ingredientMapper.selectList(new LambdaQueryWrapper<RecipeIngredient>()
                .eq(RecipeIngredient::getRecipeId, recipeId).orderByAsc(RecipeIngredient::getSortOrder));
    }

    @Override
    public List<RecipeCategory> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<RecipeCategory>().orderByAsc(RecipeCategory::getSortOrder));
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        return recipeMapper.selectList(new LambdaQueryWrapper<Recipe>()
                .like(Recipe::getTitle, keyword).or().like(Recipe::getDescription, keyword)
                .eq(Recipe::getStatus, "published").last("LIMIT 20"));
    }

    @Override
    public List<Map<String, Object>> getAllIngredients() {
        return simpleIngredientMapper.selectMaps(new LambdaQueryWrapper<Ingredient>()
                .select(Ingredient::getId, Ingredient::getName, Ingredient::getCategory)
                .orderByAsc(Ingredient::getCategory).orderByAsc(Ingredient::getName));
    }

    @Override
    public List<Map<String, Object>> searchByIngredients(List<Long> ingredientIds, String matchMode) {
        if (ingredientIds == null || ingredientIds.isEmpty()) return List.of();
        String ids = ingredientIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Map<String, Object>> results = recipeMapper.selectByIngredientIds(ids);
        if ("all".equals(matchMode)) {
            int required = ingredientIds.size();
            results = results.stream()
                .filter(r -> ((Number) r.get("match_count")).intValue() >= required)
                .collect(Collectors.toList());
        }
        return results;
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
