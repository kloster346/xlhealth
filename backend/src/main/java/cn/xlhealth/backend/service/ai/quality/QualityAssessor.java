package cn.xlhealth.backend.service.ai.quality;

import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;

/**
 * AI回复质量评估器接口
 * 负责评估AI回复的质量
 */
public interface QualityAssessor {
    
    /**
     * 评估AI回复质量
     * @param request 原始AI请求
     * @param response AI回复
     * @return 质量评分（0-100）
     */
    int assessQuality(AIRequest request, AIResponse response);
    
    /**
     * 检查回复相关性
     * @param request 原始AI请求
     * @param response AI回复
     * @return 相关性评分（0-100）
     */
    int checkRelevance(AIRequest request, AIResponse response);
    
    /**
     * 检查专业性
     * @param response AI回复
     * @return 专业性评分（0-100）
     */
    int checkProfessionalism(AIResponse response);
    
    /**
     * 检查情感适宜性
     * @param request 原始AI请求
     * @param response AI回复
     * @return 情感适宜性评分（0-100）
     */
    int checkEmotionalAppropriateness(AIRequest request, AIResponse response);
    
    /**
     * 检查安全性
     * @param response AI回复
     * @return 安全性评分（0-100）
     */
    int checkSafety(AIResponse response);
    
    /**
     * 检查完整性
     * @param response AI回复
     * @return 完整性评分（0-100）
     */
    int checkCompleteness(AIResponse response);
    
    /**
     * 判断回复是否通过质量评估
     * @param score 质量评分
     * @param minThreshold 最低阈值
     * @return 是否通过
     */
    boolean isPassing(int score, int minThreshold);
}