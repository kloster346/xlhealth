package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.config.JwtUtils;
import cn.xlhealth.backend.service.UserService;
import cn.xlhealth.backend.service.UserSessionService;
import cn.xlhealth.backend.ui.dto.ApiResponse;
import cn.xlhealth.backend.ui.dto.AuthResponse;
import cn.xlhealth.backend.ui.dto.LoginRequest;
import cn.xlhealth.backend.ui.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 * 处理用户注册、登录、登出等认证相关请求
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册请求: username={}", registerRequest.getUsername());
        
        AuthResponse authResponse = userService.register(registerRequest);
        
        return ResponseEntity.ok(ApiResponse.success("注册成功", authResponse));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: usernameOrEmail={}", loginRequest.getUsernameOrEmail());
        
        AuthResponse authResponse = userService.login(loginRequest);
        
        return ResponseEntity.ok(ApiResponse.success("登录成功", authResponse));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        try {
            // 从请求头中获取 JWT token
            String token = extractTokenFromRequest(request);
            
            if (token == null) {
                return ApiResponse.error("UNAUTHORIZED", "未提供认证令牌");
            }
            
            // 验证 token 格式有效性
            if (!jwtUtils.validateToken(token)) {
                return ApiResponse.error("UNAUTHORIZED", "无效的认证令牌");
            }
            
            // 检查会话状态
            String sessionStatus = userSessionService.checkSessionStatus(token);
            
            switch (sessionStatus) {
                case "NOT_FOUND":
                    return ApiResponse.error("SESSION_NOT_FOUND", "会话不存在");
                case "LOGGED_OUT":
                    return ApiResponse.error("ALREADY_LOGGED_OUT", "用户已经登出，无需重复操作");
                case "EXPIRED":
                    return ApiResponse.error("SESSION_EXPIRED", "会话已过期");
                case "VALID":
                    // 执行登出操作
                    boolean success = userSessionService.deleteSession(token);
                    if (success) {
                        String username = jwtUtils.getUsernameFromToken(token);
                        log.info("用户登出成功: username={}", username);
                        return ApiResponse.success("登出成功");
                    } else {
                        return ApiResponse.error("LOGOUT_FAILED", "登出操作失败");
                    }
                default:
                    return ApiResponse.error("UNKNOWN_STATUS", "未知的会话状态");
            }
            
        } catch (Exception e) {
            log.error("登出操作异常", e);
            return ApiResponse.error("INTERNAL_ERROR", "登出失败: " + e.getMessage());
        }
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request) {
        log.info("刷新token请求");
        
        // 从请求头中获取token
        String oldToken = extractTokenFromRequest(request);
        if (oldToken == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("缺少访问令牌"));
        }
        
        try {
            // 检查会话状态
            String sessionStatus = userSessionService.checkSessionStatus(oldToken);
            
            switch (sessionStatus) {
                case "NOT_FOUND":
                    return ResponseEntity.badRequest().body(ApiResponse.error("会话不存在"));
                case "LOGGED_OUT":
                    return ResponseEntity.badRequest().body(ApiResponse.error("会话已失效"));
                case "EXPIRED":
                    return ResponseEntity.badRequest().body(ApiResponse.error("会话已过期"));
                case "VALID":
                    break;
                default:
                    return ResponseEntity.badRequest().body(ApiResponse.error("未知的会话状态"));
            }
            
            // 验证token格式
            if (!jwtUtils.validateToken(oldToken)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无效的访问令牌"));
            }
            
            // 生成新的token
            String newToken = jwtUtils.refreshToken(oldToken);
            if (newToken == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("刷新令牌失败"));
            }
            
            // 更新数据库中的会话记录
            boolean sessionUpdated = userSessionService.refreshSession(oldToken, newToken);
            if (!sessionUpdated) {
                return ResponseEntity.badRequest().body(ApiResponse.error("更新会话失败"));
            }
            
            // 获取用户信息
            String username = jwtUtils.getUsernameFromToken(newToken);
            var user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
            
            // 构建响应
            String statusName = "UNKNOWN";
            if (user.getStatus() != null) {
                switch (user.getStatus()) {
                    case 0: statusName = "BANNED"; break;
                    case 1: statusName = "ACTIVE"; break;
                    case 2: statusName = "INACTIVE"; break;
                }
            }
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                statusName
            );
            
            // 获取JWT过期时间
            Long jwtExpiration = 86400L; // 默认24小时，可以从配置中获取
            AuthResponse authResponse = new AuthResponse(newToken, "Bearer", jwtExpiration, userInfo);
            
            return ResponseEntity.ok(ApiResponse.success("刷新成功", authResponse));
            
        } catch (Exception e) {
            log.error("刷新token失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("刷新令牌失败: " + e.getMessage()));
        }
    }

    /**
     * 验证token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(HttpServletRequest request) {
        // 从请求头中获取token
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.ok(ApiResponse.success("缺少访问令牌", false));
        }
        
        try {
            boolean isValid = jwtUtils.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success(isValid ? "令牌有效" : "令牌无效", isValid));
        } catch (Exception e) {
            log.warn("验证token失败: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success("令牌无效", false));
        }
    }

    /**
     * 从请求中提取token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}