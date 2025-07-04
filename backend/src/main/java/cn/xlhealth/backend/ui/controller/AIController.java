package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.ui.dto.ApiResponse;
import cn.xlhealth.backend.service.ai.AIServiceManager;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

/**
 * AI服务控制器
 */
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI服务", description = "AI心理健康咨询服务接口")
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    @Autowired
    private AIServiceManager aiServiceManager;

    /**
     * 纯AI服务调用（不涉及消息存储）
     * 注意：此接口仅用于AI服务测试和独立调用，不会保存消息到数据库
     * 如需完整的对话管理，请使用 MessageController 的 /ai-reply 接口
     */
    @PostMapping("/chat")
    @Operation(summary = "AI服务调用", description = "直接调用AI服务获取回复（不保存消息）")
    public ResponseEntity<ApiResponse<AIResponse>> chat(
            @Valid @RequestBody ChatRequest chatRequest,
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", required = false) String userId) {

        try {
            // 如果没有提供用户ID，生成一个临时ID
            if (userId == null || userId.trim().isEmpty()) {
                userId = "guest_" + UUID.randomUUID().toString().substring(0, 8);
            }

            // 转换用户ID和对话ID
            Long userIdLong = (long) Math.abs(userId.hashCode());
            Long conversationIdLong = (long) Math.abs(chatRequest.getConversationId().hashCode());

            // 构建AI请求
            AIRequest aiRequest = new AIRequest();
            aiRequest.setUserId(userIdLong);
            aiRequest.setConversationId(conversationIdLong);
            aiRequest.setUserMessage(chatRequest.getMessage());
            aiRequest.setEmotionalState(chatRequest.getEmotionalState());
            aiRequest.setParameters(chatRequest.getParameters());
            aiRequest.setTimestamp(System.currentTimeMillis());

            // 处理AI请求（仅调用AI服务，不保存消息）
            AIResponse response = aiServiceManager.processRequest(aiRequest);

            if (response.isSuccess()) {
                logger.info("AI service call successful for user: {}, conversation: {}",
                        userId, chatRequest.getConversationId());
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                logger.warn("AI service call failed for user: {}, error: {}", userId, response.getErrorMessage());
                return ResponseEntity.ok(ApiResponse.error(response.getErrorMessage()));
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid chat request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("请求参数无效: " + e.getMessage()));

        } catch (Exception e) {
            logger.error("Error processing AI service call", e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("AI服务暂时不可用，请稍后重试"));
        }
    }

    /**
     * 获取服务健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "获取AI服务健康状态")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        try {
            boolean healthy = aiServiceManager.isHealthy();
            Map<String, Object> healthInfo = Map.of(
                    "healthy", healthy,
                    "timestamp", System.currentTimeMillis(),
                    "service", "AI Service");

            if (healthy) {
                return ResponseEntity.ok(ApiResponse.success(healthInfo));
            } else {
                return ResponseEntity.ok(ApiResponse.error("服务不健康"));
            }

        } catch (Exception e) {
            logger.error("Error checking service health", e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("健康检查失败"));
        }
    }

    /**
     * 获取服务配置信息
     */
    @GetMapping("/config")
    @Operation(summary = "获取配置", description = "获取AI服务配置信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConfig() {
        try {
            Map<String, Object> configInfo = aiServiceManager.getConfigInfo();
            return ResponseEntity.ok(ApiResponse.success(configInfo));

        } catch (Exception e) {
            logger.error("Error getting config info", e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("获取配置信息失败"));
        }
    }

    /**
     * 清除用户对话上下文
     */
    @DeleteMapping("/context")
    @Operation(summary = "清除上下文", description = "清除指定用户和对话的上下文")
    public ResponseEntity<ApiResponse<String>> clearContext(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "对话ID") @RequestParam String conversationId) {

        try {
            aiServiceManager.clearUserContext(userId, conversationId);
            logger.info("Cleared context for user: {}, conversation: {}", userId, conversationId);
            return ResponseEntity.ok(ApiResponse.success("上下文已清除"));

        } catch (Exception e) {
            logger.error("Error clearing context for user: {}, conversation: {}", userId, conversationId, e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("清除上下文失败"));
        }
    }

    /**
     * 获取对话上下文摘要
     */
    @GetMapping("/context/summary")
    @Operation(summary = "获取上下文摘要", description = "获取指定用户和对话的上下文摘要")
    public ResponseEntity<ApiResponse<String>> getContextSummary(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "对话ID") @RequestParam String conversationId) {

        try {
            String summary = aiServiceManager.getContextSummary(userId, conversationId);
            return ResponseEntity.ok(ApiResponse.success(summary));

        } catch (Exception e) {
            logger.error("Error getting context summary for user: {}, conversation: {}", userId, conversationId, e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("获取上下文摘要失败"));
        }
    }

    /**
     * 获取服务统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取统计信息", description = "获取AI服务统计信息")
    public ResponseEntity<ApiResponse<String>> getStats() {
        try {
            String stats = aiServiceManager.getServiceStatsString();
            return ResponseEntity.ok(ApiResponse.success(stats));

        } catch (Exception e) {
            logger.error("Error getting service stats", e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("获取统计信息失败"));
        }
    }

    /**
     * 重置服务统计
     */
    @PostMapping("/stats/reset")
    @Operation(summary = "重置统计", description = "重置指定提供商的统计信息")
    public ResponseEntity<ApiResponse<String>> resetStats(
            @Parameter(description = "提供商名称，为空则重置全局统计") @RequestParam(required = false) String provider) {

        try {
            aiServiceManager.resetServiceStats(provider);
            String message = provider != null ? "已重置提供商 " + provider + " 的统计信息" : "已重置全局统计信息";
            return ResponseEntity.ok(ApiResponse.success(message));

        } catch (Exception e) {
            logger.error("Error resetting stats for provider: {}", provider, e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("重置统计信息失败"));
        }
    }

    /**
     * 聊天请求DTO
     */
    public static class ChatRequest {
        private String conversationId;
        private String message;
        private String emotionalState;
        private Map<String, Object> parameters;

        // 构造函数
        public ChatRequest() {
            this.conversationId = UUID.randomUUID().toString();
        }

        // Getters and Setters
        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getEmotionalState() {
            return emotionalState;
        }

        public void setEmotionalState(String emotionalState) {
            this.emotionalState = emotionalState;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
}