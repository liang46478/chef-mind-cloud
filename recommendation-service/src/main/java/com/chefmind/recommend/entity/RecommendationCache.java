package com.chefmind.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("recommendation_cache")
public class RecommendationCache {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String recipeIds;

    private String strategy;

    private LocalDateTime expiredAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
