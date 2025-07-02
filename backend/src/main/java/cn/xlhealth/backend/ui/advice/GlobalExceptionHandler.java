package cn.xlhealth.backend.ui.advice;

import cn.xlhealth.backend.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常，返回标准化的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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

                log.warn("参数验证失败: {} - {}", request.getRequestURI(), errors);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error(400, "参数验证失败", request.getRequestURI()));
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

                log.warn("数据绑定失败: {} - {}", request.getRequestURI(), errors);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error(400, "数据绑定失败", request.getRequestURI()));
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

                log.warn("参数类型不匹配: {} - {}", request.getRequestURI(), message);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error(400, message, request.getRequestURI()));
        }

        /**
         * 处理认证异常
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
                        BadCredentialsException ex, HttpServletRequest request) {

                log.warn("认证失败: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ApiResponse.unauthorized("用户名或密码错误"));
        }

        /**
         * 处理访问拒绝异常
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {

                log.warn("访问被拒绝: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                ApiResponse.forbidden("访问被拒绝，权限不足"));
        }

        /**
         * 处理约束违反异常
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
                        ConstraintViolationException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                        errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }

                log.warn("约束违反: {} - {}", request.getRequestURI(), errors);

                return ResponseEntity.badRequest().body(
                                ApiResponse.error(400, "数据验证失败", request.getRequestURI()));
        }

        /**
         * 处理数据完整性违反异常（如重复注册）
         */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
                        DataIntegrityViolationException ex, HttpServletRequest request) {

                String message = "数据操作失败";
                if (ex.getMessage() != null) {
                        if (ex.getMessage().contains("Duplicate entry")) {
                                message = "用户名或邮箱已存在，请使用其他信息注册";
                        } else if (ex.getMessage().contains("foreign key constraint")) {
                                message = "数据关联约束错误";
                        }
                }

                log.warn("数据完整性违反: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.badRequest().body(
                                ApiResponse.error(400, message, request.getRequestURI()));
        }

        /**
         * 处理JWT过期异常
         */
        @ExceptionHandler(ExpiredJwtException.class)
        public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(
                        ExpiredJwtException ex, HttpServletRequest request) {

                log.warn("JWT token已过期: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ApiResponse.jwtError("JWT token已过期，请重新登录"));
        }

        /**
         * 处理JWT格式错误异常
         */
        @ExceptionHandler(MalformedJwtException.class)
        public ResponseEntity<ApiResponse<Object>> handleMalformedJwtException(
                        MalformedJwtException ex, HttpServletRequest request) {

                log.warn("JWT token格式错误: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ApiResponse.jwtError("JWT token格式错误"));
        }

        /**
         * 处理JWT签名异常
         */
        @ExceptionHandler(SignatureException.class)
        public ResponseEntity<ApiResponse<Object>> handleSignatureException(
                        SignatureException ex, HttpServletRequest request) {

                log.warn("JWT签名验证失败: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ApiResponse.jwtError("JWT签名验证失败"));
        }

        /**
         * 处理不支持的JWT异常
         */
        @ExceptionHandler(UnsupportedJwtException.class)
        public ResponseEntity<ApiResponse<Object>> handleUnsupportedJwtException(
                        UnsupportedJwtException ex, HttpServletRequest request) {

                log.warn("不支持的JWT token: {} - {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ApiResponse.jwtError("不支持的JWT token格式"));
        }

        /**
         * 处理运行时异常
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
                        RuntimeException ex, HttpServletRequest request) {

                log.error("运行时异常: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ApiResponse.internalError("系统运行时错误"));
        }

        /**
         * 处理其他未知异常
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGenericException(
                        Exception ex, HttpServletRequest request) {

                log.error("未知异常: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ApiResponse.internalError("系统内部错误"));
        }
}