package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志实体类
 * 对应数据库表：audit_logs
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("audit_logs")
public class AuditLog {

    /**
     * 日志唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 操作类型
     */
    @TableField("action")
    private String action;

    /**
     * 资源类型
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 资源ID
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 修改前值（JSON格式）
     */
    @JsonIgnore
    @TableField("old_values")
    private String oldValues;

    /**
     * 修改后值（JSON格式）
     */
    @JsonIgnore
    @TableField("new_values")
    private String newValues;

    /**
     * 操作IP
     */
    @JsonIgnore
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 操作时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}