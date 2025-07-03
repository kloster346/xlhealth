package cn.xlhealth.backend.ui.dto;

import cn.xlhealth.backend.entity.Conversation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 对话响应DTO
 */
@Schema(description = "对话响应")
public class ConversationResponse {

    @Schema(description = "对话ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "对话标题", example = "健康咨询")
    private String title;

    @Schema(description = "对话状态", example = "ACTIVE")
    private Conversation.ConversationStatus status;

    @Schema(description = "元数据", example = "{\"type\":\"health_consultation\"}")
    private String metadata;

    @Schema(description = "对话摘要", example = "关于血压问题的咨询")
    private String summary;

    @Schema(description = "消息数量", example = "5")
    private Integer messageCount;

    @Schema(description = "总token数", example = "1500")
    private Integer totalTokens;

    @Schema(description = "开始时间", example = "2024-01-01T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-01T11:00:00")
    private LocalDateTime endTime;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01T11:00:00")
    private LocalDateTime updatedTime;

    @Schema(description = "最后消息时间", example = "2024-01-01T10:30:00")
    private LocalDateTime lastMessageTime;

    // 构造函数
    public ConversationResponse() {}

    public ConversationResponse(Conversation conversation) {
        this.id = conversation.getId();
        this.userId = conversation.getUserId();
        this.title = conversation.getTitle();
        this.status = conversation.getStatus();
        this.metadata = conversation.getMetadata();
        this.summary = conversation.getSummary();
        this.messageCount = conversation.getMessageCount();
        this.totalTokens = conversation.getTotalTokens();
        this.startTime = conversation.getStartTime();
        this.endTime = conversation.getEndTime();
        this.createdTime = conversation.getCreatedTime();
        this.updatedTime = conversation.getUpdatedTime();
        this.lastMessageTime = conversation.getLastMessageTime();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Conversation.ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(Conversation.ConversationStatus status) {
        this.status = status;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}