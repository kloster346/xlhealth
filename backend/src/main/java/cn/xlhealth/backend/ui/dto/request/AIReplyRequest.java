package cn.xlhealth.backend.ui.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AI回复请求DTO
 * 用于接收生成AI回复的请求参数
 *
 * @author xlhealth
 * @since 2024-01-01
 */
@Data
@Schema(description = "AI回复请求")
public class AIReplyRequest {

    /**
     * 用户提示词或消息内容
     */
    @NotBlank(message = "提示词不能为空")
    @Size(max = 2000, message = "提示词长度不能超过2000个字符")
    @Schema(description = "用户提示词或消息内容", example = "请帮我分析一下这个问题")
    private String prompt;

    /**
     * AI模型类型（可选）
     */
    @Schema(description = "AI模型类型", example = "gpt-3.5-turbo")
    private String model;

    /**
     * 温度参数（可选，控制回复的随机性）
     */
    @Schema(description = "温度参数，控制回复的随机性", example = "0.7")
    private Double temperature;

    /**
     * 最大令牌数（可选）
     */
    @Schema(description = "最大令牌数", example = "1000")
    private Integer maxTokens;
}