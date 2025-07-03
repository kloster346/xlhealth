package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.service.UserService;
import cn.xlhealth.backend.service.FileStorageService;
import cn.xlhealth.backend.ui.dto.*;
import cn.xlhealth.backend.ui.advice.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器
 * TASK005: 用户管理接口开发
 * 根据TASK005要求，只实现基本的用户资料管理功能
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 获取当前用户资料
     * GET /api/v1/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCurrentUserProfile() {
        log.info("获取当前用户资料请求");

        Long userId = getCurrentUserId();
        UserProfileDTO profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(ApiResponse.success("获取用户资料成功", profile));
    }

    /**
     * 更新当前用户资料
     * PUT /api/v1/users/profile
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
     * PUT /api/v1/users/password
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
     * 上传用户头像
     * POST /api/v1/users/avatar
     */
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        log.info("上传用户头像请求，文件名: {}, 文件大小: {} bytes",
                file.getOriginalFilename(), file.getSize());

        Long userId = getCurrentUserId();

        // 存储头像文件
        String avatarUrl = fileStorageService.storeAvatar(file, userId);

        // 更新用户头像URL
        userService.updateUserAvatar(userId, avatarUrl);

        log.info("用户头像上传成功，URL: {}", avatarUrl);
        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", avatarUrl);
        return ResponseEntity.ok(ApiResponse.success("头像上传成功", result));
    }

    /**
     * 获取当前登录用户ID
     * 注意：JWT验证后，authentication.getName()直接返回用户ID字符串
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.unauthorized("用户未登录");
        }

        String userIdStr = authentication.getName();
        if ("anonymousUser".equals(userIdStr)) {
            throw BusinessException.unauthorized("用户未登录");
        }

        try {
            // 直接将用户ID字符串转换为Long类型
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.error("无效的用户ID格式: {}", userIdStr, e);
            throw BusinessException.unauthorized("用户认证信息异常");
        }
    }
}