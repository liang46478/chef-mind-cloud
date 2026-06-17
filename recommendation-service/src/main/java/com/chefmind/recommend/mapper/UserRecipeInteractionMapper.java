package com.chefmind.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.recommend.entity.UserRecipeInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRecipeInteractionMapper extends BaseMapper<UserRecipeInteraction> {

    /**
     * 获取用户-菜品评分矩阵：返回 (userId, recipeId, score) 三元组
     */
    @Select("SELECT user_id, recipe_id, " +
            "  CASE " +
            "    WHEN interaction_type = 'rate' THEN GREATEST(rating, 1) " +
            "    WHEN interaction_type = 'favorite' THEN 5.0 " +
            "    WHEN interaction_type = 'cook' THEN 4.0 " +
            "    WHEN interaction_type = 'complete' THEN 3.5 " +
            "    ELSE 1.0 " +
            "  END as score " +
            "FROM user_recipe_interactions " +
            "WHERE user_id IN (SELECT DISTINCT user_id FROM user_recipe_interactions)")
    List<Map<String, Object>> selectUserRecipeMatrix();

    /**
     * 获取用户的交互记录
     */
    @Select("SELECT recipe_id, GREATEST(MAX(" +
            "  CASE " +
            "    WHEN interaction_type = 'rate' THEN rating * 1.0 " +
            "    WHEN interaction_type = 'favorite' THEN 5.0 " +
            "    WHEN interaction_type = 'cook' THEN 4.0 " +
            "    WHEN interaction_type = 'complete' THEN 3.5 " +
            "    ELSE 1.0 END" +
            "  ), 1) as score " +
            "FROM user_recipe_interactions " +
            "WHERE user_id = #{userId} " +
            "GROUP BY recipe_id")
    List<Map<String, Object>> selectUserInteractions(@Param("userId") Long userId);

    /**
     * 获取所有有交互的用户ID
     */
    @Select("SELECT DISTINCT user_id FROM user_recipe_interactions")
    List<Long> selectAllActiveUserIds();

    /**
     * 获取所有被交互的菜品ID
     */
    @Select("SELECT DISTINCT recipe_id FROM user_recipe_interactions")
    List<Long> selectAllRecipeIds();

    /**
     * 获取菜品被交互的总次数（用于热度计算）
     */
    @Select("SELECT recipe_id, COUNT(*) as cnt FROM user_recipe_interactions GROUP BY recipe_id ORDER BY cnt DESC")
    List<Map<String, Object>> selectRecipeHotScores();

    /**
     * 获取用户偏好的菜系标签（通过已收藏/高评分的菜品）
     * 注意：此查询需要跨服务调用 recipe-service 来获取菜系信息
     * 当前简化处理，返回空列表，由上层通过 Feign 调用补齐
     */
    @Select("SELECT uri.recipe_id, MAX(uri.rating) as max_rating " +
            "FROM user_recipe_interactions uri " +
            "WHERE uri.user_id = #{userId} AND uri.rating > 0 " +
            "GROUP BY uri.recipe_id ORDER BY max_rating DESC")
    List<Map<String, Object>> selectUserHighRatedRecipes(@Param("userId") Long userId);
}
