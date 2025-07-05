// 聊天相关API接口
import { conversationAPI, messageAPI, aiAPI } from './index'

/**
 * 获取对话列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.sortBy - 排序字段
 * @param {string} params.sortDirection - 排序方向
 * @returns {Promise} 对话列表
 */
export const getConversations = async (params = {}) => {
  try {
    const response = await conversationAPI.getConversations(params)
    
    return {
      success: true,
      data: response,
      conversations: response.records || response.content || response.data || response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取对话列表失败',
      error: error
    }
  }
}

/**
 * 创建新对话
 * @param {Object} conversationData - 对话数据
 * @param {string} conversationData.title - 对话标题
 * @param {string} conversationData.description - 对话描述
 * @returns {Promise} 创建结果
 */
export const createConversation = async (conversationData) => {
  try {
    const response = await conversationAPI.createConversation(conversationData)
    
    return {
      success: true,
      message: '对话创建成功',
      data: response,
      conversation: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '创建对话失败',
      error: error
    }
  }
}

/**
 * 获取对话详情
 * @param {string} conversationId - 对话ID
 * @returns {Promise} 对话详情
 */
export const getConversation = async (conversationId) => {
  try {
    const response = await conversationAPI.getConversation(conversationId)
    
    return {
      success: true,
      data: response,
      conversation: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取对话详情失败',
      error: error
    }
  }
}

/**
 * 更新对话
 * @param {string} conversationId - 对话ID
 * @param {Object} updateData - 更新数据
 * @returns {Promise} 更新结果
 */
export const updateConversation = async (conversationId, updateData) => {
  try {
    const response = await conversationAPI.updateConversation(conversationId, updateData)
    
    return {
      success: true,
      message: '对话更新成功',
      data: response,
      conversation: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '更新对话失败',
      error: error
    }
  }
}

/**
 * 删除对话
 * @param {string} conversationId - 对话ID
 * @returns {Promise} 删除结果
 */
export const deleteConversation = async (conversationId) => {
  try {
    await conversationAPI.deleteConversation(conversationId)
    
    return {
      success: true,
      message: '对话删除成功'
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '删除对话失败',
      error: error
    }
  }
}

/**
 * 获取对话中的消息列表
 * @param {string} conversationId - 对话ID
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.sortBy - 排序字段
 * @param {string} params.sortDirection - 排序方向
 * @returns {Promise} 消息列表
 */
export const getMessages = async (conversationId, params = {}) => {
  try {
    const response = await messageAPI.getMessages(conversationId, params)
    
    return {
      success: true,
      data: response,
      // 正确处理分页响应格式：response.data.records 是消息数组
      messages: response.data?.records || response.records || []
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取消息列表失败',
      error: error
    }
  }
}

/**
 * 发送消息
 * @param {string} conversationId - 对话ID
 * @param {Object} messageData - 消息数据
 * @param {string} messageData.content - 消息内容
 * @param {string} messageData.type - 消息类型
 * @returns {Promise} 发送结果
 */
export const sendMessage = async (conversationId, messageData) => {
  try {
    const response = await messageAPI.sendMessage(conversationId, messageData)
    
    return {
      success: true,
      message: '消息发送成功',
      data: response,
      messageData: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '发送消息失败',
      error: error
    }
  }
}

/**
 * 获取消息详情
 * @param {string} conversationId - 对话ID
 * @param {string} messageId - 消息ID
 * @returns {Promise} 消息详情
 */
export const getMessage = async (conversationId, messageId) => {
  try {
    const response = await messageAPI.getMessage(conversationId, messageId)
    
    return {
      success: true,
      data: response,
      message: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取消息详情失败',
      error: error
    }
  }
}

/**
 * 更新消息
 * @param {string} conversationId - 对话ID
 * @param {string} messageId - 消息ID
 * @param {Object} updateData - 更新数据
 * @returns {Promise} 更新结果
 */
export const updateMessage = async (conversationId, messageId, updateData) => {
  try {
    const response = await messageAPI.updateMessage(conversationId, messageId, updateData)
    
    return {
      success: true,
      message: '消息更新成功',
      data: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '更新消息失败',
      error: error
    }
  }
}

/**
 * 删除消息
 * @param {string} conversationId - 对话ID
 * @param {string} messageId - 消息ID
 * @returns {Promise} 删除结果
 */
export const deleteMessage = async (conversationId, messageId) => {
  try {
    await messageAPI.deleteMessage(conversationId, messageId)
    
    return {
      success: true,
      message: '消息删除成功'
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '删除消息失败',
      error: error
    }
  }
}

/**
 * 生成AI回复
 * @param {string} conversationId - 对话ID
 * @param {Object} messageData - 消息数据
 * @param {string} messageData.message - 用户消息内容
 * @param {string} messageData.emotionalState - 情绪状态（可选）
 * @returns {Promise} AI回复结果
 */
export const generateAIReply = async (conversationId, messageData) => {
  try {
    const response = await messageAPI.generateAIReply(conversationId, messageData)
    
    return {
      success: true,
      message: 'AI回复生成成功',
      data: response,
      aiMessage: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '生成AI回复失败',
      error: error
    }
  }
}

/**
 * 获取AI回复
 * @param {Object} requestData - 请求数据
 * @param {string} requestData.message - 用户消息
 * @param {string} requestData.conversationId - 对话ID（可选）
 * @returns {Promise} AI回复
 */
export const getAIResponse = async (requestData) => {
  try {
    const response = await aiAPI.chat(requestData)
    
    return {
      success: true,
      data: response,
      reply: response.reply || response.message || response.content
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取AI回复失败',
      error: error
    }
  }
}

/**
 * 流式获取AI回复
 * @param {Object} requestData - 请求数据
 * @param {Function} onMessage - 消息回调函数
 * @param {Function} onError - 错误回调函数
 * @param {Function} onComplete - 完成回调函数
 * @returns {Promise} 流式回复
 */
export const getAIResponseStream = async (requestData, onMessage, onError, onComplete) => {
  try {
    // 如果后端支持流式响应，使用相应的API
    if (aiAPI.chatStream) {
      return await aiAPI.chatStream(requestData, onMessage, onError, onComplete)
    } else {
      // 如果不支持流式，模拟流式响应
      const response = await getAIResponse(requestData)
      if (response.success) {
        onMessage && onMessage(response.reply)
        onComplete && onComplete(response.data)
      } else {
        onError && onError(response.error)
      }
      return response
    }
  } catch (error) {
    onError && onError(error)
    return {
      success: false,
      message: error.message || '获取AI流式回复失败',
      error: error
    }
  }
}

// 兼容旧的API命名和结构
export const chatService = {
  // 对话管理
  getConversations,
  createConversation,
  getConversation,
  updateConversation,
  deleteConversation,
  
  // 消息管理
  getMessages,
  sendMessage,
  getMessage,
  updateMessage,
  deleteMessage,
  
  // AI交互
  generateAIReply,
  getAIResponse,
  getAIResponseStream,
  
  // 兼容旧的方法名
  getAllConversations: getConversations,
  createNewConversation: createConversation,
  sendMessageToConversation: sendMessage,
  deleteConversationById: deleteConversation,
  clearAllConversations: async () => {
    console.warn('clearAllConversations 需要后端支持批量删除接口')
    return { success: false, message: '功能暂未实现' }
  }
}

// 导出默认服务
export default chatService

// 兼容旧的导出方式
export const getAllConversations = getConversations
export const createNewConversation = createConversation
export const sendMessageToConversation = sendMessage
export const deleteConversationById = deleteConversation
export const clearAllConversations = chatService.clearAllConversations