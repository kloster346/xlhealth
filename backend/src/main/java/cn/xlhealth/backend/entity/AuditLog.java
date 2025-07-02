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
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 请求方法
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求URL
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求参数
     */
    @TableField("request_params")
    private String requestParams;

    /**
     * 响应状态
     */
    @TableField("response_status")
    private Integer responseStatus;

    /**
     * 执行时间(毫秒)
     */
    @TableField("execution_time")
    private Long executionTime;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}