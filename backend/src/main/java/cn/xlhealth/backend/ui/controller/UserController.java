package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.entity.User;
import cn.xlhealth.backend.service.UserService;
import cn.xlhealth.backend.ui.dto.*;
import cn.xlhealth.backend.config.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户管理控制器
 * TASK005: 用户管理接口开发
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户资料
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCurrentUserProfile() {
        log.info("获取当前用户资料请求");
        
        Long userId = getCurrentUserId();
        UserProfileDTO profile = userService.getUserProfile(userId);
        
        return ResponseEntity.ok(ApiResponse.success("获取用户资料成功", profile));
    }

    /**
     * 获取指定用户资料
     * GET /api/users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile(@PathVariable Long userId) {
        log.info("获取用户资料请求: userId={}", userId);
        
        UserProfileDTO profile = userService.getUserProfile(userId);
        
        return ResponseEntity.ok(ApiResponse.success("获取用户资料成功", profile));
    }

    /**
     * 更新当前用户资料
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("更新当前用户资料请求: {}", request);
        
        Long userId = getCurrentUserId();
        UserProfileDTO profile = userService.updateUserProfile(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("用户资料更新成功", profile));
    }

    /**
     * 修改当前用户密码
     * PUT /api/users/password
     */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("修改密码请求");
        
        Long userId = getCurrentUserId();
        userService.changePassword(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }

    /**
     * 分页查询用户列表
     * GET /api/users/list
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<UserListDTO>>> getUserList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        log.info("分页查询用户列表请求: current={}, size={}, keyword={}, status={}", 
                current, size, keyword, status);
        
        User.UserStatus userStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                userStatus = User.UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的用户状态参数: {}", status);
            }
        }
        
        PageResponse<UserListDTO> result = userService.getUserList(current, size, keyword, userStatus);
        
        return ResponseEntity.ok(ApiResponse.success("查询用户列表成功", result));
    }

    /**
     * 更新用户状态
     * PUT /api/users/{userId}/status
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam String status) {
        log.info("更新用户状态请求: userId={}, status={}", userId, status);
        
        User.UserStatus userStatus;
        try {
            userStatus = User.UserStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的用户状态: " + status);
        }
        
        userService.updateUserStatus(userId, userStatus);
        
        return ResponseEntity.ok(ApiResponse.success("用户状态更新成功", null));
    }

    /**
     * 删除用户（软删除）
     * DELETE /api/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("删除用户请求: userId={}", userId);
        
        userService.deleteUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
        String username = authentication.getName();
        if ("anonymousUser".equals(username)) {
            throw new RuntimeException("用户未登录");
        }
        
        // 根据用户名查找用户ID
        cn.xlhealth.backend.entity.User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        return user.getId();
    }
}