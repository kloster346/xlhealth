package cn.xlhealth.backend.integration;

import cn.xlhealth.backend.ui.dto.LoginRequest;
import cn.xlhealth.backend.ui.dto.RegisterRequest;
import cn.xlhealth.backend.ui.dto.ApiResponse;
import cn.xlhealth.backend.ui.dto.AuthResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class ProfileDebugTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void debugProfileEndpoint() throws Exception {
        // 清理测试数据
        cleanupTestData();

        // 创建测试用户
        String username = "testuser";
        String password = "TestPassword123!"; // 符合密码复杂度要求
        String email = username + "@test.com";
        String nickname = "Test User";

        // 创建注册请求对象
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setEmail(email);
        registerRequest.setConfirmPassword(password);
        registerRequest.setNickname(nickname);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        int registerStatus = registerResult.getResponse().getStatus();
        log.info("Register status: {}", registerStatus);
        log.info("Register response: {}", registerResponse);

        if (registerStatus != 200) {
            System.out.println("注册失败详细信息:");
            System.out.println("状态码: " + registerStatus);
            System.out.println("响应内容: " + registerResponse);
            System.out.println("请求内容: " + objectMapper.writeValueAsString(registerRequest));
            throw new AssertionError(
                    "Registration failed. Status: " + registerStatus + ", Response: " + registerResponse);
        }

        // 登录获取token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(username);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        int loginStatus = loginResult.getResponse().getStatus();
        log.info("Login status: {}", loginStatus);
        log.info("Login response: {}", loginResponse);

        if (loginStatus != 200) {
            throw new AssertionError("Login failed. Status: " + loginStatus + ", Response: " + loginResponse);
        }

        // 解析token
        ObjectMapper mapper = new ObjectMapper();
        JsonNode loginNode = mapper.readTree(loginResponse);
        String token = loginNode.get("data").get("accessToken").asText();
        log.info("Token: {}", token);

        // 检查会话记录
        checkSessionRecords(token);

        // 准备请求用户资料
        log.info("准备请求用户资料，token: {}", token.substring(0, Math.min(20, token.length())) + "...");

        // 验证token有效性
        log.info("验证JWT token有效性");
        // 请求用户资料
        log.info("请求用户资料");
        MvcResult profileResult = mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取用户资料成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("testuser@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("Test User"))
                .andReturn();

        log.info("用户资料获取测试通过");
    }

    private String extractTokenFromResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);
            return jsonNode.path("data").path("accessToken").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract token from response", e);
        }
    }

    private void cleanupTestData() {
        try {
            // 删除测试用户的所有session记录
            mockMvc.perform(post("/api/v1/test/cleanup")
                    .param("username", "testuser"));
            System.out.println("清理测试数据完成");
        } catch (Exception e) {
            System.out.println("清理测试数据失败: " + e.getMessage());
        }
    }

    private void checkSessionRecords(String token) {
        try {
            // 这里我们通过直接查询来检查session记录数量
            System.out.println("=== 检查Session记录 ===");
            System.out.println("Token: " + token);
            // 注意：在实际应用中，我们需要通过service或repository来查询
            // 这里只是为了调试目的
        } catch (Exception e) {
            System.out.println("检查session记录失败: " + e.getMessage());
        }
    }
}