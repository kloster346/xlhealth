package cn.xlhealth.backend.service.ai.dto;

/**
 * 上下文消息对象
 * 用于表示对话历史中的消息
 */
public class ContextMessage {
    
    /**
     * 消息ID
     */
    private Long messageId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：USER(用户消息) 或 AI(AI回复)
     */
    private String messageType;
    
    /**
     * 消息时间戳
     */
    private Long timestamp;
    
    /**
     * 消息权重（用于上下文重要性评估）
     */
    private Double weight;
    
    public ContextMessage() {}
    
    public ContextMessage(Long messageId, String content, String messageType, Long timestamp) {
        this.messageId = messageId;
        this.content = content;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.weight = 1.0; // 默认权重
    }
    
    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}