package cn.xlhealth.backend.ui.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 批量删除消息请求DTO
 *
 * @author xlhealth
 * @since 2025-01-01
 */
@Schema(description = "批量删除消息请求")
public class BatchDeleteMessageRequest {

    @Schema(description = "消息ID列表", example = "[1, 2, 3]")
    @NotNull(message = "消息ID列表不能为空")
    @NotEmpty(message = "消息ID列表不能为空")
    private List<Long> messageIds;

    public List<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<Long> messageIds) {
        this.messageIds = messageIds;
    }

    @Override
    public String toString() {
        return "BatchDeleteMessageRequest{" +
                "messageIds=" + messageIds +
                '}';
    }
}