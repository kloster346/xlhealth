package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.config.JwtUtils;
import cn.xlhealth.backend.entity.User;
import cn.xlhealth.backend.mapper.UserMapper;
import cn.xlhealth.backend.service.UserService;
import cn.xlhealth.backend.service.UserSessionService;
import cn.xlhealth.backend.ui.advice.BusinessException;
import cn.xlhealth.backend.ui.dto.AuthResponse;
import cn.xlhealth.backend.ui.dto.LoginRequest;
import cn.xlhealth.backend.ui.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("用户注册请求: username={}, email={}", registerRequest.getUsername(), registerRequest.getEmail());

        // 验证密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BusinessException("密码和确认密码不匹配");
        }

        // 检查用户名是否已存在
        if (userMapper.findByUsername(registerRequest.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userMapper.findByEmail(registerRequest.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNickname(StringUtils.hasText(registerRequest.getNickname()) ? registerRequest.getNickname()
                : registerRequest.getUsername());
        user.setStatus(1); // 1表示ACTIVE状态
        // 时间字段由MyBatis-Plus自动填充，无需手动设置

        // 保存用户
        userMapper.insert(user);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());

        // 生成JWT token并创建会话
        return createAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("用户登录请求: usernameOrEmail={}", loginRequest.getUsernameOrEmail());

        // 查找用户
        User user = userMapper.findByUsernameOrEmail(loginRequest.getUsernameOrEmail());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新最后登录时间
        updateLastLoginTime(user.getId());
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        // 生成JWT token并创建会话
        return createAuthResponse(user);
    }

    @Override
    @Transactional
    public boolean logout(String sessionToken) {
        log.info("用户登出请求: sessionToken={}", sessionToken);

        boolean result = userSessionService.deleteSession(sessionToken);
        if (result) {
            log.info("用户登出成功: sessionToken={}", sessionToken);
        } else {
            log.warn("用户登出失败，会话不存在: sessionToken={}", sessionToken);
        }

        return result;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userMapper.findByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        userMapper.updateLastLoginTime(userId, LocalDateTime.now());
    }

    /**
     * 创建认证响应
     */
    private AuthResponse createAuthResponse(User user) {
        // 生成JWT token
        String token = jwtUtils.generateToken(user.getUsername());

        // 获取请求信息
        String ipAddress = getClientIpAddress();
        String userAgent = getUserAgent();

        // 创建会话，将JWT token作为session token存储
        userSessionService.createSession(user.getId(), token, ipAddress, userAgent);

        // 构建用户信息
        String statusName = "UNKNOWN";
        if (user.getStatus() != null) {
            switch (user.getStatus()) {
                case 0:
                    statusName = "BANNED";
                    break;
                case 1:
                    statusName = "ACTIVE";
                    break;
                case 2:
                    statusName = "INACTIVE";
                    break;
            }
        }
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                statusName);

        return new AuthResponse(token, "Bearer", jwtExpiration, userInfo);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (StringUtils.hasText(xForwardedFor)) {
                    return xForwardedFor.split(",")[0].trim();
                }
                String xRealIp = request.getHeader("X-Real-IP");
                if (StringUtils.hasText(xRealIp)) {
                    return xRealIp;
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.warn("获取客户端IP地址失败", e);
        }
        return "unknown";
    }

    /**
     * 获取用户代理
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.warn("获取用户代理失败", e);
        }
        return "unknown";
    }
}