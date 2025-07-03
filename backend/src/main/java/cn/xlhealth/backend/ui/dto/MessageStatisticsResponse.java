package cn.xlhealth.backend.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 消息统计响应DTO
 */
@Schema(description = "消息统计响应")
public class MessageStatisticsResponse {

    @Schema(description = "总消息数", example = "100")
    private Long totalMessages;

    @Schema(description = "用户消息数", example = "50")
    private Long userMessages;

    @Schema(description = "助手消息数", example = "50")
    private Long assistantMessages;

    @Schema(description = "总Token数", example = "5000")
    private Integer totalTokens;

    @Schema(description = "平均每条消息Token数", example = "50")
    private Double averageTokensPerMessage;

    @Schema(description = "用户消息占比", example = "0.5")
    private Double userMessageRatio;

    @Schema(description = "助手消息占比", example = "0.5")
    private Double assistantMessageRatio;

    public Long getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Long totalMessages) {
        this.totalMessages = totalMessages;
        calculateRatios();
    }

    public Long getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(Long userMessages) {
        this.userMessages = userMessages;
        calculateRatios();
    }

    public Long getAssistantMessages() {
        return assistantMessages;
    }

    public void setAssistantMessages(Long assistantMessages) {
        this.assistantMessages = assistantMessages;
        calculateRatios();
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
        calculateAverageTokens();
    }

    public Double getAverageTokensPerMessage() {
        return averageTokensPerMessage;
    }

    public void setAverageTokensPerMessage(Double averageTokensPerMessage) {
        this.averageTokensPerMessage = averageTokensPerMessage;
    }

    public Double getUserMessageRatio() {
        return userMessageRatio;
    }

    public void setUserMessageRatio(Double userMessageRatio) {
        this.userMessageRatio = userMessageRatio;
    }

    public Double getAssistantMessageRatio() {
        return assistantMessageRatio;
    }

    public void setAssistantMessageRatio(Double assistantMessageRatio) {
        this.assistantMessageRatio = assistantMessageRatio;
    }

    /**
     * 计算消息占比
     */
    private void calculateRatios() {
        if (totalMessages != null && totalMessages > 0) {
            if (userMessages != null) {
                this.userMessageRatio = userMessages.doubleValue() / totalMessages.doubleValue();
            }
            if (assistantMessages != null) {
                this.assistantMessageRatio = assistantMessages.doubleValue() / totalMessages.doubleValue();
            }
        }
    }

    /**
     * 计算平均Token数
     */
    private void calculateAverageTokens() {
        if (totalMessages != null && totalMessages > 0 && totalTokens != null) {
            this.averageTokensPerMessage = totalTokens.doubleValue() / totalMessages.doubleValue();
        }
    }
}