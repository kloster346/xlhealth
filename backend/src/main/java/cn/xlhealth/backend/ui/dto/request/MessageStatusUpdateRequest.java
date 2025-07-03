package cn.xlhealth.backend.ui.dto.request;

import cn.xlhealth.backend.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 消息状态更新请求
 */
@Data
@Schema(description = "消息状态更新请求")
public class MessageStatusUpdateRequest {

    @Schema(description = "消息状态", example = "SUCCESS")
    @NotNull(message = "消息状态不能为空")
    private Message.MessageStatus status;
}