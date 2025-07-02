package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.config.JwtUtils;
import cn.xlhealth.backend.entity.User;
import cn.xlhealth.backend.mapper.UserMapper;
import cn.xlhealth.backend.service.UserService;
import cn.xlhealth.backend.service.UserSessionService;
import cn.xlhealth.backend.ui.advice.BusinessException;
import cn.xlhealth.backend.ui.dto.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;
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
        user.setStatus(User.UserStatus.ACTIVE);
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
        if (user.getStatus() != null && user.getStatus() == User.UserStatus.SUSPENDED) {
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
            statusName = user.getStatus().name();
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

    // ========== TASK005: 用户管理接口实现 ==========

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        log.info("获取用户资料: userId={}", userId);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        return convertToUserProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        log.info("更新用户资料: userId={}, request={}", userId, request);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查邮箱是否被其他用户使用
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            User existingUser = userMapper.findByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException("邮箱已被其他用户使用");
            }
        }
        
        // 更新用户信息
        int result = userMapper.updateUserProfile(userId, request.getEmail(), request.getNickname(), request.getAvatarUrl());
        if (result == 0) {
            throw new BusinessException("更新用户资料失败");
        }
        
        log.info("用户资料更新成功: userId={}", userId);
        
        // 返回更新后的用户资料
        return getUserProfile(userId);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        log.info("修改密码: userId={}", userId);
        
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("当前密码错误");
        }
        
        // 检查新密码是否与当前密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException("新密码不能与当前密码相同");
        }
        
        // 更新密码
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
        int result = userMapper.updatePassword(userId, newPasswordHash);
        
        if (result > 0) {
            log.info("密码修改成功: userId={}", userId);
            return true;
        } else {
            throw new BusinessException("密码修改失败");
        }
    }

    @Override
    public PageResponse<UserListDTO> getUserList(Long current, Long size, String keyword, User.UserStatus status) {
        log.info("分页查询用户列表: current={}, size={}, keyword={}, status={}", current, size, keyword, status != null ? status.name() : null);
        
        // 参数校验
        if (current == null || current < 1) {
            current = 1L;
        }
        if (size == null || size < 1 || size > 100) {
            size = 10L;
        }
        
        // 创建分页对象
        Page<User> page = new Page<>(current, size);
        
        // 查询数据
        String statusStr = status != null ? status.name() : null;
        IPage<User> userPage = userMapper.selectUserPage(page, keyword, statusStr);
        
        // 转换为DTO
        List<UserListDTO> userList = userPage.getRecords().stream()
                .map(this::convertToUserListDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        PageResponse<UserListDTO> response = PageResponse.<UserListDTO>builder()
                .records(userList)
                .total(userPage.getTotal())
                .current(userPage.getCurrent())
                .size(userPage.getSize())
                .pages(userPage.getPages())
                .build();
        
        response.calculateAll();
        
        log.info("用户列表查询完成: total={}, current={}, size={}", response.getTotal(), response.getCurrent(), response.getSize());
        
        return response;
    }

    @Override
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, User.UserStatus status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);
        
        // 状态值校验
        if (status == null) {
            throw new BusinessException("用户状态不能为空");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        int result = userMapper.updateUserStatus(userId, status.name());
        if (result > 0) {
            log.info("用户状态更新成功: userId={}, status={}", userId, status);
            return true;
        } else {
            throw new BusinessException("用户状态更新失败");
        }
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        log.info("删除用户: userId={}", userId);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 软删除：将状态设置为禁用
        return updateUserStatus(userId, User.UserStatus.INACTIVE);
    }

    /**
     * 转换User实体为UserProfileDTO
     */
    private UserProfileDTO convertToUserProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .createdTime(user.getCreatedTime())
                .lastLoginTime(user.getLastLoginTime())
                .build();
    }

    /**
     * 转换User实体为UserListDTO
     */
    private UserListDTO convertToUserListDTO(User user) {
        return UserListDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .createdTime(user.getCreatedTime())
                .lastLoginTime(user.getLastLoginTime())
                .build();
    }
}