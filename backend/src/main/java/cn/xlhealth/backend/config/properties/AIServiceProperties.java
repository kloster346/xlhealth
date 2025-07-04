package cn.xlhealth.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI服务配置属性
 */
@Component
@ConfigurationProperties(prefix = "ai.service")
public class AIServiceProperties {
    
    /**
     * 是否启用模拟模式
     */
    private boolean mockEnabled = true;
    
    /**
     * AI服务提供商类型
     */
    private String provider = "mock";
    
    /**
     * 上下文配置
     */
    private Context context = new Context();
    
    /**
     * 质量评估配置
     */
    private Quality quality = new Quality();
    
    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();
    
    /**
     * 降级配置
     */
    private Fallback fallback = new Fallback();
    
    public static class Context {
        /**
         * 上下文窗口大小（保留最近N条消息）
         */
        private int windowSize = 10;
        
        /**
         * 上下文缓存过期时间（分钟）
         */
        private int cacheExpireMinutes = 30;
        
        /**
         * 是否启用上下文摘要
         */
        private boolean summaryEnabled = true;
        
        // Getters and Setters
        public int getWindowSize() {
            return windowSize;
        }
        
        public void setWindowSize(int windowSize) {
            this.windowSize = windowSize;
        }
        
        public int getCacheExpireMinutes() {
            return cacheExpireMinutes;
        }
        
        public void setCacheExpireMinutes(int cacheExpireMinutes) {
            this.cacheExpireMinutes = cacheExpireMinutes;
        }
        
        public boolean isSummaryEnabled() {
            return summaryEnabled;
        }
        
        public void setSummaryEnabled(boolean summaryEnabled) {
            this.summaryEnabled = summaryEnabled;
        }
    }
    
    public static class Quality {
        /**
         * 是否启用质量评估
         */
        private boolean enabled = true;
        
        /**
         * 质量评估最低分数阈值
         */
        private int minScore = 60;
        
        /**
         * 是否启用安全检查
         */
        private boolean safetyCheckEnabled = true;
        
        /**
         * 最大重试次数
         */
        private int maxRetries = 3;
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getMinScore() {
            return minScore;
        }
        
        public void setMinScore(int minScore) {
            this.minScore = minScore;
        }
        
        public boolean isSafetyCheckEnabled() {
            return safetyCheckEnabled;
        }
        
        public void setSafetyCheckEnabled(boolean safetyCheckEnabled) {
            this.safetyCheckEnabled = safetyCheckEnabled;
        }
        
        public int getMaxRetries() {
            return maxRetries;
        }
        
        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
    }
    
    public static class Monitor {
        /**
         * 健康检查间隔（秒）
         */
        private int healthCheckIntervalSeconds = 60;
        
        /**
         * 响应时间阈值（毫秒）
         */
        private long responseTimeThresholdMs = 5000;
        
        /**
         * 错误率阈值（百分比）
         */
        private double errorRateThreshold = 10.0;
        
        // Getters and Setters
        public int getHealthCheckIntervalSeconds() {
            return healthCheckIntervalSeconds;
        }
        
        public void setHealthCheckIntervalSeconds(int healthCheckIntervalSeconds) {
            this.healthCheckIntervalSeconds = healthCheckIntervalSeconds;
        }
        
        public long getResponseTimeThresholdMs() {
            return responseTimeThresholdMs;
        }
        
        public void setResponseTimeThresholdMs(long responseTimeThresholdMs) {
            this.responseTimeThresholdMs = responseTimeThresholdMs;
        }
        
        public double getErrorRateThreshold() {
            return errorRateThreshold;
        }
        
        public void setErrorRateThreshold(double errorRateThreshold) {
            this.errorRateThreshold = errorRateThreshold;
        }
    }
    
    public static class Fallback {
        /**
         * 是否启用降级
         */
        private boolean enabled = true;
        
        /**
         * 熔断器失败阈值
         */
        private int failureThreshold = 5;
        
        /**
         * 熔断器恢复时间（秒）
         */
        private int recoveryTimeSeconds = 60;
        
        /**
         * 默认降级消息
         */
        private String defaultMessage = "抱歉，我现在无法为您提供最佳回复。请稍后重试，或联系专业心理咨询师获得帮助。";
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getFailureThreshold() {
            return failureThreshold;
        }
        
        public void setFailureThreshold(int failureThreshold) {
            this.failureThreshold = failureThreshold;
        }
        
        public int getRecoveryTimeSeconds() {
            return recoveryTimeSeconds;
        }
        
        public void setRecoveryTimeSeconds(int recoveryTimeSeconds) {
            this.recoveryTimeSeconds = recoveryTimeSeconds;
        }
        
        public String getDefaultMessage() {
            return defaultMessage;
        }
        
        public void setDefaultMessage(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
    }
    
    // Main class getters and setters
    public boolean isMockEnabled() {
        return mockEnabled;
    }
    
    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public Context getContext() {
        return context;
    }
    
    public void setContext(Context context) {
        this.context = context;
    }
    
    public Quality getQuality() {
        return quality;
    }
    
    public void setQuality(Quality quality) {
        this.quality = quality;
    }
    
    public Monitor getMonitor() {
        return monitor;
    }
    
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public Fallback getFallback() {
        return fallback;
    }
    
    public void setFallback(Fallback fallback) {
        this.fallback = fallback;
    }
}