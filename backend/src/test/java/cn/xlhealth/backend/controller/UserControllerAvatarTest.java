package cn.xlhealth.backend.controller;

import cn.xlhealth.backend.dto.response.ApiResponse;
import cn.xlhealth.backend.service.FileStorageService;
import cn.xlhealth.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户头像上传控制器测试类
 * 
 * @author XLHealth Team
 * @version 1.0
 * @since 2024-12-19
 */
@WebMvcTest(UserController.class)
class UserControllerAvatarTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        String avatarUrl = "/uploads/avatars/1/avatar_123456789.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 模拟服务层行为
        when(fileStorageService.storeAvatar(any(), eq(userId))).thenReturn(avatarUrl);
        when(userService.updateUserAvatar(userId, avatarUrl)).thenReturn(true);

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("头像上传成功"))
                .andExpect(jsonPath("$.data.avatarUrl").value(avatarUrl));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_EmptyFile() throws Exception {
        // 准备测试数据 - 空文件
        Long userId = 1L;
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(emptyFile)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请选择要上传的文件"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_InvalidFileType() throws Exception {
        // 准备测试数据 - 无效文件类型
        Long userId = 1L;
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "pdf content".getBytes());

        // 模拟服务层抛出异常
        when(fileStorageService.storeAvatar(any(), eq(userId)))
                .thenThrow(new RuntimeException("不支持的文件类型"));

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(invalidFile)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("头像上传失败: 不支持的文件类型"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_FileTooLarge() throws Exception {
        // 准备测试数据 - 文件过大
        Long userId = 1L;
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large-image.jpg",
                "image/jpeg",
                new byte[6 * 1024 * 1024] // 6MB
        );

        // 模拟服务层抛出异常
        when(fileStorageService.storeAvatar(any(), eq(userId)))
                .thenThrow(new RuntimeException("文件大小超过限制"));

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(largeFile)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("头像上传失败: 文件大小超过限制"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_StorageFailure() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 模拟存储失败
        when(fileStorageService.storeAvatar(any(), eq(userId)))
                .thenThrow(new RuntimeException("存储失败"));

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("头像上传失败: 存储失败"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_DatabaseUpdateFailure() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        String avatarUrl = "/uploads/avatars/1/avatar_123456789.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 模拟文件存储成功但数据库更新失败
        when(fileStorageService.storeAvatar(any(), eq(userId))).thenReturn(avatarUrl);
        when(userService.updateUserAvatar(userId, avatarUrl)).thenReturn(false);

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("头像上传失败，请重试"));
    }

    @Test
    void testUploadAvatar_Unauthorized() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 执行请求并验证结果（未认证用户）
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = { "USER" })
    void testUploadAvatar_InvalidUserId() throws Exception {
        // 准备测试数据 - 无效的用户ID
        String invalidUserId = "invalid";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/v1/users/avatar")
                .file(file)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}