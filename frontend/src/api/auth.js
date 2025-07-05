// 认证相关API接口
import { authAPI } from './index'

/**
 * 用户登录
 * @param {Object} credentials - 登录凭据
 * @param {string} credentials.usernameOrEmail - 用户名或邮箱
 * @param {string} credentials.password - 密码
 * @returns {Promise} 登录结果
 */
export const loginUser = async (credentials) => {
  try {
    const response = await authAPI.login(credentials)
    
    // 保存token到localStorage
    if (response.accessToken) {
      localStorage.setItem('token', response.accessToken)
      localStorage.setItem('tokenType', response.tokenType || 'Bearer')
      localStorage.setItem('expiresIn', response.expiresIn)
      
      // 保存用户信息
      if (response.userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(response.userInfo))
      }
    }
    
    return {
      success: true,
      message: '登录成功',
      data: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '登录失败',
      error: error
    }
  }
}

/**
 * 用户注册
 * @param {Object} userData - 用户注册数据
 * @param {string} userData.username - 用户名
 * @param {string} userData.email - 邮箱
 * @param {string} userData.password - 密码
 * @param {string} userData.nickname - 昵称
 * @returns {Promise} 注册结果
 */
export const registerUser = async (userData) => {
  try {
    const response = await authAPI.register(userData)
    
    return {
      success: true,
      message: '注册成功',
      data: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '注册失败',
      error: error
    }
  }
}

/**
 * 验证访问令牌
 * @returns {Promise} 验证结果
 */
export const validateToken = async () => {
  try {
    const response = await authAPI.validate()
    
    return {
      success: true,
      valid: response === true,
      data: response
    }
  } catch (error) {
    return {
      success: false,
      valid: false,
      message: error.message || '令牌验证失败',
      error: error
    }
  }
}

/**
 * 刷新访问令牌
 * @returns {Promise} 刷新结果
 */
export const refreshToken = async () => {
  try {
    const response = await authAPI.refreshToken()
    
    // 更新token
    if (response.accessToken) {
      localStorage.setItem('token', response.accessToken)
      localStorage.setItem('tokenType', response.tokenType || 'Bearer')
      localStorage.setItem('expiresIn', response.expiresIn)
      
      // 更新用户信息
      if (response.userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(response.userInfo))
      }
    }
    
    return {
      success: true,
      message: '令牌刷新成功',
      data: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '令牌刷新失败',
      error: error
    }
  }
}

/**
 * 用户登出
 * @returns {Promise} 登出结果
 */
export const logoutUser = async () => {
  try {
    await authAPI.logout()
    
    // 清除本地存储的认证信息
    localStorage.removeItem('token')
    localStorage.removeItem('tokenType')
    localStorage.removeItem('expiresIn')
    localStorage.removeItem('userInfo')
    
    return {
      success: true,
      message: '登出成功'
    }
  } catch (error) {
    // 即使后端登出失败，也要清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('tokenType')
    localStorage.removeItem('expiresIn')
    localStorage.removeItem('userInfo')
    
    return {
      success: true,
      message: '登出成功'
    }
  }
}

/**
 * 获取当前用户信息（从localStorage）
 * @returns {Object|null} 用户信息
 */
export const getCurrentUser = () => {
  try {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return null
  }
}

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
export const isLoggedIn = () => {
  const token = localStorage.getItem('token')
  return !!token
}

/**
 * 获取访问令牌
 * @returns {string|null} 访问令牌
 */
export const getAccessToken = () => {
  return localStorage.getItem('token')
}

/**
 * 清除认证信息
 */
export const clearAuthData = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('tokenType')
  localStorage.removeItem('expiresIn')
  localStorage.removeItem('userInfo')
}