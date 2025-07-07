package cn.xlhealth.backend.service.ai.quality.impl;

import cn.xlhealth.backend.config.properties.AIServiceProperties;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import cn.xlhealth.backend.service.ai.quality.QualityAssessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 质量评估器实现类
 */
@Service
public class QualityAssessorImpl implements QualityAssessor {

    private static final Logger logger = LoggerFactory.getLogger(QualityAssessorImpl.class);

    @Autowired
    private AIServiceProperties aiServiceProperties;

    // 不当内容关键词
    private static final Set<String> INAPPROPRIATE_KEYWORDS = new HashSet<>(Arrays.asList(
            "自杀", "自残", "伤害", "杀人", "毒品", "违法", "犯罪",
            "酒精", "赌博", "吸毒", "暴力", "色情", "歧视"));

    // 专业术语
    private static final Set<String> PROFESSIONAL_TERMS = new HashSet<>(Arrays.asList(
            "认知行为疗法", "心理治疗", "精神分析", "焦虑障碍", "抑郁症",
            "心理健康", "情绪调节", "应激反应", "创伤后应激障碍", "心理咨询"));

    @Override
    public int assessQuality(AIRequest request, AIResponse response) {
        if (!response.isSuccess() || response.getContent() == null) {
            logger.warn("Cannot assess quality for failed response");
            return 0;
        }

        try {
            // 各维度评分
            int relevanceScore = checkRelevance(request, response);
            int professionalismScore = checkProfessionalism(response);
            int emotionalScore = checkEmotionalAppropriateness(request, response);
            int safetyScore = checkSafety(response);
            int completenessScore = checkCompleteness(response);

            // 加权平均
            int totalScore = (int) (relevanceScore * 0.3 +
                    professionalismScore * 0.2 +
                    emotionalScore * 0.2 +
                    safetyScore * 0.2 +
                    completenessScore * 0.1);

            // 设置评分
            response.setQualityScore(totalScore);

            logger.debug(
                    "Quality assessment for response: {} (relevance: {}, professionalism: {}, emotional: {}, safety: {}, completeness: {})",
                    totalScore, relevanceScore, professionalismScore, emotionalScore, safetyScore, completenessScore);

            return totalScore;

        } catch (Exception e) {
            logger.error("Error assessing response quality", e);
            return 50; // 默认中等质量
        }
    }

    @Override
    public int checkRelevance(AIRequest request, AIResponse response) {
        String userMessage = request.getUserMessage();
        String aiContent = response.getContent();
        String providerName = response.getProvider();

        if (userMessage == null || aiContent == null) {
            return 50;
        }

        // 对于MockAI服务，使用更宽松的相关性评估
        if ("MockAI".equals(providerName)) {
            return assessMockAIRelevance(request, response);
        }

        // 检查是否为简单问候
        String userLower = userMessage.toLowerCase().trim();
        String aiLower = aiContent.toLowerCase();
        
        // 简单问候的特殊处理
        if (isSimpleGreeting(userLower)) {
            if (aiLower.contains("你好") || aiLower.contains("您好") || 
                aiLower.contains("欢迎") || aiLower.contains("很高兴") ||
                aiLower.contains("帮助") || aiLower.contains("服务")) {
                return 85; // 问候得到适当回应
            } else {
                return 70; // 基本回应
            }
        }

        // 简单关键词匹配
        Set<String> userKeywords = extractKeywords(userMessage.toLowerCase());
        Set<String> responseKeywords = extractKeywords(aiContent.toLowerCase());

        // 计算关键词重叠度
        int matchCount = 0;
        for (String keyword : userKeywords) {
            if (responseKeywords.contains(keyword)) {
                matchCount++;
            }
        }

        // 计算相关性得分
        int score;
        if (userKeywords.isEmpty()) {
            score = 70; // 默认得分
        } else {
            score = Math.min(100, (int) (matchCount * 100.0 / userKeywords.size()));
        }

        // 检查是否包含问题回应
        boolean containsQuestion = userMessage.contains("?") || userMessage.contains("？");
        boolean containsAnswer = aiContent.length() > 50; // 简单假设长回复更可能包含答案

        if (containsQuestion && !containsAnswer) {
            score = Math.max(10, score - 30); // 问题没有得到充分回答
        }

        return score;
    }

    @Override
    public int checkProfessionalism(AIResponse response) {
        String content = response.getContent();
        if (content == null) {
            return 50;
        }

        int score = 70; // 基础分

        // 检查专业术语使用
        int termCount = 0;
        for (String term : PROFESSIONAL_TERMS) {
            if (content.contains(term)) {
                termCount++;
            }
        }

        // 根据专业术语使用情况加分
        score += Math.min(20, termCount * 5);

        // 检查回复长度，过短的回复可能缺乏专业性
        if (content.length() < 50) {
            score -= 20;
        } else if (content.length() > 200) {
            score += 10;
        }

        // 检查是否包含绝对化表述
        if (content.contains("一定") || content.contains("绝对") || content.contains("肯定")) {
            score -= 10; // 专业咨询通常避免绝对化表述
        }

        return Math.min(100, Math.max(0, score));
    }

    @Override
    public int checkEmotionalAppropriateness(AIRequest request, AIResponse response) {
        String userMessage = request.getUserMessage();
        String aiContent = response.getContent();
        String emotionalState = request.getEmotionalState();

        if (aiContent == null) {
            return 50;
        }

        int score = 70; // 基础分

        // 检查是否包含共情表达
        if (aiContent.contains("理解") || aiContent.contains("感受") ||
                aiContent.contains("明白") || aiContent.contains("共情")) {
            score += 15;
        }

        // 检查情绪匹配度
        boolean userNegativeEmotion = false;
        if (userMessage != null) {
            userNegativeEmotion = userMessage.contains("难过") || userMessage.contains("伤心") ||
                    userMessage.contains("痛苦") || userMessage.contains("焦虑") ||
                    userMessage.contains("抑郁") || userMessage.contains("绝望");
        }

        if (emotionalState != null) {
            userNegativeEmotion = userNegativeEmotion ||
                    emotionalState.contains("负面") || emotionalState.contains("消极");
        }

        // 如果用户表达负面情绪，回复应该包含安慰或支持
        if (userNegativeEmotion) {
            boolean containsComfort = aiContent.contains("支持") || aiContent.contains("鼓励") ||
                    aiContent.contains("希望") || aiContent.contains("相信") ||
                    aiContent.contains("陪伴") || aiContent.contains("会好");

            if (containsComfort) {
                score += 15;
            } else {
                score -= 20; // 没有对负面情绪给予适当回应
            }
        }

        return Math.min(100, Math.max(0, score));
    }

    @Override
    public int checkSafety(AIResponse response) {
        String content = response.getContent();
        if (content == null) {
            return 50;
        }

        // 默认安全
        int score = 100;

        // 检查不当内容
        for (String keyword : INAPPROPRIATE_KEYWORDS) {
            if (content.contains(keyword)) {
                // 上下文检查，区分提及和建议
                boolean isAdvice = content.contains(keyword + "建议") ||
                        content.contains("可以" + keyword) ||
                        content.contains("应该" + keyword) ||
                        content.contains("试试" + keyword);

                if (isAdvice) {
                    score -= 50; // 严重不当内容
                    logger.warn("Potentially harmful advice detected: {}", keyword);
                } else {
                    score -= 10; // 仅提及不当内容
                }
            }
        }

        // 检查是否包含免责声明
        boolean hasDisclaimer = content.contains("专业医疗") || content.contains("咨询医生") ||
                content.contains("不构成医疗建议") || content.contains("专业帮助");

        if (hasDisclaimer) {
            score += 10; // 有适当的免责声明
        }

        return Math.min(100, Math.max(0, score));
    }

    @Override
    public int checkCompleteness(AIResponse response) {
        String content = response.getContent();
        if (content == null) {
            return 0;
        }

        int score = 60; // 提高基础分

        // 基于长度的评估，对短回复更宽松
        if (content.length() < 10) {
            score = 40; // 极短
        } else if (content.length() < 20) {
            score = 60; // 短但可接受（如问候语回复）
        } else if (content.length() < 50) {
            score = 70; // 较短
        } else if (content.length() < 100) {
            score = 80; // 中等
        } else if (content.length() < 200) {
            score = 90; // 较长
        } else {
            score = 95; // 充分
        }

        // 检查是否有明确的结束语
        if (content.contains("希望能帮到你") || content.contains("祝你") ||
                content.contains("如有其他问题") || content.contains("期待")) {
            score += 5;
        }

        // 对简单问候给予额外加分
        if (content.contains("你好") || content.contains("您好") || 
            content.contains("欢迎") || content.contains("很高兴")) {
            score += 10;
        }

        return Math.min(100, score);
    }

    @Override
    public boolean isPassing(int score, int minThreshold) {
        return score >= minThreshold;
    }

    /**
     * 评估MockAI服务的相关性
     */
    private int assessMockAIRelevance(AIRequest request, AIResponse response) {
        String userMessage = request.getUserMessage();
        String aiContent = response.getContent();
        String replyType = response.getReplyType();
        
        int score = 75; // MockAI基础相关性得分
        
        // 根据回复类型调整得分
        if (replyType != null) {
            switch (replyType) {
                case "EMOTIONAL_SUPPORT":
                    // 检查用户是否表达了情感需求
                    if (containsEmotionalKeywords(userMessage)) {
                        score += 15;
                    }
                    break;
                case "COGNITIVE_GUIDANCE":
                    // 检查用户是否表达了认知相关内容
                    if (containsCognitiveKeywords(userMessage)) {
                        score += 15;
                    }
                    break;
                case "BEHAVIORAL_ADVICE":
                    // 检查用户是否寻求建议
                    if (containsAdviceKeywords(userMessage)) {
                        score += 15;
                    }
                    break;
                case "INFORMATION_GATHERING":
                    // 检查用户是否提出问题
                    if (userMessage.contains("?") || userMessage.contains("？") || 
                        userMessage.contains("什么") || userMessage.contains("如何") || 
                        userMessage.contains("为什么")) {
                        score += 15;
                    }
                    break;
            }
        }
        
        // 检查回复是否包含适当的心理咨询元素
        if (aiContent.contains("理解") || aiContent.contains("感受") || 
            aiContent.contains("支持") || aiContent.contains("帮助")) {
            score += 10;
        }
        
        // 确保回复长度合适
        if (aiContent.length() > 30) {
            score += 5;
        }
        
        return Math.min(100, score);
    }
    
    /**
     * 检查是否包含情感关键词
     */
    private boolean containsEmotionalKeywords(String message) {
        if (message == null) return false;
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("难过") || lowerMessage.contains("伤心") ||
               lowerMessage.contains("痛苦") || lowerMessage.contains("孤独") ||
               lowerMessage.contains("害怕") || lowerMessage.contains("焦虑") ||
               lowerMessage.contains("抑郁") || lowerMessage.contains("情绪") ||
               lowerMessage.contains("感觉") || lowerMessage.contains("心情");
    }
    
    /**
     * 检查是否包含认知关键词
     */
    private boolean containsCognitiveKeywords(String message) {
        if (message == null) return false;
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("想法") || lowerMessage.contains("思考") ||
               lowerMessage.contains("认为") || lowerMessage.contains("觉得") ||
               lowerMessage.contains("担心") || lowerMessage.contains("困惑") ||
               lowerMessage.contains("理解") || lowerMessage.contains("明白");
    }
    
    /**
     * 检查是否包含建议请求关键词
     */
    private boolean containsAdviceKeywords(String message) {
        if (message == null) return false;
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("怎么办") || lowerMessage.contains("如何") ||
               lowerMessage.contains("方法") || lowerMessage.contains("建议") ||
               lowerMessage.contains("帮助") || lowerMessage.contains("改善") ||
               lowerMessage.contains("解决") || lowerMessage.contains("处理");
    }
    
    /**
     * 提取文本中的关键词
     */
    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return keywords;
        }
        
        // 简单的分词和停用词过滤
        String[] words = text.split("[\\s,.!?;:，。！？；：、\\(\\)（）\\[\\]【】\\{\\}]+");
        
        for (String word : words) {
            word = word.trim();
            if (word.length() > 1 && !isStopWord(word)) {
                keywords.add(word);
            }
        }
        
        return keywords;
    }
    
    /**
     * 判断是否为简单问候
     */
    private boolean isSimpleGreeting(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String msg = message.trim().toLowerCase();
        
        // 常见的简单问候语
        return msg.equals("你好") || msg.equals("您好") || 
               msg.equals("hi") || msg.equals("hello") ||
               msg.equals("嗨") || msg.equals("哈喽") ||
               msg.equals("早上好") || msg.equals("下午好") || msg.equals("晚上好") ||
               msg.equals("你好吗") || msg.equals("您好吗") ||
               msg.equals("在吗") || msg.equals("在不在");
    }
    
    /**
     * 简单的停用词检查
     */
    private boolean isStopWord(String word) {
        // 简单的停用词列表
        String[] stopWords = {"的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这"};
        for (String stopWord : stopWords) {
            if (word.equals(stopWord)) {
                return true;
            }
        }
        return false;
    }
}