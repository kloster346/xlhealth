package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.entity.UserSession;
import cn.xlhealth.backend.mapper.UserSessionMapper;
import cn.xlhealth.backend.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户会话服务实现类
 */
@Slf4j
@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Autowired
    private UserSessionMapper userSessionMapper;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    @Transactional
    public UserSession createSession(Long userId, String sessionToken, String ipAddress, String userAgent) {
        log.info("创建用户会话: userId={}, sessionToken={}, ipAddress={}", userId, sessionToken, ipAddress);

        // 生成refresh token（使用UUID或其他方式）
        String refreshToken = java.util.UUID.randomUUID().toString().replace("-", "");

        // 创建会话对象
        UserSession session = new UserSession();
        session.setSessionToken(sessionToken);
        session.setRefreshToken(refreshToken);
        session.setUserId(userId);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(LocalDateTime.now().plusSeconds(jwtExpiration));
        session.setLastAccessedAt(LocalDateTime.now());
        session.setDeleted(0); // 未删除
        session.setStatus(1);  // 有效状态

        // 保存会话
        userSessionMapper.insert(session);
        log.info("用户会话创建成功: sessionToken={}, refreshToken={}", sessionToken, refreshToken);

        return session;
    }

    @Override
    public UserSession findValidSessionByToken(String sessionToken) {
        return userSessionMapper.findValidSessionByToken(sessionToken);
    }

    @Override
    public List<UserSession> findValidSessionsByUserId(Long userId) {
        return userSessionMapper.findValidSessionsByUserId(userId);
    }

    @Override
    @Transactional
    public void updateLastAccessedTime(String sessionToken) {
        userSessionMapper.updateLastAccessedTime(sessionToken);
    }

    @Override
    @Transactional
    public boolean deleteSession(String sessionToken) {
        log.info("登出用户会话: sessionToken={}", sessionToken);
        
        // 先查找会话（包括已删除的）
        UserSession session = userSessionMapper.findSessionByToken(sessionToken);
        if (session == null) {
            log.warn("用户会话不存在: sessionToken={}", sessionToken);
            return false;
        }
        
        // 检查会话是否已经被删除或失效
        if (session.getDeleted() == 1 || session.getStatus() == 0) {
            log.warn("用户会话已经失效或已登出: sessionToken={}", sessionToken);
            return false;
        }
        
        // 逻辑删除：设置deleted=1, status=0
        session.setDeleted(1);
        session.setStatus(0);
        int result = userSessionMapper.updateById(session);
        boolean success = result > 0;
        
        if (success) {
            log.info("用户会话登出成功: sessionToken={}", sessionToken);
        } else {
            log.warn("用户会话登出失败: sessionToken={}", sessionToken);
        }
        
        return success;
    }

    @Override
    @Transactional
    public int deleteAllUserSessions(Long userId) {
        log.info("删除用户所有会话: userId={}", userId);
        
        // 查询用户的所有会话
        List<UserSession> sessions = userSessionMapper.findValidSessionsByUserId(userId);
        
        int deletedCount = 0;
        for (UserSession session : sessions) {
            int result = userSessionMapper.deleteById(session.getId());
            if (result > 0) {
                deletedCount++;
            }
        }
        
        log.info("删除用户会话完成: userId={}, deletedCount={}", userId, deletedCount);
        return deletedCount;
    }

    @Override
    @Transactional
    public int cleanupExpiredSessions() {
        log.info("开始清理过期会话");
        
        int deletedCount = userSessionMapper.deleteExpiredSessions();
        
        log.info("过期会话清理完成: deletedCount={}", deletedCount);
        return deletedCount;
    }

    @Override
    public boolean isSessionValid(String sessionToken) {
        UserSession session = findValidSessionByToken(sessionToken);
        return session != null;
    }

    @Override
    public String checkSessionStatus(String sessionToken) {
        UserSession session = userSessionMapper.findSessionByToken(sessionToken);
        
        if (session == null) {
            return "NOT_FOUND";
        }
        
        // 检查是否已登出
        if (session.getDeleted() == 1 || session.getStatus() == 0) {
            return "LOGGED_OUT";
        }
        
        // 检查是否过期
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "EXPIRED";
        }
        
        return "VALID";
    }

    @Override
    @Transactional
    public boolean refreshSession(String oldSessionToken, String newSessionToken) {
        log.info("刷新会话令牌: oldToken={}, newToken={}", oldSessionToken, newSessionToken);
        
        // 计算新的过期时间
        LocalDateTime newExpiresAt = LocalDateTime.now().plusSeconds(jwtExpiration);
        
        // 直接更新会话令牌和相关信息
        int result = userSessionMapper.updateSessionToken(oldSessionToken, newSessionToken, newExpiresAt);
        boolean success = result > 0;
        
        if (success) {
            log.info("会话令牌刷新成功: newToken={}", newSessionToken);
        } else {
            log.warn("会话令牌刷新失败: oldToken={}", oldSessionToken);
        }
        
        return success;
    }
}