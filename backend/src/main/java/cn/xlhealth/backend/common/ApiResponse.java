package cn.xlhealth.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 统一API响应格式
 * 用于标准化所有API接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 错误详情（仅在错误时返回）
     */
    private String error;
    
    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 成功响应（自定义消息和数据）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 错误响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 错误响应（带路径）
     */
    public static <T> ApiResponse<T> error(Integer code, String message, String path) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 错误响应（带错误详情）
     */
    public static <T> ApiResponse<T> error(Integer code, String message, String path, String error) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .path(path)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 参数验证错误
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return error(400, message);
    }
    
    /**
     * 认证失败
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(401, message != null ? message : "认证失败");
    }
    
    /**
     * 权限不足
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(403, message != null ? message : "权限不足");
    }
    
    /**
     * 资源未找到
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message != null ? message : "资源未找到");
    }
    
    /**
     * 服务器内部错误
     */
    public static <T> ApiResponse<T> internalError(String message) {
        return error(500, message != null ? message : "服务器内部错误");
    }
    
    /**
     * 业务逻辑错误
     */
    public static <T> ApiResponse<T> businessError(String message) {
        return error(400, message);
    }
    
    /**
     * JWT相关错误
     */
    public static <T> ApiResponse<T> jwtError(String message) {
        return error(401, message != null ? message : "JWT token无效或已过期");
    }
}