package cn.xlhealth.backend.service.ai.monitor;

/**
 * AI服务统计信息
 */
public class ServiceStats {
    
    /**
     * 总调用次数
     */
    private long totalCalls;
    
    /**
     * 成功调用次数
     */
    private long successfulCalls;
    
    /**
     * 失败调用次数
     */
    private long failedCalls;
    
    /**
     * 总响应时间（毫秒）
     */
    private long totalResponseTime;
    
    /**
     * 最大响应时间（毫秒）
     */
    private long maxResponseTime;
    
    /**
     * 最小响应时间（毫秒）
     */
    private long minResponseTime = Long.MAX_VALUE;
    
    /**
     * 平均质量评分
     */
    private double averageQualityScore;
    
    /**
     * 最后更新时间
     */
    private long lastUpdated;
    
    /**
     * 服务提供商
     */
    private String provider;
    
    public ServiceStats(String provider) {
        this.provider = provider;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    /**
     * 记录成功调用
     * @param responseTime 响应时间（毫秒）
     * @param qualityScore 质量评分
     */
    public void recordSuccess(long responseTime, int qualityScore) {
        this.totalCalls++;
        this.successfulCalls++;
        this.totalResponseTime += responseTime;
        this.maxResponseTime = Math.max(this.maxResponseTime, responseTime);
        this.minResponseTime = Math.min(this.minResponseTime, responseTime);
        this.averageQualityScore = ((this.averageQualityScore * (this.successfulCalls - 1)) + qualityScore) / this.successfulCalls;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    /**
     * 记录失败调用
     * @param responseTime 响应时间（毫秒）
     */
    public void recordFailure(long responseTime) {
        this.totalCalls++;
        this.failedCalls++;
        this.totalResponseTime += responseTime;
        this.maxResponseTime = Math.max(this.maxResponseTime, responseTime);
        this.minResponseTime = Math.min(this.minResponseTime, responseTime);
        this.lastUpdated = System.currentTimeMillis();
    }
    
    /**
     * 获取成功率
     * @return 成功率（0-100）
     */
    public double getSuccessRate() {
        if (totalCalls == 0) {
            return 100.0;
        }
        return (double) successfulCalls / totalCalls * 100.0;
    }
    
    /**
     * 获取错误率
     * @return 错误率（0-100）
     */
    public double getErrorRate() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) failedCalls / totalCalls * 100.0;
    }
    
    /**
     * 获取平均响应时间
     * @return 平均响应时间（毫秒）
     */
    public double getAverageResponseTime() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) totalResponseTime / totalCalls;
    }
    
    /**
     * 重置统计信息
     */
    public void reset() {
        this.totalCalls = 0;
        this.successfulCalls = 0;
        this.failedCalls = 0;
        this.totalResponseTime = 0;
        this.maxResponseTime = 0;
        this.minResponseTime = Long.MAX_VALUE;
        this.averageQualityScore = 0.0;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getTotalCalls() {
        return totalCalls;
    }
    
    public void setTotalCalls(long totalCalls) {
        this.totalCalls = totalCalls;
    }
    
    public long getSuccessfulCalls() {
        return successfulCalls;
    }
    
    public void setSuccessfulCalls(long successfulCalls) {
        this.successfulCalls = successfulCalls;
    }
    
    public long getFailedCalls() {
        return failedCalls;
    }
    
    public void setFailedCalls(long failedCalls) {
        this.failedCalls = failedCalls;
    }
    
    public long getTotalResponseTime() {
        return totalResponseTime;
    }
    
    public void setTotalResponseTime(long totalResponseTime) {
        this.totalResponseTime = totalResponseTime;
    }
    
    public long getMaxResponseTime() {
        return maxResponseTime;
    }
    
    public void setMaxResponseTime(long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }
    
    public long getMinResponseTime() {
        return minResponseTime;
    }
    
    public void setMinResponseTime(long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
    
    public double getAverageQualityScore() {
        return averageQualityScore;
    }
    
    public void setAverageQualityScore(double averageQualityScore) {
        this.averageQualityScore = averageQualityScore;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
}