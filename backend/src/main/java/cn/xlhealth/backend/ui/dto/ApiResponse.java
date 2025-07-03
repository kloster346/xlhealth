package cn.xlhealth.backend.ui.dto;

import cn.xlhealth.backend.common.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 统一API响应类
 * 标准化所有接口的返回格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 响应是否成功
     */
    private boolean success;

    /**
     * 响应码
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
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 成功响应的静态工厂方法
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, ErrorCode.SUCCESS, "操作成功", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, ErrorCode.SUCCESS, "操作成功", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, ErrorCode.SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> success(Integer code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }

    // 失败响应的静态工厂方法
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, ErrorCode.ERROR, message, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    // 常用的错误响应
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(false, ErrorCode.BAD_REQUEST, message, null);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, ErrorCode.UNAUTHORIZED, message, null);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, ErrorCode.FORBIDDEN, message, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, ErrorCode.NOT_FOUND, message, null);
    }

    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(false, ErrorCode.INTERNAL_ERROR, message, null);
    }

    /**
     * JWT相关错误
     */
    public static <T> ApiResponse<T> jwtError(String message) {
        return new ApiResponse<>(false, ErrorCode.UNAUTHORIZED, message != null ? message : "JWT token无效或已过期", null);
    }

    // Getter和Setter方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}