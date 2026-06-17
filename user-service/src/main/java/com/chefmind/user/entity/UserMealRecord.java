package com.chefmind.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_meal_records")
public class UserMealRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long recipeId;

    private String recipeName;

    private String mealType;

    private Integer rating;

    private String feedback;

    private LocalDate ateAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
