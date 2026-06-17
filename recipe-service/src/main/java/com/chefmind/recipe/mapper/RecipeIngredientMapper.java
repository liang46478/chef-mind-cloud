package com.chefmind.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recipe.entity.RecipeIngredient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜谱-食材关联 Mapper
 */
@Mapper
public interface RecipeIngredientMapper extends BaseMapper<RecipeIngredient> {
}
