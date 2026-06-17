package com.chefmind.recipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 菜谱步骤实体
 */
@Data
@TableName("recipe_steps")
public class RecipeStep {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recipeId;

    private Integer stepNumber;

    private String instruction;

    private String imageUrl;

    private Integer timerMinutes;
}
