package com.chefmind.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recipe.entity.Ingredient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食材 Mapper
 */
@Mapper
public interface IngredientMapper extends BaseMapper<Ingredient> {
}
