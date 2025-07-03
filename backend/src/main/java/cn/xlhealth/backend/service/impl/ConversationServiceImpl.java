package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.entity.Conversation;
import cn.xlhealth.backend.mapper.ConversationMapper;
import cn.xlhealth.backend.service.ConversationService;
import cn.xlhealth.backend.ui.advice.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话服务实现类
 */
@Service
@Transactional
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements ConversationService {

    @Override
    public Conversation createConversation(Long userId, String title) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(title);
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        conversation.setMessageCount(0);
        conversation.setTotalTokens(0);
        conversation.setStartTime(LocalDateTime.now());
        conversation.setCreatedTime(LocalDateTime.now());
        conversation.setUpdatedTime(LocalDateTime.now());
        conversation.setDeleted(false);

        save(conversation);
        return conversation;
    }

    @Override
    public IPage<Conversation> getUserConversations(Long userId, Page<Conversation> page) {
        return baseMapper.findByUserId(page, userId);
    }

    @Override
    public Conversation getConversationById(Long conversationId, Long userId) {
        QueryWrapper<Conversation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", conversationId)
                .eq("user_id", userId)
                .eq("deleted", false);
        return getOne(queryWrapper);
    }

    @Override
    public Conversation updateConversation(Long conversationId, Long userId, String title,
            Conversation.ConversationStatus status) {
        Conversation conversation = getConversationById(conversationId, userId);
        if (conversation == null) {
            throw BusinessException.notFound("对话不存在或无权限访问");
        }

        if (title != null) {
            conversation.setTitle(title);
        }
        if (status != null) {
            conversation.setStatus(status);
        }
        conversation.setUpdatedTime(LocalDateTime.now());

        updateById(conversation);
        return conversation;
    }

    @Override
    public boolean deleteConversation(Long conversationId, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);
        if (conversation == null) {
            return false;
        }

        return baseMapper.softDelete(conversationId) > 0;
    }

    @Override
    public boolean archiveConversation(Long conversationId, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);
        if (conversation == null) {
            return false;
        }

        return baseMapper.updateStatus(conversationId, Conversation.ConversationStatus.ARCHIVED) > 0;
    }

    @Override
    public boolean activateConversation(Long conversationId, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);
        if (conversation == null) {
            return false;
        }

        return baseMapper.updateStatus(conversationId, Conversation.ConversationStatus.ACTIVE) > 0;
    }

    @Override
    public Long countUserConversations(Long userId) {
        return baseMapper.countByUserId(userId);
    }

    @Override
    public List<Conversation> getUserConversationsByStatus(Long userId, Conversation.ConversationStatus status) {
        return baseMapper.findByUserIdAndStatus(userId, status);
    }

    @Override
    public boolean updateConversationStatistics(Long conversationId, Integer messageCount, Integer totalTokens) {
        return baseMapper.updateStatistics(conversationId, messageCount, totalTokens) > 0;
    }
}