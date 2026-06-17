package com.chefmind.mealplan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("prompt_configs")
public class PromptConfig {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String promptType;

    private String content;

    private String description;

    private String variables;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
