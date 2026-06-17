package com.chefmind.recipe.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜谱实体
 */
@Data
@TableName("recipes")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String imageUrl;

    private Integer prepTime;

    private Integer cookTime;

    private String difficulty;

    private String cuisineType;

    private Integer calories;

    private Integer servings;

    private Long categoryId;

    private String status;

    private String videoUrl;

    private Long createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
