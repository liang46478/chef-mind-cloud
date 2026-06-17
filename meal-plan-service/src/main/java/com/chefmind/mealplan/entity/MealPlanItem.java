package com.chefmind.mealplan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("meal_plan_items")
public class MealPlanItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long mealPlanId;

    private LocalDate date;

    private String mealType;

    private Long recipeId;

    private String recipeName;

    private Integer sortOrder;

    private String notes;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
