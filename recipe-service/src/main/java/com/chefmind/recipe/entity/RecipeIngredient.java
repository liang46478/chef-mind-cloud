package com.chefmind.recipe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 菜谱-食材关联实体
 */
@Data
@TableName("recipe_ingredients")
public class RecipeIngredient {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recipeId;

    private Long ingredientId;

    private BigDecimal quantity;

    private String unit;

    private Boolean isSubstitutable;

    private Integer sortOrder;
}
