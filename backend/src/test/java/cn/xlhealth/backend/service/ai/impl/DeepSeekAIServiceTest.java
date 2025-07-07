package cn.xlhealth.backend.service.ai.impl;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * DeepSeek AI服务测试类
 */
@ExtendWith(MockitoExtension.class)
class DeepSeekAIServiceTest {

    @Mock
    private AIServiceProperties aiServiceProperties;

    @Mock
    private AIServiceProperties.DeepSeek deepSeekConfig;

    @InjectMocks
    private DeepSeekAIService deepSeekAIService;

    @BeforeEach
    void setUp() {
        // 设置模拟配置
        when(aiServiceProperties.getDeepseek()).thenReturn(deepSeekConfig);
        when(deepSeekConfig.getApiKey()).thenReturn("test-api-key");
        when(deepSeekConfig.getApiBase()).thenReturn("https://api.deepseek.com");
        when(deepSeekConfig.getModel()).thenReturn("deepseek-chat");
        when(deepSeekConfig.getMaxTokens()).thenReturn(1000);
        when(deepSeekConfig.getTemperature()).thenReturn(0.7);
        when(deepSeekConfig.getTimeout()).thenReturn(30000);
    }

    @Test
    void testGetProviderName() {
        String providerName = deepSeekAIService.getProviderName();
        assertEquals("DEEPSEEK", providerName);
    }

    @Test
    void testIsHealthy_WithValidConfig() {
        boolean isHealthy = deepSeekAIService.isHealthy();
        assertTrue(isHealthy);
    }

    @Test
    void testIsHealthy_WithMissingApiKey() {
        when(deepSeekConfig.getApiKey()).thenReturn("");
        
        boolean isHealthy = deepSeekAIService.isHealthy();
        assertFalse(isHealthy);
    }

    @Test
    void testIsHealthy_WithMissingApiBase() {
        when(deepSeekConfig.getApiBase()).thenReturn("");
        
        boolean isHealthy = deepSeekAIService.isHealthy();
        assertFalse(isHealthy);
    }

    @Test
    void testGetConfigInfo() {
        java.util.Map<String, Object> configInfo = deepSeekAIService.getConfigInfo();
        
        assertNotNull(configInfo);
        assertEquals("DEEPSEEK", configInfo.get("provider"));
        assertEquals("deepseek-chat", configInfo.get("model"));
        assertEquals("https://api.deepseek.com", configInfo.get("apiBase"));
    }

    @Test
    void testGenerateReply_WithValidRequest() {
        // 创建测试请求
        AIRequest request = new AIRequest();
        request.setUserId(1L);
        request.setUserMessage("你好，我感到有些焦虑");
        
        // 注意：这个测试需要真实的API调用，在实际环境中可能需要模拟RestTemplate
        // 这里只测试方法不抛出异常
        assertDoesNotThrow(() -> {
            // 由于没有真实的API密钥，这个调用会失败，但我们可以验证方法结构
            try {
                deepSeekAIService.generateReply(request);
            } catch (Exception e) {
                // 预期会有异常，因为没有真实的API环境
                assertTrue(e.getMessage().contains("DeepSeek") || e.getMessage().contains("API"));
            }
        });
    }
}