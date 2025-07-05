// 系统相关API接口
import { systemAPI } from './index'

/**
 * 获取系统版本信息
 * @returns {Promise} 版本信息
 */
export const getSystemVersion = async () => {
  try {
    const response = await systemAPI.getVersion()
    
    return {
      success: true,
      data: response,
      version: response.version || response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取系统版本信息失败',
      error: error
    }
  }
}

/**
 * 系统健康检查
 * @returns {Promise} 健康状态
 */
export const healthCheck = async () => {
  try {
    const response = await systemAPI.healthCheck()
    
    return {
      success: true,
      data: response,
      status: response.status || 'healthy',
      healthy: response.status === 'healthy' || response.healthy === true
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '健康检查失败',
      error: error,
      healthy: false
    }
  }
}

// 注意：以下函数调用的接口在后端不存在，已被注释
// 如需使用这些功能，请先在后端实现对应的接口

/*
// 这些函数调用的接口在后端OpenAPI文档中不存在
// 如果需要这些功能，请先在后端添加对应的接口实现

export const getSystemConfig = async () => { ... }
export const updateSystemConfig = async (configData) => { ... }
export const getSystemStats = async () => { ... }
export const getSystemLogs = async (params = {}) => { ... }
export const clearSystemCache = async () => { ... }
export const createSystemBackup = async () => { ... }
export const getSystemBackups = async (params = {}) => { ... }
export const restoreSystemBackup = async (backupId) => { ... }
export const deleteSystemBackup = async (backupId) => { ... }
*/

// 系统服务对象
export const systemService = {
  getSystemVersion,
  healthCheck
}

// 导出默认服务
export default systemService