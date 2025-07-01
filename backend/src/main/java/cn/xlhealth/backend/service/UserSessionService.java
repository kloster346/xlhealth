package cn.xlhealth.backend.service;

import cn.xlhealth.backend.entity.UserSession;

import java.util.List;

/**
 * 用户会话服务接口
 */
public interface UserSessionService {

    /**
     * 创建用户会话
     *
     * @param userId 用户ID
     * @param sessionToken 会话令牌（JWT Token）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 用户会话
     */
    UserSession createSession(Long userId, String sessionToken, String ipAddress, String userAgent);

    /**
     * 根据会话令牌查找有效会话
     *
     * @param sessionToken 会话令牌
     * @return 用户会话
     */
    UserSession findValidSessionByToken(String sessionToken);

    /**
     * 根据用户ID查找所有有效会话
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<UserSession> findValidSessionsByUserId(Long userId);

    /**
     * 更新会话最后活动时间
     *
     * @param sessionToken 会话令牌
     */
    void updateLastAccessedTime(String sessionToken);

    /**
     * 删除会话（登出）
     *
     * @param sessionToken 会话令牌
     * @return 是否成功
     */
    boolean deleteSession(String sessionToken);

    /**
     * 删除用户的所有会话
     *
     * @param userId 用户ID
     * @return 删除的会话数量
     */
    int deleteAllUserSessions(Long userId);

    /**
     * 清理过期会话
     *
     * @return 清理的会话数量
     */
    int cleanupExpiredSessions();

    /**
     * 验证会话是否有效
     *
     * @param sessionToken 会话令牌
     * @return 是否有效
     */
    boolean isSessionValid(String sessionToken);

    /**
     * 检查会话状态
     *
     * @param sessionToken 会话令牌
     * @return 会话状态："VALID" - 有效, "EXPIRED" - 已过期, "LOGGED_OUT" - 已登出, "NOT_FOUND" - 不存在
     */
    String checkSessionStatus(String sessionToken);

    /**
     * 刷新会话令牌
     *
     * @param oldSessionToken 旧的会话令牌
     * @param newSessionToken 新的会话令牌
     * @return 是否成功
     */
    boolean refreshSession(String oldSessionToken, String newSessionToken);
}