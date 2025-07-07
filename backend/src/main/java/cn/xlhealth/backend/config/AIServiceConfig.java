package cn.xlhealth.backend.config;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.AIService;
import cn.xlhealth.backend.service.ai.context.ContextManager;
import cn.xlhealth.backend.service.ai.context.impl.ContextManagerImpl;
import cn.xlhealth.backend.service.ai.impl.MockAIService;
import cn.xlhealth.backend.service.ai.impl.DeepSeekAIService;
import cn.xlhealth.backend.service.ai.monitor.ServiceMonitor;
import cn.xlhealth.backend.service.ai.monitor.impl.ServiceMonitorImpl;
import cn.xlhealth.backend.service.ai.quality.QualityAssessor;
import cn.xlhealth.backend.service.ai.quality.impl.QualityAssessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * AI服务配置类
 */
@Configuration
@EnableConfigurationProperties(AIServiceProperties.class)
@EnableScheduling
public class AIServiceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AIServiceConfig.class);
    
    /**
     * AI服务Bean
     * 根据配置决定使用哪种实现
     */
    @Bean
    @Primary
    public AIService aiService(AIServiceProperties properties) {
        logger.info("Initializing AI Service with provider: {}, mock mode: {}", 
                properties.getProvider(), properties.isMockMode());
        
        // 如果启用模拟模式，直接返回Mock服务
        if (properties.isMockMode()) {
            logger.info("Mock mode enabled, using MockAIService");
            return new MockAIService();
        }
        
        // 根据provider类型创建不同的实现
        String provider = properties.getProvider().toUpperCase();
        switch (provider) {
            case "MOCK":
                logger.info("Using MockAIService");
                return new MockAIService();
            case "DEEPSEEK":
                logger.info("Using DeepSeekAIService");
                return new DeepSeekAIService();
            default:
                logger.warn("Unknown provider: {}, falling back to Mock service", properties.getProvider());
                return new MockAIService();
        }
    }
    
    /**
     * 上下文管理器Bean
     */
    @Bean
    public ContextManager contextManager() {
        logger.info("Initializing Context Manager");
        return new ContextManagerImpl();
    }
    
    /**
     * 质量评估器Bean
     */
    @Bean
    public QualityAssessor qualityAssessor() {
        logger.info("Initializing Quality Assessor");
        return new QualityAssessorImpl();
    }
    
    /**
     * 服务监控器Bean
     */
    @Bean
    public ServiceMonitor serviceMonitor() {
        logger.info("Initializing Service Monitor");
        return new ServiceMonitorImpl();
    }
    
    /**
     * 定时清理过期统计信息
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 1小时 = 3600000毫秒
    public void cleanupExpiredStats() {
        try {
            ServiceMonitor monitor = serviceMonitor();
            if (monitor instanceof ServiceMonitorImpl) {
                ((ServiceMonitorImpl) monitor).cleanupExpiredStats();
                logger.debug("Cleaned up expired service stats");
            }
        } catch (Exception e) {
            logger.error("Error during scheduled cleanup of expired stats", e);
        }
    }
    
    /**
     * 定时健康检查
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void healthCheck() {
        try {
            AIService aiService = aiService(new AIServiceProperties()); // 临时创建，实际应该注入
            boolean healthy = aiService.isHealthy();
            
            if (!healthy) {
                logger.warn("AI Service health check failed");
            } else {
                logger.debug("AI Service health check passed");
            }
            
        } catch (Exception e) {
            logger.error("Error during scheduled health check", e);
        }
    }
    
    /**
     * 定时记录服务统计摘要
     * 每30分钟执行一次
     */
    @Scheduled(fixedRate = 1800000) // 30分钟 = 1800000毫秒
    public void logServiceStats() {
        try {
            ServiceMonitor monitor = serviceMonitor();
            if (monitor instanceof ServiceMonitorImpl) {
                String summary = ((ServiceMonitorImpl) monitor).getHealthSummary();
                logger.info("Service Statistics Summary:\n{}", summary);
            }
        } catch (Exception e) {
            logger.error("Error during scheduled service stats logging", e);
        }
    }
}