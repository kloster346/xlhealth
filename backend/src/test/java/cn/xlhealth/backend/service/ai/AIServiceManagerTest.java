package cn.xlhealth.backend.service.ai;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.context.ContextManager;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.dto.ContextMessage;
import cn.xlhealth.backend.service.ai.monitor.ServiceMonitor;
import cn.xlhealth.backend.service.ai.quality.QualityAssessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AI服务管理器测试类
 */
@ExtendWith(MockitoExtension.class)
class AIServiceManagerTest {

    @Mock
    private AIService aiService;

    @Mock
    private ContextManager contextManager;

    @Mock
    private QualityAssessor qualityAssessor;

    @Mock
    private ServiceMonitor serviceMonitor;

    @Mock
    private AIServiceProperties aiServiceProperties;

    @InjectMocks
    private AIServiceManager aiServiceManager;

    private AIRequest testRequest;
    private AIResponse testResponse;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testRequest = new AIRequest();
        testRequest.setUserId(123L);
        testRequest.setConversationId(456L);
        testRequest.setUserMessage("我感到很焦虑，该怎么办？");
        testRequest.setEmotionalState("焦虑");
        testRequest.setTimestamp(System.currentTimeMillis());

        testResponse = AIResponse.success(
                "我理解您的焦虑感受。建议您尝试深呼吸练习，这可以帮助缓解焦虑情绪。",
                "MOCK",
                "EMOTIONAL_SUPPORT");
        testResponse.setQualityScore(85);
    }

    @Test
    void testProcessRequest_Success() {
        // 准备Mock行为
        List<ContextMessage> mockContext = new ArrayList<>();
        when(contextManager.getContext(anyLong(), anyInt())).thenReturn(mockContext);
        when(aiService.generateReply(any(AIRequest.class))).thenReturn(testResponse);

        // 配置质量评估
        AIServiceProperties.Quality qualityConfig = new AIServiceProperties.Quality();
        qualityConfig.setEnabled(true);
        qualityConfig.setMinScore(60);
        when(aiServiceProperties.getQuality()).thenReturn(qualityConfig);
        when(qualityAssessor.assessQuality(any(AIRequest.class), any(AIResponse.class))).thenReturn(85);
        when(qualityAssessor.isPassing(85, 60)).thenReturn(true);

        // 执行测试
        AIResponse result = aiServiceManager.processRequest(testRequest);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("我理解您的焦虑感受。建议您尝试深呼吸练习，这可以帮助缓解焦虑情绪。", result.getContent());
        assertEquals(85, result.getQualityScore());

        // 验证方法调用
        verify(contextManager).getContext(eq(456L), anyInt());
        verify(aiService).generateReply(any(AIRequest.class));
        verify(qualityAssessor).assessQuality(any(AIRequest.class), any(AIResponse.class));
        verify(contextManager, times(2)).addMessage(eq(456L), any(ContextMessage.class));
        verify(serviceMonitor).recordCall(any(AIRequest.class), any(AIResponse.class), anyLong());
    }

    @Test
    void testProcessRequest_QualityBelowThreshold() {
        // 准备Mock行为
        List<ContextMessage> mockContext = new ArrayList<>();
        when(contextManager.getContext(anyLong(), anyInt())).thenReturn(mockContext);
        when(aiService.generateReply(any(AIRequest.class))).thenReturn(testResponse);

        // 配置质量评估 - 质量不达标
        AIServiceProperties.Quality qualityConfig = new AIServiceProperties.Quality();
        qualityConfig.setEnabled(true);
        qualityConfig.setMinScore(90); // 设置更高的阈值
        when(aiServiceProperties.getQuality()).thenReturn(qualityConfig);
        when(qualityAssessor.assessQuality(any(AIRequest.class), any(AIResponse.class))).thenReturn(85);
        when(qualityAssessor.isPassing(85, 90)).thenReturn(false);

        // 配置降级
        AIServiceProperties.Fallback fallbackConfig = new AIServiceProperties.Fallback();
        fallbackConfig.setEnabled(true);
        fallbackConfig.setDefaultMessage("抱歉，我现在无法为您提供最佳回复。");
        when(aiServiceProperties.getFallback()).thenReturn(fallbackConfig);

        // 执行测试
        AIResponse result = aiServiceManager.processRequest(testRequest);

        // 验证结果 - 应该返回降级回复
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("抱歉，我现在无法为您提供最佳回复。", result.getContent());
        assertEquals("FALLBACK", result.getProvider());
        assertEquals(60, result.getQualityScore());
    }

    @Test
    void testProcessRequest_InvalidRequest() {
        // 测试无效请求
        AIRequest invalidRequest = new AIRequest();
        // 不设置必要字段

        // 执行测试
        AIResponse result = aiServiceManager.processRequest(invalidRequest);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("cannot be null or empty"));
    }

    @Test
    void testProcessRequest_ServiceException() {
        // 准备Mock行为 - AI服务抛出异常
        List<ContextMessage> mockContext = new ArrayList<>();
        when(contextManager.getContext(anyLong(), anyInt())).thenReturn(mockContext);
        when(aiService.generateReply(any(AIRequest.class)))
                .thenThrow(new RuntimeException("AI service unavailable"));

        // 配置降级
        AIServiceProperties.Fallback fallbackConfig = new AIServiceProperties.Fallback();
        fallbackConfig.setEnabled(true);
        fallbackConfig.setDefaultMessage("服务暂时不可用");
        when(aiServiceProperties.getFallback()).thenReturn(fallbackConfig);

        // 执行测试
        AIResponse result = aiServiceManager.processRequest(testRequest);

        // 验证结果 - 应该返回降级回复
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("服务暂时不可用", result.getContent());
        assertEquals("FALLBACK", result.getProvider());
    }

    @Test
    void testIsHealthy() {
        // 准备Mock行为
        when(aiService.isHealthy()).thenReturn(true);
        when(serviceMonitor.isHealthy(null)).thenReturn(true);

        // 执行测试
        boolean result = aiServiceManager.isHealthy();

        // 验证结果
        assertTrue(result);
        verify(aiService).isHealthy();
        verify(serviceMonitor).isHealthy(null);
    }

    @Test
    void testIsHealthy_ServiceUnhealthy() {
        // 准备Mock行为 - AI服务不健康
        when(aiService.isHealthy()).thenReturn(false);
        when(serviceMonitor.isHealthy(null)).thenReturn(true);

        // 执行测试
        boolean result = aiServiceManager.isHealthy();

        // 验证结果
        assertFalse(result);
    }

    @Test
    void testGetConfigInfo() {
        // 准备Mock行为
        Map<String, Object> mockConfig = Map.of(
                "provider", "MOCK",
                "mockMode", true,
                "version", "1.0.0");
        when(aiService.getConfigInfo()).thenReturn(mockConfig);

        // 执行测试
        Map<String, Object> result = aiServiceManager.getConfigInfo();

        // 验证结果
        assertNotNull(result);
        assertEquals("MOCK", result.get("provider"));
        assertEquals(true, result.get("mockMode"));
        assertEquals("1.0.0", result.get("version"));
    }

    @Test
    void testClearUserContext() {
        // 执行测试
        aiServiceManager.clearUserContext("test-user", "456");

        // 验证方法调用
        verify(contextManager).clearContext(456L);
    }

    @Test
    void testGetContextSummary() {
        // 准备Mock行为
        String mockSummary = "用户表达了焦虑情绪，AI提供了情感支持和建议。";
        when(contextManager.generateContextSummary(456L))
                .thenReturn(mockSummary);

        // 执行测试
        String result = aiServiceManager.getContextSummary("test-user", "test-conversation");

        // 验证结果
        assertEquals(mockSummary, result);
        verify(contextManager).generateContextSummary(456L);
    }

    @Test
    void testResetServiceStats() {
        // 执行测试
        aiServiceManager.resetServiceStats("MOCK");

        // 验证方法调用
        verify(serviceMonitor).resetStats("MOCK");
    }
}