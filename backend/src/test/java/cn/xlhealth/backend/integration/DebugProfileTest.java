package cn.xlhealth.backend.integration;

import cn.xlhealth.backend.ui.dto.LoginRequest;
import cn.xlhealth.backend.ui.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DebugProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void debugGetUserProfile() throws Exception {
        System.out.println("=== 开始调试测试 ===");

        // 注册用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("debuguser");
        registerRequest.setPassword("Password123");
        registerRequest.setConfirmPassword("Password123");
        registerRequest.setEmail("debuguser@test.com");
        registerRequest.setNickname("Debug User");

        System.out.println("发送注册请求...");
        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        int registerStatus = registerResult.getResponse().getStatus();
        String registerResponse = registerResult.getResponse().getContentAsString();
        System.out.println("注册状态码: " + registerStatus);
        System.out.println("注册响应: " + registerResponse);

        if (registerStatus != 200 && registerStatus != 201) {
            System.out.println("注册失败，测试终止");
            return;
        }

        System.out.println("注册成功，继续测试...");

        // 解析响应获取token
        JsonNode registerJson = objectMapper.readTree(registerResponse);
        JsonNode data = registerJson.get("data");
        System.out.println("Data节点: " + data);

        String token = null;
        if (data != null) {
            JsonNode tokenNode = data.get("token");
            System.out.println("Token节点: " + tokenNode);
            if (tokenNode != null) {
                token = tokenNode.asText();
                System.out.println("提取的token: " + token);
            } else {
                System.out.println("Token节点为null");
            }
        } else {
            System.out.println("Data节点为null");
        }

        if (token == null) {
            System.out.println("无法从响应中提取token，测试终止");
            return;
        }

        System.out.println("使用token获取用户资料...");
        // 使用token获取用户资料
        MvcResult profileResult = mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", "Bearer " + token))
                .andReturn();

        String profileResponse = profileResult.getResponse().getContentAsString();
        int statusCode = profileResult.getResponse().getStatus();
        System.out.println("获取用户资料状态码: " + statusCode);
        System.out.println("获取用户资料响应: " + profileResponse);

        System.out.println("=== 调试测试结束 ===");
    }
}