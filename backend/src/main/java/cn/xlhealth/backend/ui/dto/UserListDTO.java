package cn.xlhealth.backend.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 用户列表DTO
 * 用于用户列表展示，包含基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 账户状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 状态描述
     */
    public String getStatusDescription() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case "ACTIVE":
                return "活跃";
            case "INACTIVE":
                return "非活跃";
            case "SUSPENDED":
                return "被禁用";
            default:
                return "未知";
        }
    }
}