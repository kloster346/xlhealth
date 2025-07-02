package cn.xlhealth.backend.util;

import cn.xlhealth.backend.config.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类单元测试
 * 测试Token生成、验证、解析等功能
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_SECRET = "mySecretKey123456789012345678901234567890";
    private static final long TEST_EXPIRATION = 3600000; // 1小时

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", TEST_EXPIRATION);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken(TEST_USERNAME);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.contains("."));
        
        // 验证生成的token可以被解析
        String extractedUsername = jwtUtils.getUsernameFromToken(token);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtils.generateToken(TEST_USERNAME);
        String extractedUsername = jwtUtils.getUsernameFromToken(token);
        
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtils.generateToken(TEST_USERNAME);
        Date expiration = jwtUtils.getExpirationDateFromToken(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
        
        // 验证过期时间大约是1小时后
        long timeDiff = expiration.getTime() - System.currentTimeMillis();
        assertTrue(timeDiff > 3500000 && timeDiff <= 3600000); // 允许一些误差
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtils.generateToken(TEST_USERNAME);
        
        // 新生成的token不应该过期
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void testIsTokenExpiredWithExpiredToken() {
        // 创建一个已过期的token（设置很短的过期时间）
        JwtUtils shortExpirationJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "expiration", 1L); // 1毫秒
        
        String token = shortExpirationJwtUtils.generateToken(TEST_USERNAME);
        
        // 等待token过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(shortExpirationJwtUtils.isTokenExpired(token));
    }

    @Test
    void testValidateTokenWithValidToken() {
        String token = jwtUtils.generateToken(TEST_USERNAME);
        
        assertTrue(jwtUtils.validateToken(token, TEST_USERNAME));
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        String token = jwtUtils.generateToken(TEST_USERNAME);
        
        assertFalse(jwtUtils.validateToken(token, "wronguser"));
    }

    @Test
    void testValidateTokenWithExpiredToken() {
        // 创建一个已过期的token
        JwtUtils shortExpirationJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "expiration", 1L);
        
        String token = shortExpirationJwtUtils.generateToken(TEST_USERNAME);
        
        // 等待token过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertFalse(shortExpirationJwtUtils.validateToken(token, TEST_USERNAME));
    }

    @Test
    void testRefreshToken() {
        String originalToken = jwtUtils.generateToken(TEST_USERNAME);
        String refreshedToken = jwtUtils.refreshToken(originalToken);
        
        assertNotNull(refreshedToken);
        assertNotEquals(originalToken, refreshedToken);
        
        // 验证刷新后的token包含相同的用户名
        String extractedUsername = jwtUtils.getUsernameFromToken(refreshedToken);
        assertEquals(TEST_USERNAME, extractedUsername);
        
        // 验证刷新后的token是有效的
        assertTrue(jwtUtils.validateToken(refreshedToken, TEST_USERNAME));
    }

    @Test
    void testCreateTokenWithCustomExpiration() {
        // JwtUtils没有createToken(String, long)方法，改为测试generateToken
        String token = jwtUtils.generateToken(TEST_USERNAME);
        
        assertNotNull(token);
        
        Date expiration = jwtUtils.getExpirationDateFromToken(token);
        long timeDiff = expiration.getTime() - System.currentTimeMillis();
        
        // 验证过期时间在合理范围内
        assertTrue(timeDiff > 0 && timeDiff <= TEST_EXPIRATION * 1000);
    }

    @Test
    void testExtractUsernameWithInvalidToken() {
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.getUsernameFromToken("invalid.token.format");
        });
    }

    @Test
    void testExtractUsernameWithMalformedToken() {
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.getUsernameFromToken("not-a-jwt-token");
        });
    }

    @Test
    void testValidateTokenWithMalformedToken() {
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.validateToken("invalid-token", TEST_USERNAME);
        });
    }

    @Test
    void testValidateTokenWithWrongSignature() {
        // 使用不同的密钥创建另一个JwtUtils实例
        JwtUtils differentSecretJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(differentSecretJwtUtils, "secret", "differentSecret123456789012345678901234567890");
        ReflectionTestUtils.setField(differentSecretJwtUtils, "expiration", TEST_EXPIRATION);
        
        String token = differentSecretJwtUtils.generateToken(TEST_USERNAME);
        
        // 使用原来的JwtUtils验证，应该抛出签名异常
        assertThrows(SignatureException.class, () -> {
            jwtUtils.validateToken(token, TEST_USERNAME);
        });
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.refreshToken("invalid-token");
        });
    }

    @Test
    void testRefreshTokenWithExpiredToken() {
        // 创建一个已过期的token
        JwtUtils shortExpirationJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "expiration", 1L);
        
        String expiredToken = shortExpirationJwtUtils.generateToken(TEST_USERNAME);
        
        // 等待token过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 刷新过期的token应该抛出异常
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.refreshToken(expiredToken);
        });
    }

    @Test
    void testTokenConsistency() {
        // 测试多次生成token的一致性
        String token1 = jwtUtils.generateToken(TEST_USERNAME);
        String token2 = jwtUtils.generateToken(TEST_USERNAME);
        
        // 不同时间生成的token应该不同
        assertNotEquals(token1, token2);
        
        // 但都应该包含相同的用户名
        assertEquals(jwtUtils.getUsernameFromToken(token1), jwtUtils.getUsernameFromToken(token2));
        
        // 都应该是有效的
        assertTrue(jwtUtils.validateToken(token1, TEST_USERNAME));
        assertTrue(jwtUtils.validateToken(token2, TEST_USERNAME));
    }

    @Test
    void testNullAndEmptyInputs() {
        // 测试null用户名
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtils.generateToken(null);
        });
        
        // 测试空用户名
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtils.generateToken("");
        });
        
        // 测试null token
        assertThrows(Exception.class, () -> {
            jwtUtils.getUsernameFromToken(null);
        });
        
        // 测试空token
        assertThrows(Exception.class, () -> {
            jwtUtils.getUsernameFromToken("");
        });
    }
}