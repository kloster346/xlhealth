// 统一的API服务导出文件
// 提供所有API服务的统一入口

// 导入所有需要的函数
import {
  loginUser,
  registerUser,
  validateToken,
  refreshToken,
  logoutUser,
  getCurrentUser,
  isLoggedIn,
  getAccessToken,
  clearAuthData
} from './auth'

import {
  getCurrentUserInfo,
  updateUserInfo,
  changePassword,
  uploadAvatar,
} from './user'

import {
  getConversations,
  createConversation,
  getConversation,
  updateConversation,
  deleteConversation,
  getMessages,
  sendMessage,
  getMessage,
  updateMessage,
  deleteMessage,
} from './chat'

import {
  chatWithAI,
  chatWithAIStream,
  getAIHealthStatus,
  getAIStats,
  getAIConfig,
} from './ai'

import {
  getSystemVersion,
  healthCheck,
} from './system'

import {
} from './index'

// 认证相关服务
export {
  loginUser,
  registerUser,
  validateToken,
  refreshToken,
  logoutUser,
  getCurrentUser,
  isLoggedIn,
  getAccessToken,
  clearAuthData
} from './auth'

// 用户管理服务
export {
  getCurrentUserInfo,
  updateUserInfo,
  changePassword,
  uploadAvatar,
  userService
} from './user'

// 聊天对话服务
export {
  getConversations,
  createConversation,
  getConversation,
  updateConversation,
  deleteConversation,
  getMessages,
  sendMessage,
  getMessage,
  updateMessage,
  deleteMessage,
  getAIResponse,
  getAIResponseStream,
  chatService,
  getAllConversations,
  createNewConversation,
  sendMessageToConversation,
  deleteConversationById,
  clearAllConversations
} from './chat'

// AI服务
export {
  chatWithAI,
  chatWithAIStream,
  getAIHealthStatus,
  getAIStats,
  getAIConfig,
  aiService
} from './ai'

// 系统服务
export {
  getSystemVersion,
  healthCheck,
  systemService
} from './system'

// 基础API实例（从index.js导出）
export {
  authAPI,
  userAPI,
  conversationAPI,
  messageAPI,
  aiAPI,
  systemAPI,
  // 兼容旧的chatAPI
  chatAPI
} from './index'

// 默认导出所有服务的集合
const apiServices = {
  // 认证服务
  auth: {
    login: loginUser,
    register: registerUser,
    validate: validateToken,
    refresh: refreshToken,
    logout: logoutUser,
    getCurrentUser,
    isLoggedIn,
    getAccessToken,
    clearAuthData
  },
  
  // 用户服务
  user: {
    getCurrentInfo: getCurrentUserInfo,
    updateInfo: updateUserInfo,
    changePassword,
    uploadAvatar
  },
  
  // 对话服务
  conversation: {
    getList: getConversations,
    create: createConversation,
    getById: getConversation,
    update: updateConversation,
    delete: deleteConversation
  },
  
  // 消息服务
  message: {
    getList: getMessages,
    send: sendMessage,
    getById: getMessage,
    update: updateMessage,
    delete: deleteMessage
  },
  
  // AI服务
  ai: {
    chat: chatWithAI,
    chatStream: chatWithAIStream,
    healthCheck: getAIHealthStatus,
    getStats: getAIStats,
    getConfig: getAIConfig
  },
  
  // 系统服务
  system: {
    getVersion: getSystemVersion,
    healthCheck: healthCheck
  }
}

export default apiServices

// 兼容旧的导入方式
export const API = apiServices