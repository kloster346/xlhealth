package cn.xlhealth.backend.service;

import cn.xlhealth.backend.entity.User;
import cn.xlhealth.backend.ui.dto.AuthResponse;
import cn.xlhealth.backend.ui.dto.LoginRequest;
import cn.xlhealth.backend.ui.dto.RegisterRequest;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 认证响应
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 认证响应
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * 用户登出
     * @param sessionToken 会话Token
     * @return 是否成功
     */
    boolean logout(String sessionToken);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    User findByEmail(String email);

    /**
     * 根据用户名或邮箱查找用户
     *
     * @param usernameOrEmail 用户名或邮箱
     * @return 用户实体
     */
    User findByUsernameOrEmail(String usernameOrEmail);

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
}