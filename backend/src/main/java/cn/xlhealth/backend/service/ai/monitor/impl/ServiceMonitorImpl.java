package cn.xlhealth.backend.service.ai.monitor.impl;

import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.monitor.ServiceMonitor;
import cn.xlhealth.backend.service.ai.monitor.ServiceStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务监控器实现类
 */
@Service
public class ServiceMonitorImpl implements ServiceMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceMonitorImpl.class);
    
    // 存储各个服务提供商的统计信息
    private final ConcurrentHashMap<String, ServiceStats> providerStats = new ConcurrentHashMap<>();
    
    // 全局统计信息
    private final ServiceStats globalStats = new ServiceStats("GLOBAL");
    
    // 健康检查阈值
    private static final double MIN_SUCCESS_RATE = 0.8; // 最小成功率80%
    private static final long MAX_RESPONSE_TIME = 5000; // 最大响应时间5秒
    private static final double MAX_ERROR_RATE = 0.2; // 最大错误率20%
    
    @Override
    public void recordCall(AIRequest request, AIResponse response, long responseTime) {
        if (response == null) {
            logger.warn("Cannot record call with null response");
            return;
        }
        
        String provider = response.getProvider();
        if (provider == null) {
            provider = "UNKNOWN";
        }
        
        try {
            // 获取或创建提供商统计信息
            ServiceStats stats = providerStats.computeIfAbsent(provider, ServiceStats::new);
            
            // 记录调用
            if (response.isSuccess()) {
                stats.recordSuccess(responseTime, response.getQualityScore());
                globalStats.recordSuccess(responseTime, response.getQualityScore());
                
                logger.debug("Recorded successful call for provider: {}, response time: {}ms, quality: {}", 
                        provider, responseTime, response.getQualityScore());
            } else {
                stats.recordFailure(responseTime);
                globalStats.recordFailure(responseTime);
                
                logger.debug("Recorded failed call for provider: {}, response time: {}ms, error: {}", 
                        provider, responseTime, response.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error recording call for provider: " + provider, e);
        }
    }
    
    @Override
    public void recordError(AIRequest request, Throwable error, long responseTime) {
        String provider = "UNKNOWN";
        String errorMessage = error != null ? error.getMessage() : "Unknown error";
        
        try {
            // 获取或创建提供商统计信息
            ServiceStats stats = providerStats.computeIfAbsent(provider, ServiceStats::new);
            
            // 记录错误
            stats.recordFailure(responseTime);
            globalStats.recordFailure(responseTime);
            
            logger.warn("Recorded error for provider: {}, error: {}, response time: {}ms", 
                    provider, errorMessage, responseTime);
            
        } catch (Exception e) {
            logger.error("Error recording error for provider: " + provider, e);
        }
    }
    
    public void recordError(String provider, String errorMessage, long responseTime) {
        if (provider == null) {
            provider = "UNKNOWN";
        }
        
        try {
            // 获取或创建提供商统计信息
            ServiceStats stats = providerStats.computeIfAbsent(provider, ServiceStats::new);
            
            // 记录错误
            stats.recordFailure(responseTime);
            globalStats.recordFailure(responseTime);
            
            logger.warn("Recorded error for provider: {}, error: {}, response time: {}ms", 
                    provider, errorMessage, responseTime);
            
        } catch (Exception e) {
            logger.error("Error recording error for provider: " + provider, e);
        }
    }
    
    @Override
    public double getSuccessRate(String provider, int timeWindowMinutes) {
        if (provider == null) {
            return globalStats.getSuccessRate();
        }
        
        ServiceStats stats = providerStats.get(provider);
        if (stats == null) {
            logger.debug("No stats found for provider: {}", provider);
            return 0.0;
        }
        
        return stats.getSuccessRate();
    }
    
    @Override
    public double getAverageResponseTime(String provider, int timeWindowMinutes) {
        if (provider == null) {
            return globalStats.getAverageResponseTime();
        }
        
        ServiceStats stats = providerStats.get(provider);
        if (stats == null) {
            logger.debug("No stats found for provider: {}", provider);
            return 0.0;
        }
        
        return stats.getAverageResponseTime();
    }
    
    @Override
    public double getErrorRate(String provider, int timeWindowMinutes) {
        if (provider == null) {
            return globalStats.getErrorRate();
        }
        
        ServiceStats stats = providerStats.get(provider);
        if (stats == null) {
            logger.debug("No stats found for provider: {}", provider);
            return 0.0;
        }
        
        return stats.getErrorRate();
    }
    
    @Override
    public boolean isHealthy(String provider) {
        try {
            double successRate = getSuccessRate(provider, 60); // 默认使用60分钟时间窗口
            double averageResponseTime = getAverageResponseTime(provider, 60);
            double errorRate = getErrorRate(provider, 60);
            
            boolean healthy = successRate >= MIN_SUCCESS_RATE && 
                    averageResponseTime <= MAX_RESPONSE_TIME && 
                    errorRate <= MAX_ERROR_RATE;
            
            if (!healthy) {
                logger.warn("Provider {} is unhealthy: success rate: {}, avg response time: {}ms, error rate: {}", 
                        provider, successRate, averageResponseTime, errorRate);
            }
            
            return healthy;
            
        } catch (Exception e) {
            logger.error("Error checking health for provider: " + provider, e);
            return false;
        }
    }
    
    @Override
    public ServiceStats getStats(String provider) {
        if (provider == null) {
            return globalStats;
        }
        
        ServiceStats stats = providerStats.get(provider);
        if (stats == null) {
            logger.debug("No stats found for provider: {}, returning empty stats", provider);
            return new ServiceStats(provider);
        }
        
        return stats;
    }
    
    @Override
    public void resetStats(String provider) {
        try {
            if (provider == null) {
                // 重置全局统计
                globalStats.reset();
                logger.info("Global stats reset");
            } else {
                // 重置特定提供商统计
                ServiceStats stats = providerStats.get(provider);
                if (stats != null) {
                    stats.reset();
                    logger.info("Stats reset for provider: {}", provider);
                } else {
                    logger.warn("No stats found to reset for provider: {}", provider);
                }
            }
        } catch (Exception e) {
            logger.error("Error resetting stats for provider: " + provider, e);
        }
    }
    
    /**
     * 获取所有提供商的统计信息
     */
    public ConcurrentHashMap<String, ServiceStats> getAllProviderStats() {
        return new ConcurrentHashMap<>(providerStats);
    }
    
    /**
     * 获取全局统计信息
     */
    public ServiceStats getGlobalStats() {
        return globalStats;
    }
    
    /**
     * 清理过期的统计信息
     */
    public void cleanupExpiredStats() {
        try {
            long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 保留24小时内的数据
            
            providerStats.entrySet().removeIf(entry -> {
                ServiceStats stats = entry.getValue();
                boolean expired = stats.getLastUpdated() < cutoffTime && 
                        stats.getTotalCalls() == 0;
                
                if (expired) {
                    logger.debug("Removing expired stats for provider: {}", entry.getKey());
                }
                
                return expired;
            });
            
        } catch (Exception e) {
            logger.error("Error cleaning up expired stats", e);
        }
    }
    
    /**
     * 获取健康检查摘要
     */
    public String getHealthSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Service Health Summary:\n");
        
        // 全局健康状态
        boolean globalHealthy = isHealthy(null);
        summary.append(String.format("Global: %s (Success Rate: %.2f%%, Avg Response: %.0fms, Error Rate: %.2f%%)\n",
                globalHealthy ? "HEALTHY" : "UNHEALTHY",
                getSuccessRate(null, 60),
                getAverageResponseTime(null, 60),
                getErrorRate(null, 60)));
        
        // 各提供商健康状态
        for (String provider : providerStats.keySet()) {
            boolean providerHealthy = isHealthy(provider);
            summary.append(String.format("%s: %s (Success Rate: %.2f%%, Avg Response: %.0fms, Error Rate: %.2f%%)\n",
                    provider,
                    providerHealthy ? "HEALTHY" : "UNHEALTHY",
                    getSuccessRate(provider, 60),
                    getAverageResponseTime(provider, 60),
                    getErrorRate(provider, 60)));
        }
        
        return summary.toString();
    }
}