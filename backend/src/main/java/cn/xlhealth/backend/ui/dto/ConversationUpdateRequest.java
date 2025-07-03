package cn.xlhealth.backend.ui.dto;

import cn.xlhealth.backend.entity.Conversation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * 更新对话请求DTO
 */
@Schema(description = "更新对话请求")
public class ConversationUpdateRequest {

    @Schema(description = "对话标题", example = "健康咨询")
    @Size(max = 100, message = "对话标题长度不能超过100个字符")
    private String title;

    @Schema(description = "对话状态", example = "ACTIVE")
    private Conversation.ConversationStatus status;

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
}