package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户会话实体类
 * 对应数据库表：user_sessions
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_sessions")
public class UserSession {

    /**
     * 会话ID(JWT Token ID)
     */
    @TableId(value = "session_id", type = IdType.INPUT)
    private String sessionId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * IP地址
     */
    @JsonIgnore
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理
     */
    @JsonIgnore
    @TableField("user_agent")
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 过期时间
     */
    @TableField("expires_at")
    private LocalDateTime expiresAt;

    /**
     * 最后访问时间
     */
    @TableField("last_accessed_at")
    private LocalDateTime lastAccessedAt;
}