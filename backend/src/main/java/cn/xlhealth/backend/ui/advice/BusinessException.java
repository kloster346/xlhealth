package cn.xlhealth.backend.ui.advice;

import cn.xlhealth.backend.common.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {

    private Integer code;
    private HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.BAD_REQUEST;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    // 常用的静态工厂方法
    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrorCode.BAD_REQUEST, message, HttpStatus.BAD_REQUEST);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.NOT_FOUND, message, HttpStatus.NOT_FOUND);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, message, HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.FORBIDDEN, message, HttpStatus.FORBIDDEN);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ErrorCode.CONFLICT, message, HttpStatus.CONFLICT);
    }

    public static BusinessException internalError(String message) {
        return new BusinessException(ErrorCode.INTERNAL_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}