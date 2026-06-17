package com.chefmind.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recipe.entity.RecipeStep;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜谱步骤 Mapper
 */
@Mapper
public interface RecipeStepMapper extends BaseMapper<RecipeStep> {
}
