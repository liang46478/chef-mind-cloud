package com.chefmind.recipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 菜谱标签实体
 */
@Data
@TableName("recipe_tags")
public class RecipeTag {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
}
