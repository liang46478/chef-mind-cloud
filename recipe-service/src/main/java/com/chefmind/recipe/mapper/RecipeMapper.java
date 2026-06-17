package com.chefmind.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recipe.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜谱 Mapper
 */
@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
}
