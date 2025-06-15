// 基础模拟数据服务
// 为后续功能开发提供数据结构模板

// 应用配置数据
export const appConfig = {
  name: 'AI心理咨询系统',
  version: '1.0.0',
  description: '基于AI技术的心理健康咨询平台',
  features: [
    '智能对话咨询',
    '心理健康评估',
    '情绪状态跟踪',
    '个性化建议'
  ]
}

// 基础数据结构模板（为后续开发预留）
export const dataTemplates = {
  user: {
    id: null,
    username: '',
    email: '',
    nickname: '',
    createdAt: ''
  },
  
  message: {
    id: null,
    type: '',
    title: '',
    content: '',
    timestamp: '',
    isRead: false
  }
}

// 本地存储工具类
export const localStorageUtils = {
  // 设置数据
  setItem(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
      return true
    } catch (error) {
      console.error('LocalStorage设置失败:', error)
      return false
    }
  },
  
  // 获取数据
  getItem(key) {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error('LocalStorage获取失败:', error)
      return null
    }
  },
  
  // 删除数据
  removeItem(key) {
    try {
      localStorage.removeItem(key)
      return true
    } catch (error) {
      console.error('LocalStorage删除失败:', error)
      return false
    }
  },
  
  // 清空所有数据
  clear() {
    try {
      localStorage.clear()
      return true
    } catch (error) {
      console.error('LocalStorage清空失败:', error)
      return false
    }
  }
}