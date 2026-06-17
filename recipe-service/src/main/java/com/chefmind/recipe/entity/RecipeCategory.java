package com.chefmind.recipe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜品分类实体
 */
@Data
@TableName("recipe_categories")
public class RecipeCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    private String icon;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
