package cn.xlhealth.backend.ui.advice;

import cn.xlhealth.backend.ui.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常，返回标准化的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * 处理参数验证异常
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidationException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                logger.warn("参数验证失败: {} - {}", request.getRequestURI(), errors);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error("VALIDATION_ERROR", "参数验证失败", errors));
        }

        /**
         * 处理绑定异常
         */
        @ExceptionHandler(BindException.class)
        public ResponseEntity<ApiResponse<Object>> handleBindException(
                        BindException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                logger.warn("数据绑定失败: {} - {}", request.getRequestURI(), errors);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error("BIND_ERROR", "数据绑定失败", errors));
        }

        /**
         * 处理参数类型不匹配异常
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
                        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

                String requiredTypeName = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知类型";
                String message = String.format("参数 '%s' 类型错误，期望类型: %s",
                                ex.getName(), requiredTypeName);

                logger.warn("参数类型不匹配: {} - {}", request.getRequestURI(), message);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error("TYPE_MISMATCH", message));
        }

        /**
         * 处理认证异常
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
                        BadCredentialsException ex, HttpServletRequest request) {

                logger.warn("认证失败: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ApiResponse.error("BAD_CREDENTIALS", "用户名或密码错误"));
        }

        /**
         * 处理访问拒绝异常
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {

                logger.warn("访问被拒绝: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                ApiResponse.error("ACCESS_DENIED", "访问被拒绝，权限不足"));
        }

        /**
         * 处理业务异常
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(
                        BusinessException ex, HttpServletRequest request) {

                logger.warn("业务异常: {} - {} - {}", request.getRequestURI(), ex.getCode(), ex.getMessage());

                return ResponseEntity.status(ex.getHttpStatus()).body(
                                ApiResponse.error(ex.getCode(), ex.getMessage()));
        }

        /**
         * 处理运行时异常
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
                        RuntimeException ex, HttpServletRequest request) {

                logger.error("运行时异常: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ApiResponse.error("RUNTIME_ERROR", "系统运行时错误"));
        }

        /**
         * 处理其他未知异常
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGenericException(
                        Exception ex, HttpServletRequest request) {

                logger.error("未知异常: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ApiResponse.error("INTERNAL_ERROR", "系统内部错误"));
        }
}