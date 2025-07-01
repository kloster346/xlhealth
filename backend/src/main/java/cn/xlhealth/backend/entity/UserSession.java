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
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话令牌
     */
    @TableField("session_token")
    private String sessionToken;

    /**
     * 刷新令牌
     */
    @TableField("refresh_token")
    private String refreshToken;

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
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 过期时间
     */
    @TableField("expires_at")
    private LocalDateTime expiresAt;

    /**
     * 最后活动时间
     */
    @TableField("last_activity_time")
    private LocalDateTime lastAccessedAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标识
     * 0: 未删除, 1: 已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 会话状态
     * 0: 已失效, 1: 有效
     */
    @TableField("status")
    private Integer status;
}