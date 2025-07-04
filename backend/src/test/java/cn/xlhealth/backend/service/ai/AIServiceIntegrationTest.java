package cn.xlhealth.backend.service.ai;

import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.monitor.ServiceStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI服务集成测试
 * 测试整个AI服务模块的端到端功能
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "ai.service.mock-mode=true",
        "ai.service.provider=MOCK",
        "ai.service.context.enabled=true",
        "ai.service.quality.enabled=true",
        "ai.service.monitoring.enabled=true"
})
public class AIServiceIntegrationTest {

    @Autowired
    private AIServiceManager aiServiceManager;

    @Test
    public void testCompleteAIServiceFlow() {
        // 准备测试数据
        String userId = "test-user-001";
        String conversationId = "test-conv-001";

        // 测试第一轮对话
        AIRequest request1 = createTestRequest(userId, conversationId, "我感到很焦虑", "焦虑");
        AIResponse response1 = aiServiceManager.processRequest(request1);

        // 验证第一轮对话结果
        assertNotNull(response1);
        assertTrue(response1.isSuccess());
        assertNotNull(response1.getContent());
        assertTrue(response1.getQualityScore() > 0);
        assertEquals("MOCK", response1.getProvider());

        // 测试第二轮对话（验证上下文功能）
        AIRequest request2 = createTestRequest(userId, conversationId, "有什么具体的建议吗？", "期待");
        AIResponse response2 = aiServiceManager.processRequest(request2);

        // 验证第二轮对话结果
        assertNotNull(response2);
        assertTrue(response2.isSuccess());
        assertNotNull(response2.getContent());

        // 验证服务健康状态
        assertTrue(aiServiceManager.isHealthy());

        // 验证统计信息
        Map<String, ServiceStats> stats = aiServiceManager.getServiceStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("MOCK"));

        ServiceStats mockStats = stats.get("MOCK");
        assertTrue(mockStats.getTotalCalls() >= 2);
        assertTrue(mockStats.getSuccessfulCalls() >= 2);
        assertEquals(0, mockStats.getFailedCalls());

        // 测试上下文摘要
        String summary = aiServiceManager.getContextSummary(userId, conversationId);
        assertNotNull(summary);
        assertFalse(summary.trim().isEmpty());

        // 测试清除上下文
        aiServiceManager.clearUserContext(userId, conversationId);

        // 验证上下文已清除
        String emptySummary = aiServiceManager.getContextSummary(userId, conversationId);
        assertTrue(emptySummary == null || emptySummary.trim().isEmpty());
    }

    @Test
    public void testQualityAssessment() {
        String userId = "test-user-002";
        String conversationId = "test-conv-002";

        // 测试正常质量的消息
        AIRequest normalRequest = createTestRequest(userId, conversationId, "我想了解一些放松技巧", "平静");
        AIResponse normalResponse = aiServiceManager.processRequest(normalRequest);

        assertNotNull(normalResponse);
        assertTrue(normalResponse.isSuccess());
        assertTrue(normalResponse.getQualityScore() >= 60); // 默认最低质量阈值

        // 测试可能触发质量检查的消息
        AIRequest edgeCaseRequest = createTestRequest(userId, conversationId, "测试", "中性");
        AIResponse edgeCaseResponse = aiServiceManager.processRequest(edgeCaseRequest);

        assertNotNull(edgeCaseResponse);
        // 即使质量较低，Mock服务也应该返回成功（因为是模拟环境）
        assertTrue(edgeCaseResponse.isSuccess());
    }

    @Test
    public void testServiceMonitoring() {
        String userId = "test-user-003";
        String conversationId = "test-conv-003";

        // 记录初始统计
        Map<String, ServiceStats> initialStats = aiServiceManager.getServiceStats();
        ServiceStats initialMockStats = initialStats.get("MOCK");
        long initialCalls = initialMockStats != null ? initialMockStats.getTotalCalls() : 0;

        // 执行多次请求
        for (int i = 0; i < 5; i++) {
            AIRequest request = createTestRequest(userId, conversationId, "测试消息 " + i, "中性");
            AIResponse response = aiServiceManager.processRequest(request);
            assertTrue(response.isSuccess());
        }

        // 验证统计更新
        Map<String, ServiceStats> updatedStats = aiServiceManager.getServiceStats();
        ServiceStats updatedMockStats = updatedStats.get("MOCK");

        assertNotNull(updatedMockStats);
        assertEquals(initialCalls + 5, updatedMockStats.getTotalCalls());
        assertTrue(updatedMockStats.getAverageResponseTime() > 0);

        // 测试统计重置
        aiServiceManager.resetServiceStats("MOCK");
        Map<String, ServiceStats> resetStats = aiServiceManager.getServiceStats();
        ServiceStats resetMockStats = resetStats.get("MOCK");

        if (resetMockStats != null) {
            assertEquals(0, resetMockStats.getTotalCalls());
            assertEquals(0, resetMockStats.getSuccessfulCalls());
            assertEquals(0, resetMockStats.getFailedCalls());
        }
    }

    @Test
    public void testErrorHandling() {
        // 测试无效请求
        AIRequest invalidRequest = new AIRequest();
        // 不设置必要字段

        AIResponse response = aiServiceManager.processRequest(invalidRequest);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNotNull(response.getContent());
        assertTrue(response.getContent().contains("无效") || response.getContent().contains("错误"));
    }

    @Test
    public void testConfigurationInfo() {
        Map<String, Object> config = aiServiceManager.getConfigInfo();

        assertNotNull(config);
        assertTrue(config.containsKey("mockMode"));
        assertTrue(config.containsKey("provider"));
        assertTrue(config.containsKey("contextEnabled"));
        assertTrue(config.containsKey("qualityEnabled"));
        assertTrue(config.containsKey("monitoringEnabled"));

        assertEquals(true, config.get("mockMode"));
        assertEquals("MOCK", config.get("provider"));
    }

    @Test
    public void testDifferentEmotionalStates() {
        String userId = "test-user-004";
        String conversationId = "test-conv-004";

        String[] emotionalStates = { "焦虑", "抑郁", "愤怒", "恐惧", "平静", "快乐" };

        for (String emotion : emotionalStates) {
            AIRequest request = createTestRequest(userId, conversationId,
                    "我现在感到" + emotion, emotion);
            AIResponse response = aiServiceManager.processRequest(request);

            assertNotNull(response, "情感状态 " + emotion + " 的响应不应为空");
            assertTrue(response.isSuccess(), "情感状态 " + emotion + " 应该处理成功");
            assertNotNull(response.getContent(), "情感状态 " + emotion + " 的内容不应为空");
            assertNotNull(response.getReplyType(), "情感状态 " + emotion + " 应该有回复类型");
        }
    }

    @Test
    public void testConcurrentRequests() throws InterruptedException {
        String userId = "test-user-005";
        String conversationId = "test-conv-005";

        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];

        // 创建并发请求
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    AIRequest request = createTestRequest(userId + "-" + index,
                            conversationId + "-" + index, "并发测试消息 " + index, "中性");
                    AIResponse response = aiServiceManager.processRequest(request);
                    results[index] = response != null && response.isSuccess();
                } catch (Exception e) {
                    results[index] = false;
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join(5000); // 最多等待5秒
        }

        // 验证所有请求都成功
        for (int i = 0; i < threadCount; i++) {
            assertTrue(results[i], "并发请求 " + i + " 应该成功");
        }
    }

    /**
     * 创建测试请求
     */
    private AIRequest createTestRequest(String userId, String conversationId,
            String message, String emotionalState) {
        AIRequest request = new AIRequest();
        request.setUserId(Long.parseLong(userId.replaceAll("[^0-9]", "1")));
        request.setConversationId(Long.parseLong(conversationId.replaceAll("[^0-9]", "1")));
        request.setUserMessage(message);
        request.setEmotionalState(emotionalState);
        request.setTimestamp(System.currentTimeMillis());
        return request;
    }
}