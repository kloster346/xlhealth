package cn.xlhealth.backend.service.ai.monitor;

import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;

/**
 * AI服务监控器接口
 * 负责监控AI服务的健康状态和性能指标
 */
public interface ServiceMonitor {
    
    /**
     * 记录AI服务调用
     * @param request AI请求
     * @param response AI回复
     * @param responseTime 响应时间（毫秒）
     */
    void recordCall(AIRequest request, AIResponse response, long responseTime);
    
    /**
     * 记录AI服务错误
     * @param request AI请求
     * @param error 错误信息
     * @param responseTime 响应时间（毫秒）
     */
    void recordError(AIRequest request, Throwable error, long responseTime);
    
    /**
     * 获取成功率
     * @param provider AI服务提供商
     * @param timeWindowMinutes 时间窗口（分钟）
     * @return 成功率（0-100）
     */
    double getSuccessRate(String provider, int timeWindowMinutes);
    
    /**
     * 获取平均响应时间
     * @param provider AI服务提供商
     * @param timeWindowMinutes 时间窗口（分钟）
     * @return 平均响应时间（毫秒）
     */
    double getAverageResponseTime(String provider, int timeWindowMinutes);
    
    /**
     * 获取错误率
     * @param provider AI服务提供商
     * @param timeWindowMinutes 时间窗口（分钟）
     * @return 错误率（0-100）
     */
    double getErrorRate(String provider, int timeWindowMinutes);
    
    /**
     * 检查服务健康状态
     * @param provider AI服务提供商
     * @return 是否健康
     */
    boolean isHealthy(String provider);
    
    /**
     * 获取服务统计信息
     * @param provider AI服务提供商
     * @return 统计信息
     */
    ServiceStats getStats(String provider);
    
    /**
     * 重置统计信息
     * @param provider AI服务提供商
     */
    void resetStats(String provider);
}