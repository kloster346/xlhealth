package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.entity.Conversation;
import cn.xlhealth.backend.entity.Message;
import cn.xlhealth.backend.mapper.MessageMapper;
import cn.xlhealth.backend.service.ConversationService;
import cn.xlhealth.backend.service.MessageService;
import cn.xlhealth.backend.ui.advice.BusinessException;
import cn.xlhealth.backend.ui.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationService conversationService;

    @Override
    public Message generateAIReply(Long conversationId, Message userMessage) {
        log.info("生成AI回复: conversationId={}, userMessageId={}", conversationId, userMessage.getId());
        
        // 创建AI回复消息
        Message aiMessage = new Message();
        aiMessage.setConversationId(conversationId);
        aiMessage.setUserId(userMessage.getUserId());
        aiMessage.setRole(Message.MessageRole.ASSISTANT);
        aiMessage.setContentType(Message.ContentType.TEXT);
        aiMessage.setStatus(Message.MessageStatus.PROCESSING);
        
        // 这里应该调用AI服务生成回复，暂时使用简单的回复
        String aiReply = "感谢您的消息，我正在为您分析...";
        aiMessage.setContent(aiReply);
        aiMessage.setTokenCount(aiReply.length());
        
        // 保存AI消息
        messageMapper.insert(aiMessage);
        
        // 更新状态为成功
        aiMessage.setStatus(Message.MessageStatus.SUCCESS);
        messageMapper.updateById(aiMessage);
        
        log.info("AI回复生成完成: messageId={}", aiMessage.getId());
        return aiMessage;
    }

    @Override
    public Message generateAIReply(Long conversationId, Long userId, String userMessage) {
        log.info("生成AI回复: conversationId={}, userId={}", conversationId, userId);
        
        // 验证用户是否有权限访问对话
        validateUserAccessToConversationMessages(conversationId, userId);
        
        // 创建AI回复消息
        Message aiMessage = new Message();
        aiMessage.setConversationId(conversationId);
        aiMessage.setUserId(userId); // AI消息关联到当前用户
        aiMessage.setRole(Message.MessageRole.ASSISTANT);
        aiMessage.setContentType(Message.ContentType.TEXT);
        aiMessage.setStatus(Message.MessageStatus.PROCESSING);
        
        // 模拟AI回复生成（实际实现时需要调用AI服务）
        String aiReply = "这是一个模拟的AI回复，针对用户消息: " + userMessage;
        aiMessage.setContent(aiReply);
        aiMessage.setTokenCount(aiReply.length());
        aiMessage.setCreatedTime(LocalDateTime.now());
        aiMessage.setUpdatedTime(LocalDateTime.now());
        aiMessage.setDeleted(false);
        
        // 保存AI消息
        messageMapper.insert(aiMessage);
        
        // 更新状态为成功
        aiMessage.setStatus(Message.MessageStatus.SUCCESS);
        messageMapper.updateById(aiMessage);
        
        // 更新对话统计信息
        updateConversationStatistics(conversationId);
        
        log.info("AI回复生成完成: messageId={}", aiMessage.getId());
        return aiMessage;
    }

    @Override
    @Transactional
    public Message sendMessage(Long conversationId, Long userId, String content, 
                              Message.MessageRole role, Message.ContentType contentType, String metadata) {
        log.info("发送消息: conversationId={}, userId={}, role={}, contentType={}", 
                conversationId, userId, role, contentType);
        
        // 验证用户是否有权限访问对话
        validateUserAccessToConversationMessages(conversationId, userId);
        
        // 创建消息对象
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setContentType(contentType);
        message.setMetadata(metadata);
        message.setStatus(Message.MessageStatus.SUCCESS);
        message.setCreatedTime(LocalDateTime.now());
        message.setUpdatedTime(LocalDateTime.now());
        message.setDeleted(false);
        
        // 保存消息到数据库
        int result = messageMapper.insert(message);
        if (result <= 0) {
            throw new BusinessException("消息发送失败");
        }
        
        // 更新对话统计信息
        updateConversationStatistics(conversationId);
        
        // 如果是用户消息，可能需要触发AI回复
        if (role == Message.MessageRole.USER) {
            // 这里可以异步触发AI回复生成
            log.info("用户消息已发送，可触发AI回复生成");
        }
        
        log.info("消息发送成功: messageId={}", message.getId());
        return message;
    }

    @Override
    public PageResponse<Message> getConversationMessages(Long conversationId, Long userId, 
                                                        Integer page, Integer size) {
        Page<Message> pageObj = new Page<>(page, size);
        IPage<Message> result = getConversationMessages(conversationId, userId, pageObj, "created_time", "asc");
        
        PageResponse<Message> pageResponse = PageResponse.<Message>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .current(result.getCurrent())
                .size((long)result.getSize())
                .build();
        pageResponse.calculateAll();
        return pageResponse;
    }
    
    @Override
    public IPage<Message> getConversationMessages(Long conversationId, Long userId, 
                                                      Page<Message> page, String sortBy, String sortOrder) {
        log.info("获取对话消息列表: conversationId={}, userId={}, page={}, size={}", 
                conversationId, userId, page.getCurrent(), page.getSize());
        
        // 验证用户是否有权限访问对话消息
        validateUserAccessToConversationMessages(conversationId, userId);
        
        // 构建查询条件
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
             queryWrapper.eq("conversation_id", conversationId)
                        .eq("deleted", false);
        
        // 设置排序
        if ("desc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByDesc(sortBy != null ? sortBy : "created_time");
        } else {
            queryWrapper.orderByAsc(sortBy != null ? sortBy : "created_time");
        }
        
        // 执行分页查询
        IPage<Message> result = messageMapper.selectPage(page, queryWrapper);
        
        log.info("获取对话消息列表成功: total={}, records={}", 
                result.getTotal(), result.getRecords().size());
        return result;
    }

    @Override
    public Message getMessageById(Long messageId, Long userId) {
        log.info("获取消息详情: messageId={}, userId={}", messageId, userId);
        
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted()) {
            throw new BusinessException("消息不存在");
        }
        
        // 验证用户是否有权限访问该消息
        validateUserAccessToMessage(messageId, userId);
        
        log.info("获取消息详情成功: messageId={}", messageId);
        return message;
    }

    @Override
    @Transactional
    public Boolean deleteMessage(Long messageId, Long userId) {
        log.info("删除消息: messageId={}, userId={}", messageId, userId);
        
        // 验证用户是否有权限访问该消息
        validateUserAccessToMessage(messageId, userId);
        
        // 软删除消息
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", messageId)
                    .eq("deleted", false)
                    .set("deleted", true)
                    .set("updated_time", LocalDateTime.now());
        
        int result = messageMapper.update(null, updateWrapper);
        boolean success = result > 0;
        
        if (success) {
            // 更新对话统计信息
            Message message = messageMapper.selectById(messageId);
            if (message != null) {
                updateConversationStatistics(message.getConversationId());
            }
            log.info("删除消息成功: messageId={}", messageId);
        } else {
            log.warn("删除消息失败: messageId={}", messageId);
        }
        
        return success;
    }

    @Override
    @Transactional
    public Integer batchDeleteMessages(List<Long> messageIds, Long userId) {
        log.info("批量删除消息: messageIds={}, userId={}", messageIds, userId);
        
        if (messageIds == null || messageIds.isEmpty()) {
            return 0;
        }
        
        // 验证所有消息的权限
        for (Long messageId : messageIds) {
            validateUserAccessToMessage(messageId, userId);
        }
        
        // 批量软删除
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", messageIds)
                    .eq("deleted", false)
                    .set("deleted", true)
                    .set("updated_time", LocalDateTime.now());
        
        int result = messageMapper.update(null, updateWrapper);
        
        if (result > 0) {
            // 更新相关对话的统计信息
            QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
            queryWrapper.in("id", messageIds).eq("deleted", true);
            List<Message> deletedMessages = messageMapper.selectList(queryWrapper);
            
            deletedMessages.stream()
                          .map(Message::getConversationId)
                          .distinct()
                          .forEach(this::updateConversationStatistics);
            
            log.info("批量删除消息成功: count={}", result);
        } else {
            log.warn("批量删除消息失败: messageIds={}", messageIds);
        }
        
        return result;
    }
    
    @Transactional
    public boolean batchDeleteMessagesOld(List<Long> messageIds, Long userId) {
        log.info("批量删除消息: messageIds={}, userId={}", messageIds, userId);
        
        if (messageIds == null || messageIds.isEmpty()) {
            return true;
        }
        
        // 验证所有消息的权限
        for (Long messageId : messageIds) {
            validateUserAccessToMessage(messageId, userId);
        }
        
        // 批量软删除
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", messageIds)
                    .eq("deleted", false)
                    .set("deleted", true)
                    .set("updated_time", LocalDateTime.now());
        
        int result = messageMapper.update(null, updateWrapper);
        boolean success = result > 0;
        
        if (success) {
            // 更新相关对话的统计信息
            QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
            queryWrapper.in("id", messageIds).eq("deleted", true);
            List<Message> deletedMessages = messageMapper.selectList(queryWrapper);
            
            deletedMessages.stream()
                          .map(Message::getConversationId)
                          .distinct()
                          .forEach(this::updateConversationStatistics);
            
            log.info("批量删除消息成功: count={}", result);
        } else {
            log.warn("批量删除消息失败: messageIds={}", messageIds);
        }
        
        return success;
    }

    @Override
    public Message getLastMessage(Long conversationId, Long userId) {
        log.info("获取对话最后一条消息: conversationId={}, userId={}", conversationId, userId);
        
        // 验证用户是否有权限访问对话消息
        validateUserAccessToConversationMessages(conversationId, userId);
        
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
        queryWrapper.eq("conversation_id", conversationId)
                   .eq("deleted", false)
                   .orderByDesc("created_time")
                   .last("LIMIT 1");
        
        Message lastMessage = messageMapper.selectOne(queryWrapper);
        
        log.info("获取对话最后一条消息成功: conversationId={}, messageId={}", 
                conversationId, lastMessage != null ? lastMessage.getId() : null);
        return lastMessage;
    }



    @Override
    public Long countConversationMessages(Long conversationId, Long userId) {
        log.info("统计对话消息数量: conversationId={}, userId={}", conversationId, userId);
        
        // 验证用户是否有权限访问对话消息
        validateUserAccessToConversationMessages(conversationId, userId);
        
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
         queryWrapper.eq("conversation_id", conversationId)
                    .eq("deleted", false);
         
         Long count = messageMapper.selectCount(queryWrapper);
        log.info("统计对话消息数量成功: conversationId={}, count={}", conversationId, count);
        return count;
    }

    @Override
    public Message getLastConversationMessage(Long conversationId, Long userId) {
        log.info("获取对话最后一条消息: conversationId={}, userId={}", conversationId, userId);
        
        // 验证用户是否有权限访问对话消息
        validateUserAccessToConversationMessages(conversationId, userId);
        
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
        queryWrapper.eq("conversation_id", conversationId)
                   .eq("deleted", false)
                   .orderByDesc("created_time")
                   .last("LIMIT 1");
        
        Message lastMessage = messageMapper.selectOne(queryWrapper);
        log.info("获取对话最后一条消息成功: conversationId={}, messageId={}", 
                conversationId, lastMessage != null ? lastMessage.getId() : null);
        return lastMessage;
    }

    @Override
    @Transactional
    public Boolean clearConversationMessages(Long conversationId, Long userId) {
        log.info("清空对话所有消息: conversationId={}, userId={}", conversationId, userId);
        
        // 验证用户是否有权限访问对话消息
        validateUserAccessToConversationMessages(conversationId, userId);
        
        // 软删除对话中的所有消息
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("conversation_id", conversationId)
                    .eq("deleted", false)
                    .set("deleted", true)
                    .set("updated_time", LocalDateTime.now());
        
        int result = messageMapper.update(null, updateWrapper);
        
        // 更新对话统计信息
        updateConversationStatistics(conversationId);
        
        log.info("清空对话所有消息成功: conversationId={}, deletedCount={}", conversationId, result);
        return Boolean.valueOf(result >= 0); // 即使没有消息也算成功
    }



    @Override
    public Boolean hasMessagePermission(Long messageId, Long userId) {
        return Boolean.valueOf(validateUserAccessToMessage(messageId, userId));
    }
    
    public boolean validateUserAccessToMessage(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted()) {
            throw new BusinessException("消息不存在");
        }
        
        // 检查用户是否有权限访问该消息所属的对话
        return validateUserAccessToConversationMessages(message.getConversationId(), userId);
    }

    @Override
    public Boolean hasConversationMessagePermission(Long conversationId, Long userId) {
        return Boolean.valueOf(validateUserAccessToConversationMessages(conversationId, userId));
    }
    
    public boolean validateUserAccessToConversationMessages(Long conversationId, Long userId) {
        try {
            // 通过ConversationService验证用户是否有权限访问对话
            Conversation conversation = conversationService.getConversationById(conversationId, userId);
            if (conversation == null) {
                throw new BusinessException("对话不存在或无权限访问");
            }
            
            // 检查对话状态，只有ACTIVE状态的对话才能进行消息操作
            if (conversation.getStatus() != Conversation.ConversationStatus.ACTIVE) {
                String statusDesc = conversation.getStatus() == Conversation.ConversationStatus.ARCHIVED ? "已归档" : "已删除";
                throw new BusinessException("对话" + statusDesc + "，无法进行消息操作");
            }
            
            return true;
        } catch (BusinessException e) {
            // 重新抛出BusinessException
            throw e;
        } catch (Exception e) {
            log.warn("用户无权限访问对话消息: conversationId={}, userId={}, error={}", 
                    conversationId, userId, e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Boolean updateMessageStatus(Long messageId, Message.MessageStatus status, Long userId) {
        // 验证用户权限
        validateUserAccessToMessage(messageId, userId);
        return Boolean.valueOf(updateMessageStatusInternal(messageId, status));
    }
    
    @Transactional
    public boolean updateMessageStatusInternal(Long messageId, Message.MessageStatus status) {
        log.info("更新消息状态: messageId={}, status={}", messageId, status);
        
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", messageId)
                    .set("status", status)
                    .set("updated_time", LocalDateTime.now());
        
        int result = messageMapper.update(null, updateWrapper);
        boolean success = result > 0;
        
        if (success) {
            log.info("更新消息状态成功: messageId={}, status={}", messageId, status);
        } else {
            log.warn("更新消息状态失败: messageId={}, status={}", messageId, status);
        }
        
        return success;
    }

    @Override
    public MessageService.MessageStatistics getMessageStatistics(Long conversationId, Long userId) {
        return getUserMessageStatistics(userId, conversationId);
    }
    
    public MessageStatistics getUserMessageStatistics(Long userId, Long conversationId) {
        log.info("获取用户消息统计: userId={}, conversationId={}", userId, conversationId);
        
        if (conversationId != null) {
            // 验证用户是否有权限访问对话
            validateUserAccessToConversationMessages(conversationId, userId);
        }
        
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", false);
        
        if (conversationId != null) {
            queryWrapper.eq("conversation_id", conversationId);
        }
        
        // 统计总消息数
        Long totalMessages = messageMapper.selectCount(queryWrapper);
        
        // 统计各角色消息数
        QueryWrapper<Message> userQueryWrapper = new QueryWrapper<Message>();
        userQueryWrapper.eq("user_id", userId)
                       .eq("deleted", false)
                       .eq("role", Message.MessageRole.USER);
        if (conversationId != null) {
            userQueryWrapper.eq("conversation_id", conversationId);
        }
        Long userMessages = messageMapper.selectCount(userQueryWrapper);
        
        QueryWrapper<Message> assistantQueryWrapper = new QueryWrapper<Message>();
        assistantQueryWrapper.eq("user_id", userId)
                            .eq("deleted", false)
                            .eq("role", Message.MessageRole.ASSISTANT);
        if (conversationId != null) {
            assistantQueryWrapper.eq("conversation_id", conversationId);
        }
        Long assistantMessages = messageMapper.selectCount(assistantQueryWrapper);
        
        QueryWrapper<Message> systemQueryWrapper = new QueryWrapper<Message>();
        systemQueryWrapper.eq("user_id", userId)
                         .eq("deleted", false)
                         .eq("role", Message.MessageRole.SYSTEM);
        if (conversationId != null) {
            systemQueryWrapper.eq("conversation_id", conversationId);
        }
        Long systemMessages = messageMapper.selectCount(systemQueryWrapper);
        
        // 统计总token数
        QueryWrapper<Message> tokenQueryWrapper = new QueryWrapper<Message>();
        tokenQueryWrapper.eq("user_id", userId)
                        .eq("deleted", false);
        if (conversationId != null) {
            tokenQueryWrapper.eq("conversation_id", conversationId);
        }
        tokenQueryWrapper.select("COALESCE(SUM(token_count), 0) as total_tokens");
        List<Message> tokenResult = messageMapper.selectList(tokenQueryWrapper);
        Long totalTokens = 0L;
        if (!tokenResult.isEmpty() && tokenResult.get(0).getTokenCount() != null) {
            totalTokens = Long.valueOf(tokenResult.get(0).getTokenCount());
        }
        
        MessageService.MessageStatistics statistics = new MessageService.MessageStatistics();
        statistics.setTotalMessages(totalMessages);
        statistics.setUserMessages(userMessages);
        statistics.setAiMessages(assistantMessages);
        statistics.setSystemMessages(systemMessages);
        statistics.setTotalTokens(totalTokens);
        
        log.info("获取用户消息统计成功: userId={}, statistics={}", userId, statistics);
        return statistics;
    }
    
    /**
     * 更新对话统计信息
     */
    private void updateConversationStatistics(Long conversationId) {
        try {
            // 统计对话中的消息数量
            QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>();
        queryWrapper.eq("conversation_id", conversationId)
                   .eq("deleted", false);
            Long messageCount = messageMapper.selectCount(queryWrapper);
            
            // 统计总token数
            QueryWrapper<Message> tokenQueryWrapper = new QueryWrapper<Message>();
            tokenQueryWrapper.eq("conversation_id", conversationId)
                            .eq("deleted", false)
                            .select("COALESCE(SUM(token_count), 0) as total_tokens");
            List<Message> tokenResult = messageMapper.selectList(tokenQueryWrapper);
            Integer totalTokens = 0;
            if (!tokenResult.isEmpty() && tokenResult.get(0).getTokenCount() != null) {
                totalTokens = tokenResult.get(0).getTokenCount();
            }
            
            // 更新对话统计信息
            conversationService.updateConversationStatistics(conversationId, 
                    messageCount.intValue(), totalTokens);
            
            log.debug("更新对话统计信息成功: conversationId={}, messageCount={}, totalTokens={}", 
                     conversationId, messageCount, totalTokens);
        } catch (Exception e) {
            log.error("更新对话统计信息失败: conversationId={}, error={}", conversationId, e.getMessage(), e);
        }
    }
    

}