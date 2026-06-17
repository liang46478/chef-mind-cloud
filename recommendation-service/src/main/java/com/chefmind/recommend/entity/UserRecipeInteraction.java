package com.chefmind.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_recipe_interactions")
public class UserRecipeInteraction {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long recipeId;

    private String interactionType;

    private Integer rating;

    private Integer durationSeconds;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
