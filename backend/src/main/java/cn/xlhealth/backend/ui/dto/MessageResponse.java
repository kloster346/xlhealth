package cn.xlhealth.backend.ui.dto;

import cn.xlhealth.backend.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 消息响应DTO
 */
@Schema(description = "消息响应")
public class MessageResponse {

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "对话ID", example = "1")
    private Long conversationId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "消息角色", example = "USER")
    private Message.MessageRole role;

    @Schema(description = "消息内容", example = "你好，我想咨询一下健康问题")
    private String content;

    @Schema(description = "内容类型", example = "TEXT")
    private Message.ContentType contentType;

    @Schema(description = "消息状态", example = "SENT")
    private Message.MessageStatus status;

    @Schema(description = "Token数量", example = "25")
    private Integer tokenCount;

    @Schema(description = "模型名称", example = "gpt-3.5-turbo")
    private String modelName;

    @Schema(description = "提示Token数", example = "20")
    private Integer promptTokens;

    @Schema(description = "完成Token数", example = "30")
    private Integer completionTokens;

    @Schema(description = "总Token数", example = "50")
    private Integer totalTokens;

    @Schema(description = "响应时间(毫秒)", example = "1500")
    private Long responseTime;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "元数据", example = "{\"source\":\"web\"}")
    private String metadata;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedTime;

    @Schema(description = "是否已删除", example = "false")
    private Boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Message.MessageRole getRole() {
        return role;
    }

    public void setRole(Message.MessageRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(Message.ContentType contentType) {
        this.contentType = contentType;
    }

    public Message.MessageStatus getStatus() {
        return status;
    }

    public void setStatus(Message.MessageStatus status) {
        this.status = status;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}