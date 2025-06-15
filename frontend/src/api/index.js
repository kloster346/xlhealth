// API 接口管理文件
// 为后续与后端API对接提供统一的接口管理

import axios from 'axios'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080/api',
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
    return response.data
  },
  error => {
    console.error('响应拦截器错误:', error)
    
    // 处理常见错误状态码
    if (error.response) {
      switch (error.response.status) {
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
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error('请求失败:', error.response.data.message || '未知错误')
      }
    } else if (error.request) {
      console.error('网络错误，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
    }
    
    return Promise.reject(error)
  }
)

// API 接口定义（为后续开发预留）
export const authAPI = {
  // 用户登录
  login: (credentials) => apiClient.post('/auth/login', credentials),
  
  // 用户注册
  register: (userData) => apiClient.post('/auth/register', userData),
  
  // 刷新token
  refreshToken: (refreshToken) => apiClient.post('/auth/refresh', { refreshToken }),
  
  // 用户登出
  logout: () => apiClient.post('/auth/logout')
}

export const userAPI = {
  // 获取用户信息
  getUserInfo: () => apiClient.get('/user/profile'),
  
  // 更新用户信息
  updateUserInfo: (userData) => apiClient.put('/user/profile', userData),
  
  // 修改密码
  changePassword: (passwordData) => apiClient.put('/user/password', passwordData)
}

export const chatAPI = {
  // 发送消息
  sendMessage: (message) => apiClient.post('/chat/message', message),
  
  // 获取对话历史
  getChatHistory: (params) => apiClient.get('/chat/history', { params }),
  
  // 创建新对话
  createChat: () => apiClient.post('/chat/create'),
  
  // 删除对话
  deleteChat: (chatId) => apiClient.delete(`/chat/${chatId}`)
}

// 导出 axios 实例供其他地方使用
export default apiClient