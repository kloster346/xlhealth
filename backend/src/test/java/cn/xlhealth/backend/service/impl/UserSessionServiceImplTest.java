package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.mapper.UserSessionMapper;
import cn.xlhealth.backend.entity.UserSession;
import cn.xlhealth.backend.config.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户会话服务实现类单元测试
 * 测试会话的创建、查找、更新、删除、验证等功能
 */
@ExtendWith(MockitoExtension.class)
class UserSessionServiceImplTest {

    @Mock
    private UserSessionMapper userSessionMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserSessionServiceImpl userSessionService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_ACCESS_TOKEN = "test.access.token";
    private static final String TEST_REFRESH_TOKEN = "test.refresh.token";
    private static final String TEST_NEW_ACCESS_TOKEN = "new.access.token";
    private static final String TEST_NEW_REFRESH_TOKEN = "new.refresh.token";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_IP_ADDRESS = "192.168.1.1";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";

    private UserSession testSession;

    @BeforeEach
    void setUp() {
        // 设置JWT过期时间
        ReflectionTestUtils.setField(userSessionService, "jwtExpiration", 3600L);

        testSession = new UserSession();
        testSession.setId(1L);
        testSession.setUserId(TEST_USER_ID);
        testSession.setSessionToken(TEST_ACCESS_TOKEN);
        testSession.setRefreshToken(TEST_REFRESH_TOKEN);
        testSession.setIpAddress(TEST_IP_ADDRESS);
        testSession.setUserAgent(TEST_USER_AGENT);
        testSession.setLastActivityTime(LocalDateTime.now());
        testSession.setExpiresAt(LocalDateTime.now().plusHours(1));
        testSession.setStatus(UserSession.SessionStatus.ACTIVE.getValue());
        testSession.setDeleted(false);
    }

    @Test
    void testCreateSession() {
        when(userSessionMapper.insert(any(UserSession.class))).thenReturn(1);

        UserSession result = userSessionService.createSession(1L, TEST_ACCESS_TOKEN, TEST_IP_ADDRESS, TEST_USER_AGENT);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(TEST_ACCESS_TOKEN, result.getSessionToken());
        assertEquals(TEST_IP_ADDRESS, result.getIpAddress());
        assertEquals(TEST_USER_AGENT, result.getUserAgent());
        assertEquals(UserSession.SessionStatus.ACTIVE.getValue(), result.getStatus());
        assertNotNull(result.getLastActivityTime());
        assertNotNull(result.getExpiresAt());
        assertFalse(result.getDeleted());

        verify(userSessionMapper).insert(any(UserSession.class));
    }

    @Test
    void testFindValidSessionByToken() {
        when(userSessionMapper.findValidSessionByToken(TEST_ACCESS_TOKEN)).thenReturn(testSession);

        UserSession result = userSessionService.findValidSessionByToken(TEST_ACCESS_TOKEN);

        assertNotNull(result);
        assertEquals(testSession.getId(), result.getId());
        assertEquals(testSession.getUserId(), result.getUserId());
        assertEquals(testSession.getSessionToken(), result.getSessionToken());

        verify(userSessionMapper).findValidSessionByToken(TEST_ACCESS_TOKEN);
    }

    @Test
    void testFindValidSessionsByUserId() {
        List<UserSession> sessions = Arrays.asList(testSession);
        when(userSessionMapper.findValidSessionsByUserId(TEST_USER_ID)).thenReturn(sessions);

        List<UserSession> result = userSessionService.findValidSessionsByUserId(TEST_USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSession.getId(), result.get(0).getId());

        verify(userSessionMapper).findValidSessionsByUserId(TEST_USER_ID);
    }

    @Test
    void testUpdateLastAccessedTime() {
        when(userSessionMapper.updateLastAccessedTime(TEST_ACCESS_TOKEN)).thenReturn(1);

        userSessionService.updateLastAccessedTime(TEST_ACCESS_TOKEN);

        verify(userSessionMapper).updateLastAccessedTime(TEST_ACCESS_TOKEN);
    }

    @Test
    void testDeleteSessionByToken() {
        when(userSessionMapper.findSessionByToken(TEST_ACCESS_TOKEN)).thenReturn(testSession);
        when(userSessionMapper.updateById(any(UserSession.class))).thenReturn(1);

        boolean result = userSessionService.deleteSession(TEST_ACCESS_TOKEN);

        assertTrue(result);
        verify(userSessionMapper).findSessionByToken(TEST_ACCESS_TOKEN);
        verify(userSessionMapper).updateById(any(UserSession.class));
    }

    @Test
    void testDeleteSessionByTokenFailed() {
        when(userSessionMapper.findSessionByToken(TEST_ACCESS_TOKEN)).thenReturn(testSession);
        when(userSessionMapper.updateById(any(UserSession.class))).thenReturn(0);

        boolean result = userSessionService.deleteSession(TEST_ACCESS_TOKEN);

        assertFalse(result);
        verify(userSessionMapper).findSessionByToken(TEST_ACCESS_TOKEN);
        verify(userSessionMapper).updateById(any(UserSession.class));
    }

    @Test
    void testCleanupExpiredSessions() {
        when(userSessionMapper.deleteExpiredSessions(any(LocalDateTime.class))).thenReturn(5);

        int result = userSessionService.cleanupExpiredSessions();

        assertEquals(5, result);
        verify(userSessionMapper).deleteExpiredSessions(any(LocalDateTime.class));
    }

    @Test
    void testIsSessionValidWithValidSession() {
        when(userSessionMapper.findValidSessionByToken(TEST_ACCESS_TOKEN)).thenReturn(testSession);

        boolean result = userSessionService.isSessionValid(TEST_ACCESS_TOKEN);

        assertTrue(result);
        verify(userSessionMapper).findValidSessionByToken(TEST_ACCESS_TOKEN);
    }

    @Test
    void testIsSessionValidWithNonExistentSession() {
        when(userSessionMapper.findValidSessionByToken(TEST_ACCESS_TOKEN)).thenReturn(null);

        boolean result = userSessionService.isSessionValid(TEST_ACCESS_TOKEN);

        assertFalse(result);
        verify(userSessionMapper).findValidSessionByToken(TEST_ACCESS_TOKEN);
    }

    @Test
    void testRefreshSession() {
        when(userSessionMapper.updateSessionToken(eq(TEST_ACCESS_TOKEN), eq(TEST_NEW_ACCESS_TOKEN),
                any(LocalDateTime.class))).thenReturn(1);

        boolean result = userSessionService.refreshSession(TEST_ACCESS_TOKEN, TEST_NEW_ACCESS_TOKEN);

        assertTrue(result);
        verify(userSessionMapper).updateSessionToken(eq(TEST_ACCESS_TOKEN), eq(TEST_NEW_ACCESS_TOKEN),
                any(LocalDateTime.class));
    }

}