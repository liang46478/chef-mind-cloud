package com.chefmind.recipe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recipe.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {

    @Select(value = {
        "SELECT r.*, COUNT(ri.ingredient_id) as match_count",
        "FROM recipes r",
        "JOIN recipe_ingredients ri ON r.id = ri.recipe_id",
        "WHERE ri.ingredient_id = ANY(string_to_array(#{ids}, ',')::bigint[]) AND r.status = 'published'",
        "GROUP BY r.id",
        "ORDER BY match_count DESC"
    })
    List<Map<String, Object>> selectByIngredientIds(@Param("ids") String ids);
}
