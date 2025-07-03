package cn.xlhealth.backend.ui.dto;

import cn.xlhealth.backend.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 发送消息请求DTO
 */
@Schema(description = "发送消息请求")
public class MessageSendRequest {

    @Schema(description = "消息内容", example = "你好，我想咨询一下健康问题")
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 10000, message = "消息内容长度不能超过10000个字符")
    private String content;

    @Schema(description = "消息角色", example = "USER")
    @NotNull(message = "消息角色不能为空")
    private Message.MessageRole role;

    @Schema(description = "内容类型", example = "TEXT")
    @NotNull(message = "内容类型不能为空")
    private Message.ContentType contentType;

    @Schema(description = "元数据", example = "{\"source\":\"web\"}")
    @Size(max = 1000, message = "元数据长度不能超过1000个字符")
    private String metadata;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message.MessageRole getRole() {
        return role;
    }

    public void setRole(Message.MessageRole role) {
        this.role = role;
    }

    public Message.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(Message.ContentType contentType) {
        this.contentType = contentType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}