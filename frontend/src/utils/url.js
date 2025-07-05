// URL 工具函数

/**
 * 获取API基础URL
 * @returns {string} API基础URL
 */
export function getApiBaseUrl() {
  return process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081'
}

/**
 * 构建完整的头像URL
 * @param {string} avatarUrl - 头像相对路径
 * @returns {string} 完整的头像URL
 */
export function buildAvatarUrl(avatarUrl) {
  if (!avatarUrl) {
    return ''
  }
  
  // 如果已经是完整URL，直接返回
  if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
    return avatarUrl
  }
  
  // 如果是相对路径，拼接API基础URL
  const baseUrl = getApiBaseUrl()
  // 确保路径正确拼接
  const cleanAvatarUrl = avatarUrl.startsWith('/') ? avatarUrl : `/${avatarUrl}`
  
  return `${baseUrl}${cleanAvatarUrl}`
}

/**
 * 构建完整的文件URL
 * @param {string} filePath - 文件相对路径
 * @returns {string} 完整的文件URL
 */
export function buildFileUrl(filePath) {
  if (!filePath) {
    return ''
  }
  
  // 如果已经是完整URL，直接返回
  if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
    return filePath
  }
  
  // 如果是相对路径，拼接API基础URL
  const baseUrl = getApiBaseUrl()
  // 确保路径正确拼接
  const cleanFilePath = filePath.startsWith('/') ? filePath : `/${filePath}`
  
  return `${baseUrl}${cleanFilePath}`
}