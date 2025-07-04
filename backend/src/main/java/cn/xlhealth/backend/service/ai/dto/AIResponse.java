package cn.xlhealth.backend.service.ai.dto;

import java.util.Map;

/**
 * AI服务响应对象
 */
public class AIResponse {
    
    /**
     * AI回复内容
     */
    private String content;
    
    /**
     * 回复质量评分（0-100）
     */
    private Integer qualityScore;
    
    /**
     * 回复类型：EMOTIONAL_SUPPORT(情感支持)、COGNITIVE_GUIDANCE(认知引导)、
     * BEHAVIORAL_ADVICE(行为建议)、INFORMATION_GATHERING(信息收集)
     */
    private String replyType;
    
    /**
     * 是否成功生成回复
     */
    private boolean success;
    
    /**
     * 错误信息（如果生成失败）
     */
    private String errorMessage;
    
    /**
     * AI服务提供商
     */
    private String provider;
    
    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;
    
    /**
     * 扩展信息
     */
    private Map<String, Object> metadata;
    
    /**
     * 生成时间戳
     */
    private Long timestamp;
    
    public AIResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 创建成功响应
     */
    public static AIResponse success(String content, String replyType, String provider) {
        AIResponse response = new AIResponse();
        response.setContent(content);
        response.setReplyType(replyType);
        response.setProvider(provider);
        response.setSuccess(true);
        return response;
    }
    
    /**
     * 创建失败响应
     */
    public static AIResponse failure(String errorMessage, String provider) {
        AIResponse response = new AIResponse();
        response.setErrorMessage(errorMessage);
        response.setProvider(provider);
        response.setSuccess(false);
        return response;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getQualityScore() {
        return qualityScore;
    }
    
    public void setQualityScore(Integer qualityScore) {
        this.qualityScore = qualityScore;
    }
    
    public String getReplyType() {
        return replyType;
    }
    
    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public Long getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}