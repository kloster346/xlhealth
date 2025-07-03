package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.config.JwtUtils;
import cn.xlhealth.backend.service.UserService;
import cn.xlhealth.backend.service.UserSessionService;
import cn.xlhealth.backend.common.ErrorCode;
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
import org.springframework.http.HttpStatus;

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
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            // 从请求头中获取 JWT token
            String token = extractTokenFromRequest(request);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "未提供认证令牌"));
            }

            // 验证 token 格式有效性
            if (!jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "无效的认证令牌"));
            }

            // 检查会话状态
            String sessionStatus = userSessionService.checkSessionStatus(token);

            switch (sessionStatus) {
                case "NOT_FOUND":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(ErrorCode.NOT_FOUND, "会话不存在"));
                case "LOGGED_OUT":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error(ErrorCode.BAD_REQUEST, "用户已经登出，无需重复操作"));
                case "EXPIRED":
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "会话已过期"));
                case "VALID":
                    // 执行登出操作
                    boolean success = userSessionService.deleteSession(token);
                    if (success) {
                        String userIdStr = jwtUtils.getUserIdFromToken(token);
                        log.info("用户登出成功: userId={}", userIdStr);
                        return ResponseEntity.ok(ApiResponse.success("登出成功"));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "登出操作失败"));
                    }
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "未知的会话状态"));
            }

        } catch (Exception e) {
            log.error("登出操作异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "登出失败: " + e.getMessage()));
        }
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request) {
        log.info("刷新token请求");

        try {
            // 从请求头中获取token
            String oldToken = extractTokenFromRequest(request);
            if (oldToken == null || oldToken.trim().isEmpty()) {
                log.warn("刷新token请求缺少访问令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "缺少访问令牌"));
            }

            log.debug("提取到的token: {}", oldToken.substring(0, Math.min(oldToken.length(), 20)) + "...");

            // 首先验证token格式和有效性
            if (!jwtUtils.validateToken(oldToken)) {
                log.warn("刷新token失败: token格式无效");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "无效的访问令牌"));
            }

            // 检查会话状态
            String sessionStatus = userSessionService.checkSessionStatus(oldToken);
            log.debug("会话状态检查结果: {}", sessionStatus);

            switch (sessionStatus) {
                case "NOT_FOUND":
                    log.warn("刷新token失败: 会话不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(ErrorCode.NOT_FOUND, "会话不存在"));
                case "LOGGED_OUT":
                    log.warn("刷新token失败: 会话已失效");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "会话已失效"));
                case "EXPIRED":
                    log.warn("刷新token失败: 会话已过期");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "会话已过期"));
                case "VALID":
                    break;
                default:
                    log.warn("刷新token失败: 未知的会话状态 - {}", sessionStatus);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "未知的会话状态"));
            }

            // 生成新的token
            String newToken = jwtUtils.refreshToken(oldToken);
            if (newToken == null || newToken.trim().isEmpty()) {
                log.error("刷新token失败: 无法生成新token");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "刷新令牌失败"));
            }

            log.debug("生成新token成功: {}", newToken.substring(0, Math.min(newToken.length(), 20)) + "...");

            // 更新数据库中的会话记录
            boolean sessionUpdated = userSessionService.refreshSession(oldToken, newToken);
            if (!sessionUpdated) {
                log.error("刷新token失败: 更新会话记录失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "更新会话失败"));
            }

            // 获取用户信息
            String userIdStr = jwtUtils.getUserIdFromToken(newToken);
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                log.error("刷新token失败: 无法从新token中提取用户ID");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "无法提取用户信息"));
            }

            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.error("刷新token失败: 用户ID格式无效 - {}", userIdStr);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(ErrorCode.BAD_REQUEST, "用户ID格式无效"));
            }

            var user = userService.findById(userId);
            if (user == null) {
                log.error("刷新token失败: 用户不存在 - {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(ErrorCode.NOT_FOUND, "用户不存在"));
            }

            // 构建响应
            String statusName = "UNKNOWN";
            if (user.getStatus() != null) {
                switch (user.getStatus()) {
                    case ACTIVE:
                        statusName = "ACTIVE";
                        break;
                    case INACTIVE:
                        statusName = "INACTIVE";
                        break;
                    case SUSPENDED:
                        statusName = "SUSPENDED";
                        break;
                    case DELETED:
                        statusName = "DELETED";
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

            // 获取JWT过期时间
            Long jwtExpiration = 86400L; // 默认24小时，可以从配置中获取
            AuthResponse authResponse = new AuthResponse(newToken, "Bearer", jwtExpiration, userInfo);

            log.info("刷新token成功: userId={}", userId);
            return ResponseEntity.ok(ApiResponse.success("刷新成功", authResponse));

        } catch (io.jsonwebtoken.JwtException e) {
            log.error("刷新token失败: JWT异常", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "令牌处理失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("刷新token失败: 未知异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "刷新令牌失败: " + e.getMessage()));
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