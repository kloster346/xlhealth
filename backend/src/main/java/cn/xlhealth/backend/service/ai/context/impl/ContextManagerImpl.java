package cn.xlhealth.backend.service.ai.context.impl;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.entity.Message;
import cn.xlhealth.backend.service.MessageService;
import cn.xlhealth.backend.service.ai.context.ContextManager;
import cn.xlhealth.backend.service.ai.dto.ContextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 上下文管理器实现类
 */
@Service
public class ContextManagerImpl implements ContextManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ContextManagerImpl.class);
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private AIServiceProperties aiServiceProperties;
    
    // 内存缓存，存储活跃对话的上下文
    private final Map<Long, List<ContextMessage>> contextCache = new ConcurrentHashMap<>();
    
    // 缓存过期时间记录
    private final Map<Long, Long> cacheExpireTime = new ConcurrentHashMap<>();
    
    @Override
    public List<ContextMessage> getContext(Long conversationId, int limit) {
        try {
            // 检查缓存
            if (isCacheValid(conversationId)) {
                List<ContextMessage> cachedContext = contextCache.get(conversationId);
                if (cachedContext != null) {
                    logger.debug("Retrieved context from cache for conversation {}", conversationId);
                    return cachedContext.stream()
                            .limit(limit)
                            .collect(Collectors.toList());
                }
            }
            
            // 从数据库获取消息历史
            List<Message> messages = messageService.getConversationMessages(conversationId, limit);
            
            // 转换为上下文消息
            List<ContextMessage> contextMessages = messages.stream()
                    .map(this::convertToContextMessage)
                    .collect(Collectors.toList());
            
            // 计算消息权重
            contextMessages.forEach(msg -> {
                double weight = calculateMessageWeight(msg);
                msg.setWeight(weight);
            });
            
            // 按权重排序
            contextMessages.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
            
            // 更新缓存
            updateCache(conversationId, contextMessages);
            
            logger.debug("Retrieved {} context messages for conversation {}", contextMessages.size(), conversationId);
            return contextMessages;
            
        } catch (Exception e) {
            logger.error("Error retrieving context for conversation {}", conversationId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void addMessage(Long conversationId, ContextMessage message) {
        try {
            // 添加到缓存
            List<ContextMessage> context = contextCache.computeIfAbsent(conversationId, k -> new ArrayList<>());
            
            // 计算权重
            double weight = calculateMessageWeight(message);
            message.setWeight(weight);
            
            context.add(0, message); // 添加到开头
            
            // 限制缓存大小
            int windowSize = aiServiceProperties.getContext().getWindowSize();
            if (context.size() > windowSize) {
                context = context.subList(0, windowSize);
                contextCache.put(conversationId, context);
            }
            
            // 更新过期时间
            updateCacheExpireTime(conversationId);
            
            logger.debug("Added message to context cache for conversation {}", conversationId);
            
        } catch (Exception e) {
            logger.error("Error adding message to context for conversation {}", conversationId, e);
        }
    }
    
    @Override
    public void clearContext(Long conversationId) {
        contextCache.remove(conversationId);
        cacheExpireTime.remove(conversationId);
        logger.debug("Cleared context for conversation {}", conversationId);
    }
    
    @Override
    public String generateContextSummary(Long conversationId) {
        if (!aiServiceProperties.getContext().isSummaryEnabled()) {
            return "";
        }
        
        try {
            List<ContextMessage> context = getContext(conversationId, aiServiceProperties.getContext().getWindowSize());
            
            if (context.isEmpty()) {
                return "暂无对话历史";
            }
            
            StringBuilder summary = new StringBuilder();
            summary.append("对话摘要：\n");
            
            // 统计消息类型
            Map<String, Long> typeCount = context.stream()
                    .collect(Collectors.groupingBy(
                            ContextMessage::getMessageType,
                            Collectors.counting()
                    ));
            
            summary.append(String.format("共 %d 条消息（用户消息：%d，AI回复：%d）\n",
                    context.size(),
                    typeCount.getOrDefault("USER", 0L),
                    typeCount.getOrDefault("AI", 0L)));
            
            // 提取关键信息
            List<String> keyPoints = extractKeyPoints(context);
            if (!keyPoints.isEmpty()) {
                summary.append("关键要点：\n");
                keyPoints.forEach(point -> summary.append("- ").append(point).append("\n"));
            }
            
            return summary.toString();
            
        } catch (Exception e) {
            logger.error("Error generating context summary for conversation {}", conversationId, e);
            return "生成摘要时发生错误";
        }
    }
    
    @Override
    public double calculateMessageWeight(ContextMessage message) {
        double weight = 1.0;
        
        // 基于时间的权重衰减
        long currentTime = System.currentTimeMillis();
        long messageTime = message.getTimestamp();
        long timeDiff = currentTime - messageTime;
        
        // 时间权重：越新的消息权重越高
        double timeWeight = Math.exp(-timeDiff / (24 * 60 * 60 * 1000.0)); // 24小时衰减
        weight *= timeWeight;
        
        // 基于内容长度的权重
        String content = message.getContent();
        if (content != null) {
            double lengthWeight = Math.min(content.length() / 100.0, 2.0); // 最大权重2倍
            weight *= lengthWeight;
        }
        
        // 基于消息类型的权重
        if ("USER".equals(message.getMessageType())) {
            weight *= 1.2; // 用户消息权重稍高
        }
        
        // 基于关键词的权重
        if (content != null && containsImportantKeywords(content)) {
            weight *= 1.5;
        }
        
        return Math.max(weight, 0.1); // 最小权重0.1
    }
    
    @Override
    public int getContextSize(Long conversationId) {
        List<ContextMessage> context = contextCache.get(conversationId);
        return context != null ? context.size() : 0;
    }
    
    /**
     * 转换Message为ContextMessage
     */
    private ContextMessage convertToContextMessage(Message message) {
        ContextMessage contextMessage = new ContextMessage();
        contextMessage.setMessageId(message.getId());
        contextMessage.setContent(message.getContent());
        contextMessage.setMessageType(message.getRole().toString());
        contextMessage.setTimestamp(message.getCreatedTime().toEpochSecond(java.time.ZoneOffset.UTC) * 1000);
        return contextMessage;
    }
    
    /**
     * 检查缓存是否有效
     */
    private boolean isCacheValid(Long conversationId) {
        Long expireTime = cacheExpireTime.get(conversationId);
        return expireTime != null && System.currentTimeMillis() < expireTime;
    }
    
    /**
     * 更新缓存
     */
    private void updateCache(Long conversationId, List<ContextMessage> context) {
        contextCache.put(conversationId, new ArrayList<>(context));
        updateCacheExpireTime(conversationId);
    }
    
    /**
     * 更新缓存过期时间
     */
    private void updateCacheExpireTime(Long conversationId) {
        long expireTime = System.currentTimeMillis() + 
                aiServiceProperties.getContext().getCacheExpireMinutes() * 60 * 1000L;
        cacheExpireTime.put(conversationId, expireTime);
    }
    
    /**
     * 检查是否包含重要关键词
     */
    private boolean containsImportantKeywords(String content) {
        String[] importantKeywords = {
                "抑郁", "焦虑", "自杀", "自害", "痛苦", "绝望",
                "治疗", "药物", "医生", "心理", "咨询",
                "家庭", "工作", "学习", "人际关系"
        };
        
        String lowerContent = content.toLowerCase();
        for (String keyword : importantKeywords) {
            if (lowerContent.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 提取关键要点
     */
    private List<String> extractKeyPoints(List<ContextMessage> context) {
        List<String> keyPoints = new ArrayList<>();
        
        // 简单的关键词提取逻辑
        Set<String> mentionedTopics = new HashSet<>();
        
        for (ContextMessage message : context) {
            String content = message.getContent();
            if (content != null && "USER".equals(message.getMessageType())) {
                // 提取用户提到的主要话题
                if (content.contains("工作") || content.contains("职场")) {
                    mentionedTopics.add("工作相关问题");
                }
                if (content.contains("家庭") || content.contains("父母") || content.contains("孩子")) {
                    mentionedTopics.add("家庭关系");
                }
                if (content.contains("朋友") || content.contains("同事") || content.contains("人际")) {
                    mentionedTopics.add("人际关系");
                }
                if (content.contains("焦虑") || content.contains("紧张") || content.contains("担心")) {
                    mentionedTopics.add("焦虑情绪");
                }
                if (content.contains("抑郁") || content.contains("难过") || content.contains("伤心")) {
                    mentionedTopics.add("抑郁情绪");
                }
            }
        }
        
        keyPoints.addAll(mentionedTopics);
        return keyPoints;
    }
}