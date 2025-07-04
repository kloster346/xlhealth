package cn.xlhealth.backend.service.ai.impl;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.AIService;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.exception.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模拟AI服务实现
 * 提供心理咨询相关的模拟AI回复
 */
@Service
public class MockAIService implements AIService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockAIService.class);
    
    private static final String PROVIDER_NAME = "MockAI";
    
    @Autowired
    private AIServiceProperties aiServiceProperties;
    
    // 回复模板映射
    private static final Map<String, List<String>> REPLY_TEMPLATES = new HashMap<>();
    
    // 关键词映射到回复类型
    private static final Map<String, String> KEYWORD_TO_TYPE = new HashMap<>();
    
    static {
        initializeTemplates();
        initializeKeywords();
    }
    
    @Override
    public AIResponse generateReply(AIRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟处理时间
            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2000));
            
            // 分析用户消息，确定回复类型
            String replyType = analyzeMessageType(request.getUserMessage());
            
            // 生成回复内容
            String content = generateReplyContent(request, replyType);
            
            // 创建成功响应
            AIResponse response = AIResponse.success(content, replyType, PROVIDER_NAME);
            response.setResponseTime(System.currentTimeMillis() - startTime);
            
            logger.info("Generated mock AI reply for user {}, type: {}, response time: {}ms", 
                    request.getUserId(), replyType, response.getResponseTime());
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Mock AI service interrupted", e);
            return AIResponse.failure("服务被中断", PROVIDER_NAME);
        } catch (Exception e) {
            logger.error("Error generating mock AI reply", e);
            throw new AIServiceException("MOCK_ERROR", "生成模拟回复时发生错误: " + e.getMessage(), PROVIDER_NAME, e);
        }
    }
    
    @Override
    public boolean isHealthy() {
        return aiServiceProperties.isMockEnabled();
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public java.util.Map<String, Object> getConfigInfo() {
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("provider", PROVIDER_NAME);
        config.put("mockMode", aiServiceProperties.isMockEnabled());
        config.put("templateTypes", REPLY_TEMPLATES.size());
        config.put("contextEnabled", aiServiceProperties.getContext().isSummaryEnabled());
        config.put("qualityEnabled", aiServiceProperties.getQuality().isEnabled());
        config.put("monitoringEnabled", true); // Monitor doesn't have isEnabled method
        config.put("version", "1.0.0");
        return config;
    }
    
    /**
     * 分析消息类型
     */
    private String analyzeMessageType(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "INFORMATION_GATHERING";
        }
        
        String lowerMessage = message.toLowerCase();
        
        // 检查关键词匹配
        for (Map.Entry<String, String> entry : KEYWORD_TO_TYPE.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 默认情感支持
        return "EMOTIONAL_SUPPORT";
    }
    
    /**
     * 生成回复内容
     */
    private String generateReplyContent(AIRequest request, String replyType) {
        List<String> templates = REPLY_TEMPLATES.get(replyType);
        if (templates == null || templates.isEmpty()) {
            templates = REPLY_TEMPLATES.get("EMOTIONAL_SUPPORT");
        }
        
        // 随机选择模板
        String template = templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
        
        // 简单的模板变量替换
        return template.replace("{user}", "朋友")
                      .replace("{time}", getTimeGreeting());
    }
    
    /**
     * 获取时间问候语
     */
    private String getTimeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 6) {
            return "深夜";
        } else if (hour < 12) {
            return "上午";
        } else if (hour < 18) {
            return "下午";
        } else {
            return "晚上";
        }
    }
    
    /**
     * 初始化回复模板
     */
    private static void initializeTemplates() {
        // 情感支持模式
        REPLY_TEMPLATES.put("EMOTIONAL_SUPPORT", Arrays.asList(
            "我能理解你现在的感受，这确实不容易。请记住，你并不孤单，我会陪伴你度过这段困难时期。",
            "你的感受是完全可以理解的，每个人都会遇到困难的时候。重要的是要相信自己有能力克服这些挑战。",
            "感谢你愿意分享你的感受。承认自己的情绪需要很大的勇气，这本身就是一种进步。",
            "你现在经历的这些情绪都是正常的，给自己一些时间和空间来处理这些感受吧。",
            "我听到了你的痛苦，也看到了你的坚强。请相信，这些困难的时刻会过去的。"
        ));
        
        // 认知引导模式
        REPLY_TEMPLATES.put("COGNITIVE_GUIDANCE", Arrays.asList(
            "让我们一起来看看这个问题的不同角度。有时候换个视角，问题可能会有新的解决方案。",
            "你提到的这种想法很常见，但我们可以尝试用更客观的方式来分析一下这个情况。",
            "当我们陷入负面思维时，往往会忽略一些积极的方面。让我们一起找找这件事的其他可能性。",
            "这种思维模式可能会让你感到困扰。我们可以尝试用一些技巧来调整这种想法。",
            "你的担心是可以理解的，但让我们看看这些担心是否都是基于事实的。"
        ));
        
        // 行为建议模式
        REPLY_TEMPLATES.put("BEHAVIORAL_ADVICE", Arrays.asList(
            "基于你的情况，我建议你可以尝试一些放松技巧，比如深呼吸或者冥想。",
            "也许你可以考虑建立一个日常作息表，规律的生活有助于改善情绪状态。",
            "运动是很好的情绪调节方式，即使是简单的散步也能带来积极的影响。",
            "你可以尝试写日记来记录自己的感受和想法，这有助于更好地了解自己。",
            "与信任的朋友或家人分享你的感受，可能会让你感到轻松一些。"
        ));
        
        // 信息收集模式
        REPLY_TEMPLATES.put("INFORMATION_GATHERING", Arrays.asList(
            "能告诉我更多关于这个情况的细节吗？这样我能更好地理解你的处境。",
            "这种情况持续多长时间了？了解时间线有助于我们找到合适的应对方法。",
            "你之前是否遇到过类似的情况？如果有的话，当时是怎么处理的？",
            "除了这个问题，还有其他让你感到困扰的事情吗？",
            "你觉得什么时候这种感受最强烈？是否有特定的触发因素？"
        ));
    }
    
    /**
     * 初始化关键词映射
     */
    private static void initializeKeywords() {
        // 情感支持关键词
        KEYWORD_TO_TYPE.put("难过", "EMOTIONAL_SUPPORT");
        KEYWORD_TO_TYPE.put("伤心", "EMOTIONAL_SUPPORT");
        KEYWORD_TO_TYPE.put("痛苦", "EMOTIONAL_SUPPORT");
        KEYWORD_TO_TYPE.put("孤独", "EMOTIONAL_SUPPORT");
        KEYWORD_TO_TYPE.put("害怕", "EMOTIONAL_SUPPORT");
        KEYWORD_TO_TYPE.put("焦虑", "EMOTIONAL_SUPPORT");
        KEYWORD_TO_TYPE.put("抑郁", "EMOTIONAL_SUPPORT");
        
        // 认知引导关键词
        KEYWORD_TO_TYPE.put("想法", "COGNITIVE_GUIDANCE");
        KEYWORD_TO_TYPE.put("思考", "COGNITIVE_GUIDANCE");
        KEYWORD_TO_TYPE.put("认为", "COGNITIVE_GUIDANCE");
        KEYWORD_TO_TYPE.put("觉得", "COGNITIVE_GUIDANCE");
        KEYWORD_TO_TYPE.put("担心", "COGNITIVE_GUIDANCE");
        KEYWORD_TO_TYPE.put("困惑", "COGNITIVE_GUIDANCE");
        
        // 行为建议关键词
        KEYWORD_TO_TYPE.put("怎么办", "BEHAVIORAL_ADVICE");
        KEYWORD_TO_TYPE.put("如何", "BEHAVIORAL_ADVICE");
        KEYWORD_TO_TYPE.put("方法", "BEHAVIORAL_ADVICE");
        KEYWORD_TO_TYPE.put("建议", "BEHAVIORAL_ADVICE");
        KEYWORD_TO_TYPE.put("帮助", "BEHAVIORAL_ADVICE");
        KEYWORD_TO_TYPE.put("改善", "BEHAVIORAL_ADVICE");
        
        // 信息收集关键词
        KEYWORD_TO_TYPE.put("?", "INFORMATION_GATHERING");
        KEYWORD_TO_TYPE.put("？", "INFORMATION_GATHERING");
        KEYWORD_TO_TYPE.put("什么", "INFORMATION_GATHERING");
        KEYWORD_TO_TYPE.put("为什么", "INFORMATION_GATHERING");
        KEYWORD_TO_TYPE.put("哪里", "INFORMATION_GATHERING");
    }
}