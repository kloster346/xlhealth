// 本地存储工具类
// 提供统一的本地存储管理功能

/**
 * 本地存储工具类
 * 封装 localStorage 操作，提供类型安全和错误处理
 */
export class StorageUtils {
  /**
   * 设置存储项
   * @param {string} key - 存储键
   * @param {any} value - 存储值
   * @param {number} expireTime - 过期时间（毫秒），可选
   * @returns {boolean} - 是否设置成功
   */
  static setItem(key, value, expireTime = null) {
    try {
      const data = {
        value,
        timestamp: Date.now(),
        expireTime: expireTime ? Date.now() + expireTime : null
      }
      localStorage.setItem(key, JSON.stringify(data))
      return true
    } catch (error) {
      console.error('LocalStorage 设置失败:', error)
      return false
    }
  }

  /**
   * 获取存储项
   * @param {string} key - 存储键
   * @returns {any} - 存储值，如果不存在或已过期则返回 null
   */
  static getItem(key) {
    try {
      const item = localStorage.getItem(key)
      if (!item) return null

      const data = JSON.parse(item)
      
      // 检查是否过期
      if (data.expireTime && Date.now() > data.expireTime) {
        this.removeItem(key)
        return null
      }

      return data.value
    } catch (error) {
      console.error('LocalStorage 获取失败:', error)
      return null
    }
  }

  /**
   * 删除存储项
   * @param {string} key - 存储键
   * @returns {boolean} - 是否删除成功
   */
  static removeItem(key) {
    try {
      localStorage.removeItem(key)
      return true
    } catch (error) {
      console.error('LocalStorage 删除失败:', error)
      return false
    }
  }

  /**
   * 清空所有存储
   * @returns {boolean} - 是否清空成功
   */
  static clear() {
    try {
      localStorage.clear()
      return true
    } catch (error) {
      console.error('LocalStorage 清空失败:', error)
      return false
    }
  }

  /**
   * 获取所有存储键
   * @returns {string[]} - 所有存储键的数组
   */
  static getAllKeys() {
    try {
      const keys = []
      for (let i = 0; i < localStorage.length; i++) {
        keys.push(localStorage.key(i))
      }
      return keys
    } catch (error) {
      console.error('获取存储键失败:', error)
      return []
    }
  }

  /**
   * 检查存储项是否存在
   * @param {string} key - 存储键
   * @returns {boolean} - 是否存在
   */
  static hasItem(key) {
    return this.getItem(key) !== null
  }

  /**
   * 获取存储大小（近似值，以字符数计算）
   * @returns {number} - 存储大小
   */
  static getStorageSize() {
    try {
      let total = 0
      for (let key in localStorage) {
        if (Object.prototype.hasOwnProperty.call(localStorage, key)) {
          total += localStorage[key].length + key.length
        }
      }
      return total
    } catch (error) {
      console.error('获取存储大小失败:', error)
      return 0
    }
  }
}

// 专用存储键常量
export const STORAGE_KEYS = {
  // 用户认证相关
  TOKEN: 'auth_token',
  USER_INFO: 'user_info',
  REFRESH_TOKEN: 'refresh_token',
  
  // 应用设置
  APP_SETTINGS: 'app_settings',
  THEME: 'app_theme',
  LANGUAGE: 'app_language',
  
  // 对话相关
  CHAT_HISTORY: 'chat_history',
  CURRENT_CHAT: 'current_chat',
  DRAFT_MESSAGE: 'draft_message',
  
  // 用户偏好
  USER_PREFERENCES: 'user_preferences'
}

// 便捷方法
export const tokenStorage = {
  set: (token, expireTime = 24 * 60 * 60 * 1000) => // 默认24小时过期
    StorageUtils.setItem(STORAGE_KEYS.TOKEN, token, expireTime),
  get: () => StorageUtils.getItem(STORAGE_KEYS.TOKEN),
  remove: () => StorageUtils.removeItem(STORAGE_KEYS.TOKEN),
  exists: () => StorageUtils.hasItem(STORAGE_KEYS.TOKEN)
}

export const userStorage = {
  set: (userInfo) => StorageUtils.setItem(STORAGE_KEYS.USER_INFO, userInfo),
  get: () => StorageUtils.getItem(STORAGE_KEYS.USER_INFO),
  remove: () => StorageUtils.removeItem(STORAGE_KEYS.USER_INFO),
  exists: () => StorageUtils.hasItem(STORAGE_KEYS.USER_INFO)
}

export const chatStorage = {
  setHistory: (history) => StorageUtils.setItem(STORAGE_KEYS.CHAT_HISTORY, history),
  getHistory: () => StorageUtils.getItem(STORAGE_KEYS.CHAT_HISTORY) || [],
  setCurrent: (chat) => StorageUtils.setItem(STORAGE_KEYS.CURRENT_CHAT, chat),
  getCurrent: () => StorageUtils.getItem(STORAGE_KEYS.CURRENT_CHAT),
  clearHistory: () => StorageUtils.removeItem(STORAGE_KEYS.CHAT_HISTORY),
  clearCurrent: () => StorageUtils.removeItem(STORAGE_KEYS.CURRENT_CHAT)
}

// 默认导出
export default StorageUtils