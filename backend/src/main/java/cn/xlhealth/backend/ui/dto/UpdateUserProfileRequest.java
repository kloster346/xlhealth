package cn.xlhealth.backend.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 更新用户资料请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户昵称
     */
    @Size(min = 1, max = 50, message = "昵称长度必须在1-50个字符之间")
    private String nickname;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatarUrl;
}