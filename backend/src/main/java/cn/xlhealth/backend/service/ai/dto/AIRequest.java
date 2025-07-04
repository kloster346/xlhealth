package cn.xlhealth.backend.service.ai.dto;

import java.util.List;
import java.util.Map;

/**
 * AI服务请求对象
 */
public class AIRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对话ID
     */
    private Long conversationId;

    /**
     * 用户消息内容
     */
    private String userMessage;

    /**
     * 消息上下文（历史消息）
     */
    private List<ContextMessage> context;

    /**
     * 用户情绪状态
     */
    private String emotionalState;

    /**
     * 扩展参数
     */
    private Map<String, Object> parameters;

    /**
     * 请求时间戳
     */
    private Long timestamp;

    public AIRequest() {
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public List<ContextMessage> getContext() {
        return context;
    }

    public void setContext(List<ContextMessage> context) {
        this.context = context;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}