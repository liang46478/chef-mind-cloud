package com.chefmind.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_dietary_preferences")
public class UserDietaryPreference {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String cuisineType;

    private String taste;

    private String spicinessLevel;

    private String mealType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
