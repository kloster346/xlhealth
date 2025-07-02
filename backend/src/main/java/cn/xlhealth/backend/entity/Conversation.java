package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 对话实体类
 * 对应数据库表：conversations
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("conversations")
public class Conversation {

    /**
     * 对话唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 对话标题
     */
    @TableField("title")
    private String title;

    /**
     * 对话状态
     */
    @TableField("status")
    private ConversationStatus status;

    /**
     * 扩展信息（JSON格式）
     */
    @TableField("metadata")
    private String metadata;

    /**
     * 对话摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 消息数量
     */
    @TableField("message_count")
    private Integer messageCount;

    /**
     * 总token数
     */
    @TableField("total_tokens")
    private Integer totalTokens;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

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
     * 最后消息时间
     */
    @TableField("last_message_at")
    private LocalDateTime lastMessageTime;

    /**
     * 对话状态枚举
     */
    public enum ConversationStatus {
        ACTIVE,     // 活跃
        ARCHIVED,   // 已归档
        DELETED     // 已删除
    }
}