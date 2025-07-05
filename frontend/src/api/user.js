// 用户管理相关API接口
import { userAPI } from './index'

/**
 * 获取当前用户信息
 * @returns {Promise} 用户信息
 */
export const getCurrentUserInfo = async () => {
  try {
    const response = await userAPI.getUserInfo()
    
    return {
      success: true,
      data: response,
      user: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '获取用户信息失败',
      error: error
    }
  }
}

/**
 * 更新用户信息
 * @param {Object} updateData - 更新数据
 * @param {string} updateData.nickname - 昵称
 * @param {string} updateData.email - 邮箱
 * @param {string} updateData.avatarUrl - 头像URL
 * @returns {Promise} 更新结果
 */
export const updateUserInfo = async (updateData) => {
  try {
    const response = await userAPI.updateUserInfo(updateData)
    
    // 更新本地存储的用户信息
    if (response) {
      const currentUserInfo = localStorage.getItem('userInfo')
      if (currentUserInfo) {
        const userInfo = JSON.parse(currentUserInfo)
        const updatedUserInfo = { ...userInfo, ...response }
        localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
      }
    }
    
    return {
      success: true,
      message: '用户信息更新成功',
      data: response,
      user: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '更新用户信息失败',
      error: error
    }
  }
}

/**
 * 修改密码
 * @param {Object} passwordData - 密码数据
 * @param {string} passwordData.oldPassword - 旧密码
 * @param {string} passwordData.newPassword - 新密码
 * @returns {Promise} 修改结果
 */
export const changePassword = async (passwordData) => {
  try {
    const response = await userAPI.changePassword(passwordData)
    
    return {
      success: true,
      message: '密码修改成功',
      data: response
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '密码修改失败',
      error: error
    }
  }
}

// 以下函数对应的后端接口不存在，已注释
// /**
//  * 获取用户列表（管理员功能）
//  */
// export const getUserList = async (params = {}) => {
//   // 对应的后端接口不存在
// }

// /**
//  * 获取指定用户信息（管理员功能）
//  */
// export const getUserById = async (userId) => {
//   // 对应的后端接口不存在
// }

// /**
//  * 删除用户（管理员功能）
//  */
// export const deleteUser = async (userId) => {
//   // 对应的后端接口不存在
// }

/**
 * 上传用户头像
 * @param {File} file - 头像文件
 * @returns {Promise} 上传结果
 */
export const uploadAvatar = async (file) => {
  try {
    // 如果后端提供了专门的头像上传接口
    if (userAPI.uploadAvatar) {
      const response = await userAPI.uploadAvatar(file)
      
      // 更新本地存储的用户信息
      const currentUserInfo = localStorage.getItem('userInfo')
      if (currentUserInfo && response.avatarUrl) {
        const userInfo = JSON.parse(currentUserInfo)
        userInfo.avatarUrl = response.avatarUrl
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
      }
      
      return {
        success: true,
        message: '头像上传成功',
        data: response,
        avatarUrl: response.avatarUrl || response.url
      }
    } else {
      // 如果没有专门的头像上传接口，使用通用文件上传
      console.warn('头像上传功能需要后端支持')
      return {
        success: false,
        message: '头像上传功能暂未实现'
      }
    }
  } catch (error) {
    return {
      success: false,
      message: error.message || '头像上传失败',
      error: error
    }
  }
}

// 以下函数对应的后端接口不存在，已注释
// /**
//  * 检查邮箱是否可用
//  */
// export const checkEmailAvailable = async (email) => {
//   // 对应的后端接口不存在
// }

// /**
//  * 检查用户名是否可用
//  */
// export const checkUsernameAvailable = async (username) => {
//   // 对应的后端接口不存在
// }

// /**
//  * 检查昵称是否可用
//  */
// export const checkNicknameAvailable = async (nickname) => {
//   // 对应的后端接口不存在
// }

// 用户服务对象
export const userService = {
  getCurrentUserInfo,
  updateUserInfo,
  changePassword,
  uploadAvatar
}

// 导出默认服务
export default userService

// 兼容旧的API命名
export const getUserInfo = getCurrentUserInfo
export const updateProfile = updateUserInfo