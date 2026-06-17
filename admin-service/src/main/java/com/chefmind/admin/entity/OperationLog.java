package com.chefmind.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_logs")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String operator;

    private String action;

    private String resourceType;

    private Long resourceId;

    private String detail;

    private String result;

    private Long durationMs;

    private String ipAddress;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
