package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.service.ai.AIServiceManager;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.monitor.ServiceStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AI控制器测试类
 * 测试AI服务管理相关的REST API接口功能
 * 注意：此控制器专注于AI服务本身，不涉及消息存储
 * 消息管理功能请参考MessageControllerTest
 */
@WebMvcTest(controllers = AIController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        cn.xlhealth.backend.ui.interceptor.JwtRequestFilter.class,
        cn.xlhealth.backend.ui.interceptor.JwtAuthenticationEntryPoint.class,
        cn.xlhealth.backend.config.SecurityConfig.class
}))
public class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIServiceManager aiServiceManager;

    @Autowired
    private ObjectMapper objectMapper;

    private AIResponse mockSuccessResponse;
    private AIResponse mockFailureResponse;
    private Map<String, ServiceStats> mockStats;
    private Map<String, Object> mockConfig;

    @BeforeEach
    public void setUp() {
        // 准备成功响应
        mockSuccessResponse = AIResponse.success(
                "这是一个模拟的AI回复，我理解您的感受。",
                "MOCK",
                "EMOTIONAL_SUPPORT");
        mockSuccessResponse.setQualityScore(85);
        mockSuccessResponse.setResponseTime(1200L);
        mockSuccessResponse.setTimestamp(System.currentTimeMillis());

        // 准备失败响应
        mockFailureResponse = AIResponse.failure(
                "处理请求时发生错误",
                "MOCK");

        // 准备统计数据
        mockStats = new HashMap<>();
        ServiceStats stats = new ServiceStats("MOCK");
        stats.recordSuccess(1000L, 85);
        stats.recordSuccess(1200L, 90);
        mockStats.put("MOCK", stats);

        // 准备配置信息
        mockConfig = new HashMap<>();
        mockConfig.put("mockMode", true);
        mockConfig.put("provider", "MOCK");
        mockConfig.put("contextEnabled", true);
        mockConfig.put("qualityEnabled", true);
        mockConfig.put("monitoringEnabled", true);
    }

    @Test
    public void testChatEndpoint_Success() throws Exception {
        // 模拟成功响应
        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockSuccessResponse);

        // 准备请求数据
        AIController.ChatRequest chatRequest = new AIController.ChatRequest();
        chatRequest.setConversationId("test-conv-001");
        chatRequest.setMessage("我感到很焦虑");
        chatRequest.setEmotionalState("焦虑");

        // 执行请求并验证响应
        mockMvc.perform(post("/api/ai/chat")
                .header("X-User-Id", "test-user-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.content").value(mockSuccessResponse.getContent()))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.provider").value("EMOTIONAL_SUPPORT"))
                .andExpect(jsonPath("$.data.qualityScore").value(85))
                .andExpect(jsonPath("$.data.replyType").value("MOCK"));
    }

    @Test
    public void testChatEndpoint_Failure() throws Exception {
        // 模拟失败响应
        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockFailureResponse);

        // 准备请求数据
        AIController.ChatRequest chatRequest = new AIController.ChatRequest();
        chatRequest.setConversationId("test-conv-002");
        chatRequest.setMessage("测试消息");
        chatRequest.setEmotionalState("中性");

        // 执行请求并验证响应
        mockMvc.perform(post("/api/ai/chat")
                .header("X-User-Id", "test-user-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(99999))
                .andExpect(jsonPath("$.message").value("处理请求时发生错误"));
    }

    @Test
    public void testChatEndpoint_MissingUserId() throws Exception {
        // 模拟成功响应
        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockSuccessResponse);

        // 准备请求数据
        AIController.ChatRequest chatRequest = new AIController.ChatRequest();
        chatRequest.setConversationId("test-conv-003");
        chatRequest.setMessage("测试消息");
        chatRequest.setEmotionalState("中性");

        // 执行请求并验证响应（缺少User-Id头，应自动生成临时ID）
        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    public void testChatEndpoint_InvalidRequest() throws Exception {
        // 模拟成功响应
        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockSuccessResponse);

        // 准备无效请求数据（缺少必要字段）
        AIController.ChatRequest chatRequest = new AIController.ChatRequest();
        // 不设置任何字段

        // 执行请求并验证响应（现在会自动处理空字段）
        mockMvc.perform(post("/api/ai/chat")
                .header("X-User-Id", "test-user-004")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    public void testHealthEndpoint_Healthy() throws Exception {
        // 模拟健康状态
        when(aiServiceManager.isHealthy()).thenReturn(true);
        when(aiServiceManager.getServiceStats()).thenReturn(mockStats);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.healthy").value(true));
    }

    @Test
    public void testHealthEndpoint_Unhealthy() throws Exception {
        // 模拟不健康状态
        when(aiServiceManager.isHealthy()).thenReturn(false);
        when(aiServiceManager.getServiceStats()).thenReturn(mockStats);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(99999))
                .andExpect(jsonPath("$.message").value("服务不健康"));
    }

    @Test
    public void testConfigEndpoint() throws Exception {
        // 模拟配置信息
        when(aiServiceManager.getConfigInfo()).thenReturn(mockConfig);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ai/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.mockMode").value(true))
                .andExpect(jsonPath("$.data.provider").exists())
                .andExpect(jsonPath("$.data.contextEnabled").value(true));
    }

    @Test
    public void testClearContextEndpoint() throws Exception {
        // 执行请求并验证响应
        mockMvc.perform(delete("/api/ai/context")
                .header("X-User-Id", "test-user-005")
                .param("conversationId", "test-conv-005"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(50001));
    }

    @Test
    public void testClearContextEndpoint_MissingUserId() throws Exception {
        // 执行请求并验证响应（缺少userId参数，应返回500错误）
        mockMvc.perform(delete("/api/ai/context")
                .param("conversationId", "test-conv-006"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(50001));
    }

    @Test
    public void testContextSummaryEndpoint() throws Exception {
        // 模拟上下文摘要
        String mockSummary = "用户表达了焦虑情绪，AI提供了情感支持和建议。";
        when(aiServiceManager.getContextSummary(anyString(), anyString()))
                .thenReturn(mockSummary);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ai/context/summary")
                .header("X-User-Id", "test-user-007")
                .param("conversationId", "test-conv-007"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(50001));
    }

    @Test
    public void testContextSummaryEndpoint_EmptySummary() throws Exception {
        // 模拟空摘要
        when(aiServiceManager.getContextSummary(anyString(), anyString()))
                .thenReturn(null);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ai/context/summary")
                .header("X-User-Id", "test-user-008")
                .param("conversationId", "test-conv-008"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(50001));
    }

    @Test
    public void testStatsEndpoint() throws Exception {
        // 模拟统计信息
        when(aiServiceManager.getServiceStats()).thenReturn(mockStats);

        // 执行请求并验证响应
        mockMvc.perform(get("/api/ai/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testResetStatsEndpoint() throws Exception {
        // 执行请求并验证响应
        mockMvc.perform(post("/api/ai/stats/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testChatEndpoint_WithDifferentEmotionalStates() throws Exception {
        // 测试不同情感状态
        String[] emotions = { "焦虑", "抑郁", "愤怒", "恐惧", "平静", "快乐" };

        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockSuccessResponse);

        for (String emotion : emotions) {
            AIController.ChatRequest chatRequest = new AIController.ChatRequest();
            chatRequest.setConversationId("test-conv-emotion");
            chatRequest.setMessage("我感到" + emotion);
            chatRequest.setEmotionalState(emotion);

            mockMvc.perform(post("/api/ai/chat")
                    .header("X-User-Id", "test-user-emotion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(chatRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.success").value(true));
        }
    }

    @Test
    public void testChatEndpoint_LongMessage() throws Exception {
        // 测试长消息
        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockSuccessResponse);

        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longMessage.append("这是一个很长的消息，用来测试系统对长文本的处理能力。");
        }

        AIController.ChatRequest chatRequest = new AIController.ChatRequest();
        chatRequest.setConversationId("test-conv-long");
        chatRequest.setMessage(longMessage.toString());
        chatRequest.setEmotionalState("中性");

        mockMvc.perform(post("/api/ai/chat")
                .header("X-User-Id", "test-user-long")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testChatEndpoint_SpecialCharacters() throws Exception {
        // 测试特殊字符
        when(aiServiceManager.processRequest(any(AIRequest.class)))
                .thenReturn(mockSuccessResponse);

        AIController.ChatRequest chatRequest = new AIController.ChatRequest();
        chatRequest.setConversationId("test-conv-special");
        chatRequest.setMessage("测试特殊字符：@#$%^&*()_+{}|:<>?[]\\;'\",./ 和 emoji 😊😢😡");
        chatRequest.setEmotionalState("中性");

        mockMvc.perform(post("/api/ai/chat")
                .header("X-User-Id", "test-user-special")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0));
    }
}