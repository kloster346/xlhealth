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
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 地理位置
     */
    @TableField("location")
    private String location;

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
    private LocalDateTime lastActivityTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

    /**
     * 会话状态
     */
    @TableField("status")
    private SessionStatus status;

    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        INVALID,    // 已失效
        ACTIVE,     // 活跃
        EXPIRED     // 已过期
    }
}