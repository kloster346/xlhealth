package cn.xlhealth.backend.service.ai.exception;

/**
 * AI服务异常类
 */
public class AIServiceException extends RuntimeException {
    
    /**
     * 错误代码
     */
    private String errorCode;
    
    /**
     * AI服务提供商
     */
    private String provider;
    
    public AIServiceException(String message) {
        super(message);
    }
    
    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AIServiceException(String errorCode, String message, String provider) {
        super(message);
        this.errorCode = errorCode;
        this.provider = provider;
    }
    
    public AIServiceException(String errorCode, String message, String provider, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.provider = provider;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
}