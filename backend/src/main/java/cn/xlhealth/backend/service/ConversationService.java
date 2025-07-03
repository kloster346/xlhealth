package cn.xlhealth.backend.service;

import cn.xlhealth.backend.entity.Conversation;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 对话服务接口
 */
public interface ConversationService {

    /**
     * 创建新对话
     *
     * @param userId 用户ID
     * @param title 对话标题
     * @return 创建的对话
     */
    Conversation createConversation(Long userId, String title);

    /**
     * 获取用户的对话列表（分页）
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 对话列表
     */
    IPage<Conversation> getUserConversations(Long userId, Page<Conversation> page);

    /**
     * 根据ID获取对话详情
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @return 对话详情
     */
    Conversation getConversationById(Long conversationId, Long userId);

    /**
     * 更新对话信息
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @param title 新标题
     * @param status 新状态
     * @return 更新后的对话
     */
    Conversation updateConversation(Long conversationId, Long userId, String title, Conversation.ConversationStatus status);

    /**
     * 删除对话（软删除）
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deleteConversation(Long conversationId, Long userId);

    /**
     * 归档对话
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否归档成功
     */
    boolean archiveConversation(Long conversationId, Long userId);

    /**
     * 激活对话
     *
     * @param conversationId 对话ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否激活成功
     */
    boolean activateConversation(Long conversationId, Long userId);

    /**
     * 统计用户对话数量
     *
     * @param userId 用户ID
     * @return 对话数量
     */
    Long countUserConversations(Long userId);

    /**
     * 根据状态获取用户对话列表
     *
     * @param userId 用户ID
     * @param status 对话状态
     * @return 对话列表
     */
    List<Conversation> getUserConversationsByStatus(Long userId, Conversation.ConversationStatus status);

    /**
     * 更新对话统计信息
     *
     * @param conversationId 对话ID
     * @param messageCount 消息数量
     * @param totalTokens 总token数
     * @return 是否更新成功
     */
    boolean updateConversationStatistics(Long conversationId, Integer messageCount, Integer totalTokens);
}