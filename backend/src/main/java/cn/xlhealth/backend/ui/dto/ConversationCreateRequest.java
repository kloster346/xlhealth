package cn.xlhealth.backend.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建对话请求DTO
 */
@Schema(description = "创建对话请求")
public class ConversationCreateRequest {

    @Schema(description = "对话标题", example = "健康咨询")
    @NotBlank(message = "对话标题不能为空")
    @Size(max = 100, message = "对话标题长度不能超过100个字符")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}