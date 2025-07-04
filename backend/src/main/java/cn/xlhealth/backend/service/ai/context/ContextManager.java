package cn.xlhealth.backend.service.ai.context;

import cn.xlhealth.backend.service.ai.dto.ContextMessage;

import java.util.List;

/**
 * 消息上下文管理器接口
 * 负责管理对话历史和上下文信息
 */
public interface ContextManager {
    
    /**
     * 获取对话上下文
     * @param conversationId 对话ID
     * @param limit 最大消息数量
     * @return 上下文消息列表
     */
    List<ContextMessage> getContext(Long conversationId, int limit);
    
    /**
     * 添加消息到上下文
     * @param conversationId 对话ID
     * @param message 上下文消息
     */
    void addMessage(Long conversationId, ContextMessage message);
    
    /**
     * 清除对话上下文
     * @param conversationId 对话ID
     */
    void clearContext(Long conversationId);
    
    /**
     * 生成上下文摘要
     * @param conversationId 对话ID
     * @return 上下文摘要
     */
    String generateContextSummary(Long conversationId);
    
    /**
     * 计算消息权重
     * @param message 上下文消息
     * @return 权重值
     */
    double calculateMessageWeight(ContextMessage message);
    
    /**
     * 获取上下文大小
     * @param conversationId 对话ID
     * @return 上下文消息数量
     */
    int getContextSize(Long conversationId);
}