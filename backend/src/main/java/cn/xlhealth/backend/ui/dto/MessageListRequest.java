package cn.xlhealth.backend.ui.dto;

import cn.xlhealth.backend.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 消息列表查询请求DTO
 */
@Schema(description = "消息列表查询请求")
public class MessageListRequest {

    @Schema(description = "对话ID", example = "1")
    @NotNull(message = "对话ID不能为空")
    private Long conversationId;

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "20")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;

    @Schema(description = "排序字段", example = "created_time")
    private String sortBy = "created_time";

    @Schema(description = "排序方向", example = "asc")
    private String sortOrder = "asc";

    @Schema(description = "消息角色过滤", example = "USER")
    private Message.MessageRole role;

    @Schema(description = "消息状态过滤", example = "SENT")
    private Message.MessageStatus status;

    @Schema(description = "内容类型过滤", example = "TEXT")
    private Message.ContentType contentType;

    @Schema(description = "关键词搜索")
    private String keyword;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Message.MessageRole getRole() {
        return role;
    }

    public void setRole(Message.MessageRole role) {
        this.role = role;
    }

    public Message.MessageStatus getStatus() {
        return status;
    }

    public void setStatus(Message.MessageStatus status) {
        this.status = status;
    }

    public Message.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(Message.ContentType contentType) {
        this.contentType = contentType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}