package cn.xlhealth.backend.service.ai;

import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;

/**
 * AI服务接口
 * 定义统一的AI服务调用接口，支持模拟实现和真实AI服务集成
 */
public interface AIService {
    
    /**
     * 生成AI回复
     * @param request AI请求对象，包含用户消息、上下文等信息
     * @return AI回复响应
     */
    AIResponse generateReply(AIRequest request);
    
    /**
     * 检查AI服务健康状态
     * @return 服务是否可用
     */
    boolean isHealthy();
    
    /**
     * 获取AI服务提供商名称
     * @return 提供商名称
     */
    String getProviderName();
    
    /**
     * 获取AI服务配置信息
     * @return 配置信息
     */
    java.util.Map<String, Object> getConfigInfo();
}