package cn.xlhealth.backend.ui.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * AI回复请求DTO
 * 用于 /api/v1/conversations/{conversationId}/messages/ai-reply 接口
 */
@Schema(description = "AI回复请求")
public class AIReplyRequest {

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000个字符")
    @Schema(description = "用户消息内容", example = "我最近感到很焦虑，不知道该怎么办")
    private String message;

    @Schema(description = "用户当前情绪状态", example = "焦虑")
    private String emotionalState;

    @Schema(description = "对话上下文信息")
    private ContextInfo context;

    @Schema(description = "AI回复偏好设置")
    private ReplyPreferences preferences;

    @Schema(description = "额外参数")
    private Map<String, Object> parameters;

    // 构造函数
    public AIReplyRequest() {
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public ContextInfo getContext() {
        return context;
    }

    public void setContext(ContextInfo context) {
        this.context = context;
    }

    public ReplyPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(ReplyPreferences preferences) {
        this.preferences = preferences;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * 对话上下文信息
     */
    @Schema(description = "对话上下文信息")
    public static class ContextInfo {
        @Schema(description = "是否包含历史消息", example = "true")
        private Boolean includeHistory = true;

        @Schema(description = "历史消息数量限制", example = "10")
        private Integer historyLimit = 10;

        @Schema(description = "是否包含用户档案信息", example = "true")
        private Boolean includeUserProfile = true;

        @Schema(description = "上下文摘要", example = "用户之前提到过工作压力问题")
        private String summary;

        // Getters and Setters
        public Boolean getIncludeHistory() {
            return includeHistory;
        }

        public void setIncludeHistory(Boolean includeHistory) {
            this.includeHistory = includeHistory;
        }

        public Integer getHistoryLimit() {
            return historyLimit;
        }

        public void setHistoryLimit(Integer historyLimit) {
            this.historyLimit = historyLimit;
        }

        public Boolean getIncludeUserProfile() {
            return includeUserProfile;
        }

        public void setIncludeUserProfile(Boolean includeUserProfile) {
            this.includeUserProfile = includeUserProfile;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    /**
     * AI回复偏好设置
     */
    @Schema(description = "AI回复偏好设置")
    public static class ReplyPreferences {
        @Schema(description = "回复类型偏好", example = "EMOTIONAL_SUPPORT", allowableValues = { "EMOTIONAL_SUPPORT",
                "COGNITIVE_GUIDANCE", "BEHAVIORAL_ADVICE", "INFORMATION_GATHERING" })
        private String preferredType;

        @Schema(description = "回复长度偏好", example = "MEDIUM", allowableValues = { "SHORT", "MEDIUM", "LONG" })
        private String length = "MEDIUM";

        @Schema(description = "回复语调", example = "WARM", allowableValues = { "PROFESSIONAL", "WARM", "CASUAL" })
        private String tone = "WARM";

        @Schema(description = "是否包含建议", example = "true")
        private Boolean includeAdvice = true;

        @Schema(description = "是否包含问题引导", example = "true")
        private Boolean includeQuestions = true;

        // Getters and Setters
        public String getPreferredType() {
            return preferredType;
        }

        public void setPreferredType(String preferredType) {
            this.preferredType = preferredType;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }

        public Boolean getIncludeAdvice() {
            return includeAdvice;
        }

        public void setIncludeAdvice(Boolean includeAdvice) {
            this.includeAdvice = includeAdvice;
        }

        public Boolean getIncludeQuestions() {
            return includeQuestions;
        }

        public void setIncludeQuestions(Boolean includeQuestions) {
            this.includeQuestions = includeQuestions;
        }
    }
}