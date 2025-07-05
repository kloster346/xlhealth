// AI相关API接口
import { aiAPI } from './index'

/**
 * AI聊天对话
 * @param {Object} requestData - 请求数据
 * @param {string} requestData.message - 用户消息
 * @param {string} requestData.conversationId - 对话ID（可选）
 * @param {Object} requestData.context - 上下文信息（可选）
 * @returns {Promise} AI回复
 */
export const chatWithAI = async (requestData) => {
  try {
    const response = await aiAPI.chat(requestData)
    
    return {
      success: true,
      data: response,
      reply: response.reply || response.message || response.content,
      conversationId: response.conversationId
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || 'AI对话失败',
      error: error
    }
  }
}

/**
 * 流式AI聊天对话（模拟实现）
 * @param {Object} requestData - 请求数据
 * @param {Function} onMessage - 消息回调函数
 * @param {Function} onError - 错误回调函数
 * @param {Function} onComplete - 完成回调函数
 * @returns {Promise} 流式回复控制器
 */
export const chatWithAIStream = async (requestData, onMessage, onError, onComplete) => {
  try {
    // 使用普通聊天接口模拟流式响应
    const response = await chatWithAI(requestData)
    if (response.success) {
      // 模拟逐字输出
      const reply = response.reply || ''
      let currentText = ''
      
      for (let i = 0; i < reply.length; i++) {
        currentText += reply[i]
        onMessage && onMessage({
          content: currentText,
          isComplete: i === reply.length - 1
        })
        
        // 模拟打字延迟
        await new Promise(resolve => setTimeout(resolve, 50))
      }
      
      onComplete && onComplete(response.data)
    } else {
      onError && onError(response.error)
    }
    return response
  } catch (error) {
    onError && onError(error)
    return {
      success: false,
      message: error.message || '流式AI对话失败',
      error: error
    }
  }
}

/**
 * 获取AI健康状态
 * @returns {Promise} 健康状态
 */
export const getAIHealthStatus = async () => {
  try {
    const response = await aiAPI.healthCheck()
    
    return {
      success: true,
      data: response,
      status: response.status || 'healthy'
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取AI健康状态失败',
      error: error
    }
  }
}

/**
 * 获取AI统计信息
 * @returns {Promise} 统计信息
 */
export const getAIStats = async () => {
  try {
    const response = await aiAPI.getStats()
    
    return {
      success: true,
      data: response,
      stats: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取AI统计信息失败',
      error: error
    }
  }
}

/**
 * 获取AI配置
 * @returns {Promise} AI配置
 */
export const getAIConfig = async () => {
  try {
    const response = await aiAPI.getConfig()
    
    return {
      success: true,
      data: response,
      config: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取AI配置失败',
      error: error
    }
  }
}

// 注意：以下函数调用的接口在后端不存在，已被注释
// 如需使用这些功能，请先在后端实现对应的接口

/*
// 这些函数调用的接口在后端OpenAPI文档中不存在
// 如果需要这些功能，请先在后端添加对应的接口实现

export const getAIChatHistory = async (params = {}) => { ... }
export const clearAIChatHistory = async (params = {}) => { ... }
export const getAIPromptTemplates = async () => { ... }
export const createAIPromptTemplate = async (templateData) => { ... }
export const updateAIPromptTemplate = async (templateId, updateData) => { ... }
export const deleteAIPromptTemplate = async (templateId) => { ... }
*/

// AI服务对象
export const aiService = {
  chatWithAI,
  chatWithAIStream,
  getAIHealthStatus,
  getAIStats,
  getAIConfig
}

// 导出默认服务
export default aiService

// 兼容旧的API命名
export const getAIResponse = chatWithAI
export const getAIResponseStream = chatWithAIStream