package com.chefmind.recipe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食材实体
 */
@Data
@TableName("ingredients")
public class Ingredient {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String unit;

    private String category;

    private BigDecimal caloriesPerUnit;

    private String imageUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
