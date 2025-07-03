package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.ui.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于测试应用是否正常启动和运行
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * 健康检查接口
     * 无需认证，用于检查服务状态
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "XLHealth Backend");
        healthInfo.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success("服务运行正常", healthInfo));
    }

    /**
     * 版本信息接口
     */
    @GetMapping("/version")
    public ResponseEntity<ApiResponse<Map<String, String>>> version() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("application", "XLHealth Backend");
        versionInfo.put("version", "1.0.0");
        versionInfo.put("buildTime", "2024-01-01");
        versionInfo.put("springBootVersion", "3.5.0");
        versionInfo.put("javaVersion", System.getProperty("java.version"));

        return ResponseEntity.ok(ApiResponse.success("版本信息", versionInfo));
    }
}