// API 接口管理文件
// 为后续与后端API对接提供统一的接口管理

import axios from 'axios'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 添加认证token（从localStorage获取）
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    // 处理后端统一响应格式 ApiResponse
    const { data } = response
    if (data && typeof data === 'object') {
      // 检查是否是后端的统一响应格式
      if (Object.prototype.hasOwnProperty.call(data, 'code') && Object.prototype.hasOwnProperty.call(data, 'message')) {
        if (data.code === 0) {
          // 成功响应，返回data字段
          return data.data !== undefined ? data.data : data
        } else {
          // 业务错误，抛出错误
          const error = new Error(data.message || '请求失败')
          error.code = data.code
          error.details = data.details
          throw error
        }
      }
    }
    // 兼容其他格式的响应
    return data
  },
  error => {
    console.error('响应拦截器错误:', error)
    
    // 处理HTTP错误状态码
    if (error.response) {
      const { status, data } = error.response
      
      // 处理后端ErrorResponse格式
      if (data && data.code && data.message) {
        error.message = data.message
        error.code = data.code
        error.details = data.details
        error.timestamp = data.timestamp
        error.path = data.path
      }
      
      switch (status) {
        case 401:
          // 未授权，清除token并跳转登录
          localStorage.removeItem('token')
          // 这里可以添加路由跳转逻辑
          break
        case 403:
          console.error('访问被拒绝')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 409:
          console.error('资源冲突:', error.message)
          break
        case 500:
          console.error('服务器内部错误:', error.message)
          break
        default:
          console.error('请求失败:', error.message || '未知错误')
      }
    } else if (error.request) {
      console.error('网络错误，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
    }
    
    return Promise.reject(error)
  }
)

// API 接口定义
export const authAPI = {
  // 用户登录
  login: (credentials) => {
    // 后端使用 usernameOrEmail 字段
    const loginData = {
      usernameOrEmail: credentials.email || credentials.username || credentials.usernameOrEmail,
      password: credentials.password
    }
    return apiClient.post('/api/v1/auth/login', loginData)
  },
  
  // 用户注册
  register: (userData) => apiClient.post('/api/v1/auth/register', userData),
  
  // 验证访问令牌
  validate: () => apiClient.get('/api/v1/auth/validate'),
  
  // 刷新token
  refreshToken: () => apiClient.post('/api/v1/auth/refresh'),
  
  // 用户登出
  logout: () => apiClient.post('/api/v1/auth/logout')
}

export const userAPI = {
  // 获取用户信息
  getUserInfo: () => apiClient.get('/api/v1/users/profile'),
  
  // 更新用户信息
  updateUserInfo: (userData) => apiClient.put('/api/v1/users/profile', userData),
  
  // 修改密码
  changePassword: (passwordData) => apiClient.put('/api/v1/users/password', passwordData),
  
  // 上传头像
  uploadAvatar: (formData) => apiClient.post('/api/v1/users/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 对话管理API
export const conversationAPI = {
  // 获取对话列表
  getConversations: (params) => apiClient.get('/api/v1/conversations', { params }),
  
  // 获取特定对话详情
  getConversation: (conversationId) => apiClient.get(`/api/v1/conversations/${conversationId}`),
  
  // 创建新对话
  createConversation: (conversationData) => apiClient.post('/api/v1/conversations', conversationData),
  
  // 更新对话
  updateConversation: (conversationId, updateData) => apiClient.put(`/api/v1/conversations/${conversationId}`, updateData),
  
  // 删除对话
  deleteConversation: (conversationId) => apiClient.delete(`/api/v1/conversations/${conversationId}`),
  
  // 归档对话
  archiveConversation: (conversationId) => apiClient.put(`/api/v1/conversations/${conversationId}/archive`),
  
  // 激活对话
  activateConversation: (conversationId) => apiClient.put(`/api/v1/conversations/${conversationId}/activate`),
  
  // 获取对话统计
  getConversationStats: () => apiClient.get('/api/v1/conversations/stats'),
  
  // 根据状态获取对话
  getConversationsByStatus: (status) => apiClient.get(`/api/v1/conversations/status/${status}`)
}

// 消息管理API
export const messageAPI = {
  // 获取对话中的消息列表
  getMessages: (conversationId, params) => apiClient.get(`/api/v1/conversations/${conversationId}/messages`, { params }),
  
  // 发送消息
  sendMessage: (conversationId, messageData) => apiClient.post(`/api/v1/conversations/${conversationId}/messages`, messageData),
  
  // 删除消息
  deleteMessage: (conversationId, messageId) => apiClient.delete(`/api/v1/conversations/${conversationId}/messages/${messageId}`),
  
  // AI回复生成
  generateAIReply: (conversationId, messageData) => apiClient.post(`/api/v1/conversations/${conversationId}/messages/ai-reply`, messageData),
  
  // 批量操作消息
  batchMessages: (conversationId, batchData) => apiClient.post(`/api/v1/conversations/${conversationId}/messages/batch`, batchData),
  
  // 获取消息数量
  getMessageCount: (conversationId) => apiClient.get(`/api/v1/conversations/${conversationId}/messages/count`),
  
  // 获取最后一条消息
  getLastMessage: (conversationId) => apiClient.get(`/api/v1/conversations/${conversationId}/messages/last`),
  
  // 清空对话消息
  clearMessages: (conversationId) => apiClient.delete(`/api/v1/conversations/${conversationId}/messages/clear`),
  
  // 获取消息统计
  getMessageStatistics: (conversationId) => apiClient.get(`/api/v1/conversations/${conversationId}/messages/statistics`),
  
  // 更新消息状态
  updateMessageStatus: (conversationId, messageId, statusData) => apiClient.put(`/api/v1/conversations/${conversationId}/messages/${messageId}/status`, statusData)
}

// 系统健康检查API
export const systemAPI = {
  // 健康检查
  healthCheck: () => apiClient.get('/api/v1/health'),
  
  // 获取系统版本信息
  getVersion: () => apiClient.get('/api/v1/health/version')
}

// AI功能API
export const aiAPI = {
  // AI聊天
  chat: (chatData) => apiClient.post('/api/ai/chat', chatData),
  
  // AI健康检查
  healthCheck: () => apiClient.get('/api/ai/health'),
  
  // 获取AI统计信息
  getStats: () => apiClient.get('/api/ai/stats'),
  
  // 获取AI配置
  getConfig: () => apiClient.get('/api/ai/config')
}

// 兼容旧的chatAPI命名
export const chatAPI = {
  // 发送消息（兼容旧接口）
  sendMessage: (conversationId, message) => messageAPI.sendMessage(conversationId, { content: message }),
  
  // 获取对话历史（兼容旧接口）
  getChatHistory: (conversationId, params) => messageAPI.getMessages(conversationId, params),
  
  // 创建新对话（兼容旧接口）
  createChat: (title) => conversationAPI.createConversation({ title: title || '新对话' }),
  
  // 删除对话（兼容旧接口）
  deleteChat: (chatId) => conversationAPI.deleteConversation(chatId)
}

// 导出 axios 实例供其他地方使用
export default apiClient