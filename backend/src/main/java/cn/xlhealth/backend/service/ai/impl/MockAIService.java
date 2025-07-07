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
            // 设置模拟的token数量（基于内容长度估算）
            response.setTokenCount(content.length() / 4); // 简单估算：4个字符约等于1个token

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
        return aiServiceProperties.isMockMode();
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public java.util.Map<String, Object> getConfigInfo() {
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("provider", PROVIDER_NAME);
        config.put("mockMode", aiServiceProperties.isMockMode());
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

        // 优先级匹配：按照匹配度和优先级排序
        Map<String, Integer> typeScores = new HashMap<>();

        // 检查关键词匹配并计分
        for (Map.Entry<String, String> entry : KEYWORD_TO_TYPE.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                String type = entry.getValue();
                typeScores.put(type, typeScores.getOrDefault(type, 0) + 1);
            }
        }

        // 如果有匹配的关键词，返回得分最高的类型
        if (!typeScores.isEmpty()) {
            return typeScores.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("EMOTIONAL_SUPPORT");
        }

        // 基于消息长度和内容特征进行进一步分析
        if (lowerMessage.contains("?") || lowerMessage.contains("？")) {
            return "INFORMATION_GATHERING";
        }

        if (message.length() > 50) {
            return "COGNITIVE_GUIDANCE";
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

        // 增加个性化元素
        String userMessage = request.getUserMessage();
        String personalizedPrefix = generatePersonalizedPrefix(userMessage, replyType);

        // 模板变量替换
        String content = template.replace("{user}", "朋友")
                .replace("{time}", getTimeGreeting());

        // 添加个性化前缀（有30%概率）
        if (ThreadLocalRandom.current().nextDouble() < 0.3 && !personalizedPrefix.isEmpty()) {
            content = personalizedPrefix + " " + content;
        }

        // 添加随机的结尾语（有20%概率）
        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
            content += " " + getRandomEnding();
        }

        return content;
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
     * 生成个性化前缀
     */
    private String generatePersonalizedPrefix(String userMessage, String replyType) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "";
        }

        List<String> prefixes = new ArrayList<>();

        // 根据消息类型添加不同的前缀
        switch (replyType) {
            case "EMOTIONAL_SUPPORT":
                prefixes.addAll(Arrays.asList(
                        "我注意到你提到了", "听起来你正在经历", "从你的话中我感受到"));
                break;
            case "COGNITIVE_GUIDANCE":
                prefixes.addAll(Arrays.asList(
                        "关于你提到的想法", "针对你的这种思考", "从你的描述来看"));
                break;
            case "BEHAVIORAL_ADVICE":
                prefixes.addAll(Arrays.asList(
                        "基于你的情况", "考虑到你的需求", "针对你的问题"));
                break;
            case "INFORMATION_GATHERING":
                prefixes.addAll(Arrays.asList(
                        "为了更好地帮助你", "让我们深入了解一下", "关于你的疑问"));
                break;
        }

        if (prefixes.isEmpty()) {
            return "";
        }

        return prefixes.get(ThreadLocalRandom.current().nextInt(prefixes.size()));
    }

    /**
     * 获取随机结尾语
     */
    private String getRandomEnding() {
        List<String> endings = Arrays.asList(
                "你觉得这个建议怎么样？",
                "希望这对你有所帮助。",
                "如果你还有其他想法，随时可以告诉我。",
                "我们可以继续深入探讨这个话题。",
                "记住，每一步进步都值得肯定。",
                "你并不孤单，我会一直陪伴你。",
                "相信你有能力处理好这些挑战。");

        return endings.get(ThreadLocalRandom.current().nextInt(endings.size()));
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
                "我听到了你的痛苦，也看到了你的坚强。请相信，这些困难的时刻会过去的。",
                "每个人的情感体验都是独特而珍贵的，你的感受值得被认真对待和理解。",
                "在这个{time}，能够勇敢地表达内心的感受，这本身就是一种成长。",
                "情绪就像天气一样会变化，现在的阴霾终将散去，阳光会重新照进你的生活。",
                "你的坚持和努力我都看在眼里，即使在最困难的时候，你依然在寻求帮助，这很了不起。",
                "让我们一起慢慢地、温柔地面对这些情感，没有什么是必须独自承受的。"));

        // 认知引导模式
        REPLY_TEMPLATES.put("COGNITIVE_GUIDANCE", Arrays.asList(
                "让我们一起来看看这个问题的不同角度。有时候换个视角，问题可能会有新的解决方案。",
                "你提到的这种想法很常见，但我们可以尝试用更客观的方式来分析一下这个情况。",
                "当我们陷入负面思维时，往往会忽略一些积极的方面。让我们一起找找这件事的其他可能性。",
                "这种思维模式可能会让你感到困扰。我们可以尝试用一些技巧来调整这种想法。",
                "你的担心是可以理解的，但让我们看看这些担心是否都是基于事实的。",
                "思维就像一面镜子，有时候需要调整角度才能看到更清晰的画面。",
                "每个想法都有它存在的原因，让我们探索一下这个想法背后的真实需求。",
                "有时候我们的大脑会放大问题，让我们尝试用更平衡的方式来看待这个情况。",
                "你的思考很深入，现在让我们看看是否还有其他的理解方式。",
                "认知的灵活性是一种很重要的能力，让我们一起练习用不同的方式思考这个问题。"));

        // 行为建议模式
        REPLY_TEMPLATES.put("BEHAVIORAL_ADVICE", Arrays.asList(
                "基于你的情况，我建议你可以尝试一些放松技巧，比如深呼吸或者冥想。",
                "也许你可以考虑建立一个日常作息表，规律的生活有助于改善情绪状态。",
                "运动是很好的情绪调节方式，即使是简单的散步也能带来积极的影响。",
                "你可以尝试写日记来记录自己的感受和想法，这有助于更好地了解自己。",
                "与信任的朋友或家人分享你的感受，可能会让你感到轻松一些。",
                "尝试建立一些小的、可实现的目标，每完成一个都是对自己的肯定。",
                "音乐、艺术或其他创造性活动可能会帮助你表达和处理情感。",
                "保持充足的睡眠和健康的饮食，身体的健康会影响心理状态。",
                "学会说'不'也是一种重要的自我保护技能，不要给自己太大压力。",
                "可以尝试一些正念练习，专注于当下的感受而不是过去或未来的担忧。"));

        // 信息收集模式
        REPLY_TEMPLATES.put("INFORMATION_GATHERING", Arrays.asList(
                "能告诉我更多关于这个情况的细节吗？这样我能更好地理解你的处境。",
                "这种情况持续多长时间了？了解时间线有助于我们找到合适的应对方法。",
                "你之前是否遇到过类似的情况？如果有的话，当时是怎么处理的？",
                "除了这个问题，还有其他让你感到困扰的事情吗？",
                "你觉得什么时候这种感受最强烈？是否有特定的触发因素？",
                "在这种情况下，你通常会有什么样的想法或反应？",
                "有没有什么特别的事件或变化引发了这种感受？",
                "你希望通过我们的对话达到什么样的目标？",
                "在你的生活中，有哪些支持你的资源或人际关系？",
                "你觉得什么样的帮助对你来说最有意义？"));
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