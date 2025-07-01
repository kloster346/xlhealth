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
     * 消息类型
     */
    @TableField("message_type")
    private MessageType messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息元数据（JSON格式）
     */
    @TableField("metadata")
    private String metadata;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 是否已删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        USER,       // 用户消息
        AI,         // AI回复
        SYSTEM      // 系统消息
    }
}