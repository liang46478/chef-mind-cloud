package com.chefmind.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recipe.entity.RecipeCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类 Mapper
 */
@Mapper
public interface RecipeCategoryMapper extends BaseMapper<RecipeCategory> {
}
