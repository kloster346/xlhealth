package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 消息实体类
 * 对应数据库表：messages
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("messages")
public class Message {

    /**
     * 消息唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对话ID
     */
    @TableField("conversation_id")
    private Long conversationId;

    /**
     * 发送者用户ID
     */
    @TableField("user_id")
    private Long userId;



    /**
     * 消息角色
     */
    @TableField("role")
    private MessageRole role;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 内容类型
     */
    @TableField("content_type")
    private ContentType contentType;

    /**
     * token数量
     */
    @TableField("token_count")
    private Integer tokenCount;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 提示token数
     */
    @TableField("prompt_tokens")
    private Integer promptTokens;

    /**
     * 完成token数
     */
    @TableField("completion_tokens")
    private Integer completionTokens;

    /**
     * 总token数
     */
    @TableField("total_tokens")
    private Integer totalTokens;

    /**
     * 响应时间(毫秒)
     */
    @TableField("response_time")
    private Long responseTime;

    /**
     * 消息状态
     */
    @TableField("status")
    private MessageStatus status;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;



    /**
     * 消息角色枚举
     */
    public enum MessageRole {
        USER,       // 用户
        ASSISTANT,  // 助手
        SYSTEM      // 系统
    }

    /**
     * 内容类型枚举
     */
    public enum ContentType {
        TEXT,       // 文本
        IMAGE,      // 图片
        AUDIO,      // 音频
        VIDEO,      // 视频
        FILE        // 文件
    }

    /**
     * 消息状态枚举
     */
    public enum MessageStatus {
        FAILED,     // 失败
        SUCCESS,    // 成功
        PROCESSING  // 处理中
    }
}