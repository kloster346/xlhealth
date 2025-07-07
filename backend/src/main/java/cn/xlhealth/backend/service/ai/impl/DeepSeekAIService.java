package cn.xlhealth.backend.service.ai.impl;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.AIService;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.exception.AIServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek AI服务实现
 * 集成DeepSeek API提供AI对话服务
 */
@Service
public class DeepSeekAIService implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekAIService.class);

    @Autowired
    private AIServiceProperties aiServiceProperties;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DeepSeekAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AIResponse generateReply(AIRequest request) {
        logger.info("Generating reply using DeepSeek API for user: {}", request.getUserId());

        long startTime = System.currentTimeMillis();

        try {
            // 获取DeepSeek配置
            AIServiceProperties.DeepSeek config = aiServiceProperties.getDeepseek();
            String apiKey = config.getApiKey();

            // 验证API密钥
            if (!StringUtils.hasText(apiKey)) {
                throw new AIServiceException("DeepSeek API key is not configured", "MISSING_API_KEY", "DeepSeek");
            }

            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(request, config);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用DeepSeek API
            String url = config.getApiBase() + "/v1/chat/completions";
            logger.debug("Calling DeepSeek API: {}", url);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                AIResponse aiResponse = parseResponse(response.getBody(), request);
                // 计算实际响应时间
                long responseTime = System.currentTimeMillis() - startTime;
                aiResponse.setResponseTime(responseTime);
                logger.info("DeepSeek API response time: {}ms", responseTime);
                return aiResponse;
            } else {
                throw new AIServiceException(
                        "DeepSeek API returned error: " + response.getStatusCode(),
                        "API_ERROR",
                        "DeepSeek");
            }

        } catch (Exception e) {
            logger.error("Error calling DeepSeek API", e);
            if (e instanceof AIServiceException) {
                throw e;
            }
            throw new AIServiceException("API_CALL_FAILED", "Failed to generate reply: " + e.getMessage(), "DeepSeek");
        }
    }

    /**
     * 构建DeepSeek API请求体
     */
    private Map<String, Object> buildRequestBody(AIRequest request, AIServiceProperties.DeepSeek config) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        requestBody.put("stream", false);

        // 构建消息列表，包含上下文
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加系统提示词
        messages.add(Map.of(
                "role", "system",
                "content", buildSystemPrompt()));

        // 添加历史上下文消息
        if (request.getContext() != null && !request.getContext().isEmpty()) {
            logger.debug("Adding {} context messages to request", request.getContext().size());
            for (cn.xlhealth.backend.service.ai.dto.ContextMessage contextMsg : request.getContext()) {
                String role = "USER".equals(contextMsg.getMessageType()) ? "user" : "assistant";
                messages.add(Map.of(
                        "role", role,
                        "content", contextMsg.getContent()));
            }
        } else {
            logger.debug("No context messages found for conversation: {}", request.getConversationId());
        }

        // 添加当前用户消息
        messages.add(Map.of(
                "role", "user",
                "content", request.getUserMessage()));

        requestBody.put("messages", messages);

        logger.debug("Built request with {} total messages (including system prompt)", messages.size());
        return requestBody;
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt() {
        return "你是一个专业的心理健康咨询助手。请以温暖、专业、有同理心的方式回应用户的问题。" +
                "你的回复应该：\n" +
                "1. 表现出对用户感受的理解和共情\n" +
                "2. 提供专业但易懂的心理健康建议\n" +
                "3. 鼓励用户寻求专业帮助（如果需要）\n" +
                "4. 保持积极正面的态度\n" +
                "5. 避免诊断或提供医疗建议\n" +
                "请用中文回复，语言要温和友善。";
    }

    /**
     * 解析DeepSeek API响应
     */
    private AIResponse parseResponse(String responseBody, AIRequest request) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 提取回复内容
            JsonNode choices = jsonNode.get("choices");
            if (choices == null || !choices.isArray() || choices.size() == 0) {
                throw new AIServiceException("Invalid response format from DeepSeek API", "INVALID_RESPONSE",
                        "DeepSeek");
            }

            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.get("message");
            String content = message.get("content").asText();

            // 提取使用统计
            JsonNode usage = jsonNode.get("usage");
            int promptTokens = usage != null ? usage.get("prompt_tokens").asInt(0) : 0;
            int completionTokens = usage != null ? usage.get("completion_tokens").asInt(0) : 0;
            int totalTokens = usage != null ? usage.get("total_tokens").asInt(0) : 0;

            // 构建响应
            AIResponse aiResponse = new AIResponse();
            aiResponse.setContent(content);
            aiResponse.setProvider("DEEPSEEK");
            aiResponse.setSuccess(true);
            // responseTime will be set in generateReply method

            // 设置使用统计
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("prompt_tokens", promptTokens);
            metadata.put("completion_tokens", completionTokens);
            metadata.put("total_tokens", totalTokens);
            metadata.put("model", aiServiceProperties.getDeepseek().getModel());
            aiResponse.setMetadata(metadata);

            logger.info("Successfully generated reply using DeepSeek API. Tokens used: {}", totalTokens);

            return aiResponse;

        } catch (Exception e) {
            logger.error("Error parsing DeepSeek API response", e);
            throw new AIServiceException("PARSE_ERROR", "Failed to parse API response: " + e.getMessage(), "DeepSeek");
        }
    }

    @Override
    public boolean isHealthy() {
        try {
            // 获取DeepSeek配置
            AIServiceProperties.DeepSeek config = aiServiceProperties.getDeepseek();

            // 验证配置
            if (!StringUtils.hasText(config.getApiKey())) {
                logger.warn("DeepSeek API key is not configured");
                return false;
            }

            if (!StringUtils.hasText(config.getApiBase())) {
                logger.warn("DeepSeek API base URL is not configured");
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error("DeepSeek AI Service health check failed", e);
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "DeepSeek";
    }

    @Override
    public Map<String, Object> getConfigInfo() {
        AIServiceProperties.DeepSeek config = aiServiceProperties.getDeepseek();
        Map<String, Object> configInfo = new HashMap<>();
        configInfo.put("provider", "DeepSeek");
        configInfo.put("model", config.getModel());
        configInfo.put("api_base", config.getApiBase());
        configInfo.put("max_tokens", config.getMaxTokens());
        configInfo.put("temperature", config.getTemperature());
        configInfo.put("timeout_ms", config.getTimeout());
        configInfo.put("api_key_configured", StringUtils.hasText(config.getApiKey()));
        return configInfo;
    }
}