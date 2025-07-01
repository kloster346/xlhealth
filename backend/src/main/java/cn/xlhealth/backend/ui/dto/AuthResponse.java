package cn.xlhealth.backend.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 用户信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String nickname;
        private String avatarUrl;
        private String status;
    }

    /**
     * 构造方法（不包含过期时间）
     */
    public AuthResponse(String accessToken, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.userInfo = userInfo;
    }
}