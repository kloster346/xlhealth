package cn.xlhealth.backend.service.ai.health;

import cn.xlhealth.backend.service.ai.AIServiceManager;
import cn.xlhealth.backend.service.ai.monitor.ServiceMonitor;
import cn.xlhealth.backend.service.ai.monitor.impl.ServiceMonitorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.actuator.health.Health;
// import org.springframework.boot.actuator.health.HealthIndicator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * AI服务健康检查指示器
 */
// @Component("aiServiceHealthIndicator")
public class AIServiceHealthIndicator /* implements HealthIndicator */ {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceHealthIndicator.class);

    @Autowired
    private AIServiceManager aiServiceManager;

    @Autowired
    private ServiceMonitor serviceMonitor;

    // @Override
    public Object health() {
        try {
            // 检查AI服务管理器健康状态
            boolean managerHealthy = aiServiceManager.isHealthy();

            // 检查服务监控器健康状态
            boolean monitorHealthy = serviceMonitor.isHealthy(null);

            // 获取详细统计信息
            Map<String, Object> details = buildHealthDetails();

            // 综合判断健康状态
            boolean overallHealthy = managerHealthy && monitorHealthy;

            Map<String, Object> result = new HashMap<>();
            result.put("status", overallHealthy ? "UP" : "DOWN");
            result.put("details", details);
            return result;

        } catch (Exception e) {
            logger.error("Error during AI service health check", e);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "DOWN");
            result.put("error", "Health check failed: " + e.getMessage());
            result.put("timestamp", LocalDateTime.now());
            return result;
        }
    }

    /**
     * 构建健康检查详细信息
     */
    private Map<String, Object> buildHealthDetails() {
        Map<String, Object> details = new HashMap<>();

        try {
            // 基本信息
            details.put("timestamp", LocalDateTime.now());
            details.put("service", "AI Service");

            // 管理器健康状态
            boolean managerHealthy = aiServiceManager.isHealthy();
            details.put("manager_healthy", managerHealthy);

            // 全局统计信息
            double globalSuccessRate = serviceMonitor.getSuccessRate(null, 60); // 使用60分钟时间窗口
            double globalAvgResponseTime = serviceMonitor.getAverageResponseTime(null, 60); // 使用60分钟时间窗口
            double globalErrorRate = serviceMonitor.getErrorRate(null, 60); // 使用60分钟时间窗口

            details.put("global_success_rate", String.format("%.2f%%", globalSuccessRate * 100));
            details.put("global_avg_response_time", String.format("%.0fms", globalAvgResponseTime));
            details.put("global_error_rate", String.format("%.2f%%", globalErrorRate * 100));

            // 各提供商统计信息
            if (serviceMonitor instanceof ServiceMonitorImpl) {
                ServiceMonitorImpl monitorImpl = (ServiceMonitorImpl) serviceMonitor;
                Map<String, Object> providerStats = new HashMap<>();

                monitorImpl.getAllProviderStats().forEach((provider, stats) -> {
                    Map<String, Object> providerDetail = new HashMap<>();
                    providerDetail.put("success_rate", String.format("%.2f%%", stats.getSuccessRate() * 100));
                    providerDetail.put("avg_response_time", String.format("%.0fms", stats.getAverageResponseTime()));
                    providerDetail.put("error_rate", String.format("%.2f%%", stats.getErrorRate() * 100));
                    providerDetail.put("total_calls", stats.getTotalCalls());
                    providerDetail.put("healthy", serviceMonitor.isHealthy(provider));
                    providerStats.put(provider, providerDetail);
                });

                details.put("providers", providerStats);
            }

            // 配置信息
            Map<String, Object> configInfo = aiServiceManager.getConfigInfo();
            details.put("config", configInfo);

            // 健康状态判断标准
            details.put("health_criteria", Map.of(
                    "min_success_rate", "80%",
                    "max_response_time", "5000ms",
                    "max_error_rate", "20%"));

        } catch (Exception e) {
            logger.warn("Error building health details", e);
            details.put("details_error", "Failed to collect detailed information: " + e.getMessage());
        }

        return details;
    }
}