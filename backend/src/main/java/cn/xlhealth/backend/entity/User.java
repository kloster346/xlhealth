package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：users
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    /**
     * 用户唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录用）
     */
    @TableField("username")
    private String username;

    /**
     * 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 密码哈希值(BCrypt)
     * 使用@JsonIgnore隐藏敏感字段
     */
    @JsonIgnore
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 用户昵称（显示用）
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 账户状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE,     // 活跃
        INACTIVE,   // 非活跃
        BANNED      // 被禁用
    }
}