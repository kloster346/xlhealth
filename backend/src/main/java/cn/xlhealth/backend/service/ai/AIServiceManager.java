package cn.xlhealth.backend.service.ai;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.context.ContextManager;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.dto.ContextMessage;
import cn.xlhealth.backend.service.ai.exception.AIServiceException;
import cn.xlhealth.backend.service.ai.monitor.ServiceMonitor;
import cn.xlhealth.backend.service.ai.quality.QualityAssessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * AI服务管理器
 * 负责协调AI服务、上下文管理、质量评估和监控等组件
 */
@Service
public class AIServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceManager.class);

    @Autowired
    private AIService aiService;

    @Autowired
    private ContextManager contextManager;

    @Autowired
    private QualityAssessor qualityAssessor;

    @Autowired
    private ServiceMonitor serviceMonitor;

    @Autowired
    private AIServiceProperties aiServiceProperties;

    /**
     * 处理用户请求并生成回复
     */
    public AIResponse processRequest(AIRequest request) {
        long startTime = System.currentTimeMillis();
        AIResponse response = null;

        try {
            // 验证请求
            validateRequest(request);

            // 获取上下文
            List<ContextMessage> context = contextManager.getContext(
                    request.getConversationId(),
                    aiServiceProperties.getContext().getWindowSize());
            request.setContext(context);

            // 生成AI回复
            response = aiService.generateReply(request);

            // 质量评估
            if (response.isSuccess() && aiServiceProperties.getQuality().isEnabled()) {
                int qualityScore = qualityAssessor.assessQuality(request, response);

                // 检查质量是否达标
                if (!qualityAssessor.isPassing(qualityScore, aiServiceProperties.getQuality().getMinScore())) {
                    logger.warn("Response quality below threshold: {} < {}",
                            qualityScore, aiServiceProperties.getQuality().getMinScore());

                    // 如果启用了降级，尝试降级处理
                    if (aiServiceProperties.getFallback().isEnabled()) {
                        response = handleFallback(request, "Quality below threshold");
                    }
                }
            }

            // 添加消息到上下文
            if (response.isSuccess()) {
                // 添加用户消息到上下文
                ContextMessage userMessage = new ContextMessage();
                userMessage.setContent(request.getUserMessage());
                userMessage.setMessageType("USER");
                userMessage.setTimestamp(System.currentTimeMillis());
                contextManager.addMessage(request.getConversationId(), userMessage);

                // 添加AI回复
                ContextMessage aiMessage = new ContextMessage();
                aiMessage.setContent(response.getContent());
                aiMessage.setMessageType("AI");
                aiMessage.setTimestamp(System.currentTimeMillis());
                contextManager.addMessage(request.getConversationId(), aiMessage);
            }

            return response;

        } catch (AIServiceException e) {
            logger.error("AI service error: {}", e.getMessage(), e);
            response = AIResponse.failure(e.getMessage(), e.getProvider());

            // 尝试降级处理
            if (aiServiceProperties.getFallback().isEnabled()) {
                response = handleFallback(request, e.getMessage());
            }

            return response;

        } catch (Exception e) {
            logger.error("Unexpected error processing AI request", e);
            response = AIResponse.failure("服务暂时不可用，请稍后重试", "UNKNOWN");

            // 尝试降级处理
            if (aiServiceProperties.getFallback().isEnabled()) {
                response = handleFallback(request, "Unexpected error: " + e.getMessage());
            }

            return response;

        } finally {
            // 记录监控信息
            long responseTime = System.currentTimeMillis() - startTime;
            if (response != null) {
                serviceMonitor.recordCall(request, response, responseTime);
            } else {
                serviceMonitor.recordError(request, new RuntimeException("No response generated"), responseTime);
            }
        }
    }

    /**
     * 处理降级逻辑
     */
    private AIResponse handleFallback(AIRequest request, String reason) {
        try {
            logger.info("Executing fallback for reason: {}", reason);

            // 简单的降级回复
            String fallbackMessage = aiServiceProperties.getFallback().getDefaultMessage();
            if (fallbackMessage == null || fallbackMessage.isEmpty()) {
                fallbackMessage = "抱歉，我现在无法为您提供最佳回复。请稍后重试，或联系专业心理咨询师获得帮助。";
            }

            AIResponse fallbackResponse = AIResponse.success(
                    fallbackMessage,
                    "FALLBACK",
                    "FALLBACK");

            fallbackResponse.setQualityScore(60); // 降级回复给予中等质量分

            return fallbackResponse;

        } catch (Exception e) {
            logger.error("Error in fallback handling", e);
            return AIResponse.failure("服务暂时不可用", "FALLBACK");
        }
    }

    /**
     * 验证请求参数
     */
    private void validateRequest(AIRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (request.getUserMessage() == null || request.getUserMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("User message cannot be null or empty");
        }

        if (request.getConversationId() == null) {
            throw new IllegalArgumentException("Conversation ID cannot be null");
        }
    }

    /**
     * 获取服务健康状态
     */
    public boolean isHealthy() {
        try {
            // 检查AI服务健康状态
            boolean aiServiceHealthy = aiService.isHealthy();

            // 检查监控器健康状态
            boolean monitorHealthy = serviceMonitor.isHealthy(null);

            return aiServiceHealthy && monitorHealthy;

        } catch (Exception e) {
            logger.error("Error checking service health", e);
            return false;
        }
    }

    /**
     * 获取服务配置信息
     */
    public Map<String, Object> getConfigInfo() {
        try {
            return aiService.getConfigInfo();
        } catch (Exception e) {
            logger.error("Error getting config info", e);
            return Map.of("error", "Unable to retrieve config info");
        }
    }

    /**
     * 清除用户上下文
     */
    public void clearUserContext(String userId, String conversationId) {
        try {
            Long conversationIdLong = Long.parseLong(conversationId);
            contextManager.clearContext(conversationIdLong);
            logger.info("Cleared context for user: {}, conversation: {}", userId, conversationId);
        } catch (NumberFormatException e) {
            logger.error("Invalid conversation ID format: {}", conversationId, e);
        } catch (Exception e) {
            logger.error("Error clearing context for user: {}, conversation: {}", userId, conversationId, e);
        }
    }

    /**
     * 获取上下文摘要
     */
    public String getContextSummary(String userId, String conversationId) {
        try {
            Long conversationIdLong = Long.parseLong(conversationId);
            return contextManager.generateContextSummary(conversationIdLong);
        } catch (NumberFormatException e) {
            logger.error("Invalid conversation ID format: {}", conversationId, e);
            return "无法获取上下文摘要：对话ID格式错误";
        } catch (Exception e) {
            logger.error("Error getting context summary for user: {}, conversation: {}", userId, conversationId, e);
            return "无法获取上下文摘要";
        }
    }

    /**
     * 获取服务统计信息
     */
    public java.util.Map<String, cn.xlhealth.backend.service.ai.monitor.ServiceStats> getServiceStats() {
        try {
            java.util.Map<String, cn.xlhealth.backend.service.ai.monitor.ServiceStats> stats = new java.util.HashMap<>();

            // 获取全局统计
            cn.xlhealth.backend.service.ai.monitor.ServiceStats globalStats = serviceMonitor.getStats(null);
            stats.put("GLOBAL", globalStats);

            // 获取各个提供商的统计
            cn.xlhealth.backend.service.ai.monitor.ServiceStats mockStats = serviceMonitor.getStats("MockAI");
            stats.put("MockAI", mockStats);

            return stats;
        } catch (Exception e) {
            logger.error("Error getting service stats", e);
            return new java.util.HashMap<>();
        }
    }

    /**
     * 获取服务统计信息字符串格式
     */
    public String getServiceStatsString() {
        try {
            return ((cn.xlhealth.backend.service.ai.monitor.impl.ServiceMonitorImpl) serviceMonitor)
                    .getHealthSummary();
        } catch (Exception e) {
            logger.error("Error getting service stats", e);
            return "无法获取服务统计信息";
        }
    }

    /**
     * 重置服务统计
     */
    public void resetServiceStats(String provider) {
        try {
            serviceMonitor.resetStats(provider);
            logger.info("Reset stats for provider: {}", provider);
        } catch (Exception e) {
            logger.error("Error resetting stats for provider: {}", provider, e);
        }
    }
}