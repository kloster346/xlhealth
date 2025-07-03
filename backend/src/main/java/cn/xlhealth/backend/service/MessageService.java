package cn.xlhealth.backend.service;

import cn.xlhealth.backend.entity.Message;
import cn.xlhealth.backend.ui.dto.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 消息管理服务接口
 * 
 * @author XLHealth Team
 * @since 1.0.0
 */
public interface MessageService {

  /**
   * 发送消息
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID
   * @param content        消息内容
   * @param role           消息角色
   * @param contentType    内容类型
   * @return 发送的消息
   */
  Message sendMessage(Long conversationId, Long userId, String content,
      Message.MessageRole role, Message.ContentType contentType);

  /**
   * 获取对话消息列表（分页）
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID（用于权限验证）
   * @param page           页码
   * @param size           每页大小
   * @return 分页消息列表
   */
  PageResponse<Message> getConversationMessages(Long conversationId, Long userId,
      Integer page, Integer size);

  /**
   * 获取对话消息列表（分页，支持排序）
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID（用于权限验证）
   * @param page           分页对象
   * @param sortBy         排序字段
   * @param sortOrder      排序方向
   * @return 分页消息列表
   */
  com.baomidou.mybatisplus.core.metadata.IPage<Message> getConversationMessages(
      Long conversationId, Long userId, Page<Message> page, String sortBy, String sortOrder);

  /**
   * 获取消息详情
   * 
   * @param messageId 消息ID
   * @param userId    用户ID（用于权限验证）
   * @return 消息详情
   */
  Message getMessageById(Long messageId, Long userId);

  /**
   * 删除消息（软删除）
   * 
   * @param messageId 消息ID
   * @param userId    用户ID（用于权限验证）
   * @return 是否删除成功
   */
  Boolean deleteMessage(Long messageId, Long userId);

  /**
   * 批量删除消息
   * 
   * @param messageIds 消息ID列表
   * @param userId     用户ID（用于权限验证）
   * @return 删除成功的数量
   */
  Integer batchDeleteMessages(List<Long> messageIds, Long userId);

  /**
   * 统计对话消息数量
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID（用于权限验证）
   * @return 消息数量
   */
  Long countConversationMessages(Long conversationId, Long userId);

  /**
   * 获取对话最后一条消息
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID（用于权限验证）
   * @return 最后一条消息
   */
  Message getLastMessage(Long conversationId, Long userId);

  /**
   * 获取对话最后一条消息
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID（用于权限验证）
   * @return 最后一条消息
   */
  Message getLastConversationMessage(Long conversationId, Long userId);

  /**
   * 清空对话所有消息
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID（用于权限验证）
   * @return 是否清空成功
   */
  Boolean clearConversationMessages(Long conversationId, Long userId);

  /**
   * 生成AI回复（模拟实现）
   * 
   * @param conversationId 对话ID
   * @param userMessage    用户消息
   * @return AI回复消息
   */
  Message generateAIReply(Long conversationId, Message userMessage);

  /**
   * 生成AI回复（模拟实现）
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID
   * @param userMessage    用户消息内容
   * @return AI回复消息
   */
  Message generateAIReply(Long conversationId, Long userId, String userMessage);

  /**
   * 验证用户是否有权限访问消息
   * 
   * @param messageId 消息ID
   * @param userId    用户ID
   * @return 是否有权限
   */
  Boolean hasMessagePermission(Long messageId, Long userId);

  /**
   * 验证用户是否有权限访问对话消息
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID
   * @return 是否有权限
   */
  Boolean hasConversationMessagePermission(Long conversationId, Long userId);

  /**
   * 更新消息状态
   * 
   * @param messageId 消息ID
   * @param status    新状态
   * @param userId    用户ID（用于权限验证）
   * @return 是否更新成功
   */
  Boolean updateMessageStatus(Long messageId, Message.MessageStatus status, Long userId);

  /**
   * 获取用户在对话中的消息统计
   * 
   * @param conversationId 对话ID
   * @param userId         用户ID
   * @return 消息统计信息
   */
  MessageStatistics getMessageStatistics(Long conversationId, Long userId);
  
  /**
   * 获取用户的消息统计信息
   * 
   * @param userId         用户ID
   * @param conversationId 对话ID（可选）
   * @return 消息统计信息
   */
  MessageStatistics getUserMessageStatistics(Long userId, Long conversationId);

  /**
   * 消息统计信息内部类
   */
  class MessageStatistics {
    private Long totalMessages;
    private Long userMessages;
    private Long aiMessages;
    private Long systemMessages;
    private Long totalTokens;

    // 构造函数
    public MessageStatistics() {
    }

    public MessageStatistics(Long totalMessages, Long userMessages,
        Long aiMessages, Long systemMessages, Long totalTokens) {
      this.totalMessages = totalMessages;
      this.userMessages = userMessages;
      this.aiMessages = aiMessages;
      this.systemMessages = systemMessages;
      this.totalTokens = totalTokens;
    }

    // Getter和Setter方法
    public Long getTotalMessages() {
      return totalMessages;
    }

    public void setTotalMessages(Long totalMessages) {
      this.totalMessages = totalMessages;
    }

    public Long getUserMessages() {
      return userMessages;
    }

    public void setUserMessages(Long userMessages) {
      this.userMessages = userMessages;
    }

    public Long getAiMessages() {
      return aiMessages;
    }

    public void setAiMessages(Long aiMessages) {
      this.aiMessages = aiMessages;
    }

    public Long getSystemMessages() {
      return systemMessages;
    }

    public void setSystemMessages(Long systemMessages) {
      this.systemMessages = systemMessages;
    }

    public Long getTotalTokens() {
      return totalTokens;
    }

    public void setTotalTokens(Long totalTokens) {
      this.totalTokens = totalTokens;
    }
  }
}