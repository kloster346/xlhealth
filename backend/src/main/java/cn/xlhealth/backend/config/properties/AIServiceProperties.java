package cn.xlhealth.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI服务配置属性
 */
@ConfigurationProperties(prefix = "ai.service")
public class AIServiceProperties {
    
    /**
     * 是否启用模拟模式
     */
    private boolean mockMode = false;
    
    /**
     * AI服务提供商类型
     */
    private String provider = "DEEPSEEK";
    
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
    
    /**
     * DeepSeek服务配置
     */
    private DeepSeek deepseek = new DeepSeek();
    
    public static class Context {
        /**
         * 上下文窗口大小（保留最近N条消息）
         */
        private int windowSize = 10;
        
        /**
         * 最大上下文消息数量（与windowSize同义，用于配置文件映射）
         */
        @com.fasterxml.jackson.annotation.JsonProperty("max-messages")
        private int maxMessages = 20;
        
        /**
         * 是否启用上下文管理
         */
        private boolean enabled = true;
        
        /**
         * 上下文过期时间（小时）
         */
        @com.fasterxml.jackson.annotation.JsonProperty("expiry-hours")
        private int expiryHours = 24;
        
        /**
         * 上下文缓存过期时间（分钟）
         */
        private int cacheExpireMinutes = 30;
        
        /**
         * 是否启用上下文摘要
         */
        @com.fasterxml.jackson.annotation.JsonProperty("summary-enabled")
        private boolean summaryEnabled = true;
        
        /**
         * 摘要触发的消息数量阈值
         */
        @com.fasterxml.jackson.annotation.JsonProperty("summary-threshold")
        private int summaryThreshold = 10;
        
        // Getters and Setters
        public int getWindowSize() {
            return windowSize;
        }
        
        public void setWindowSize(int windowSize) {
            this.windowSize = windowSize;
        }
        
        public int getMaxMessages() {
            return maxMessages;
        }
        
        public void setMaxMessages(int maxMessages) {
            this.maxMessages = maxMessages;
            // 同时更新windowSize以保持一致性
            this.windowSize = maxMessages;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getExpiryHours() {
            return expiryHours;
        }
        
        public void setExpiryHours(int expiryHours) {
            this.expiryHours = expiryHours;
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
        
        public int getSummaryThreshold() {
            return summaryThreshold;
        }
        
        public void setSummaryThreshold(int summaryThreshold) {
            this.summaryThreshold = summaryThreshold;
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
    
    public static class DeepSeek {
        /**
         * API密钥
         */
        @com.fasterxml.jackson.annotation.JsonProperty("api-key")
        private String apiKey;
        
        /**
         * API基础URL
         */
        @com.fasterxml.jackson.annotation.JsonProperty("api-base")
        private String apiBase = "https://api.deepseek.com";
        
        /**
         * 使用的模型
         */
        private String model = "deepseek-chat";
        
        /**
         * 最大token数
         */
        @com.fasterxml.jackson.annotation.JsonProperty("max-tokens")
        private int maxTokens = 2048;
        
        /**
         * 温度参数 (0.0-2.0)
         */
        private double temperature = 0.7;
        
        /**
         * 请求超时时间(毫秒)
         */
        private int timeout = 30000;
        
        // Getters and Setters
        public String getApiKey() {
            return apiKey;
        }
        
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public String getApiBase() {
            return apiBase;
        }
        
        public void setApiBase(String apiBase) {
            this.apiBase = apiBase;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public int getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
        
        public int getTimeout() {
            return timeout;
        }
        
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }
    
    // Main class getters and setters
    public boolean isMockMode() {
        return mockMode;
    }
    
    public void setMockMode(boolean mockMode) {
        this.mockMode = mockMode;
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
    
    public DeepSeek getDeepseek() {
        return deepseek;
    }
    
    public void setDeepseek(DeepSeek deepseek) {
        this.deepseek = deepseek;
    }
}