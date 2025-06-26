// 模拟API延迟
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

// 模拟用户数据库
let users = [
  {
    id: 1,
    email: 'admin@example.com',
    password: 'Admin123',
    nickname: '管理员',
    avatar: '',
    createdAt: '2024-01-01T00:00:00.000Z'
  },
  {
    id: 2,
    email: 'test@example.com',
    password: 'Test123',
    nickname: '测试用户',
    avatar: '',
    createdAt: '2024-01-01T00:00:00.000Z'
  }
]

// 获取下一个用户ID
const getNextUserId = () => {
  return Math.max(...users.map(u => u.id), 0) + 1
}

// 用户登录
export const loginUser = async (credentials) => {
  await delay(1000) // 模拟网络延迟
  
  const { email, password } = credentials
  
  // 查找用户
  const user = users.find(u => u.email === email)
  
  if (!user) {
    throw new Error('用户不存在')
  }
  
  if (user.password !== password) {
    throw new Error('密码错误')
  }
  
  // 返回用户信息（不包含密码）
  const { password: _password, ...userInfo } = user
  
  return {
    success: true,
    message: '登录成功',
    user: userInfo,
    token: `mock_token_${user.id}_${Date.now()}`
  }
}

// 用户注册
export const registerUser = async (userData) => {
  await delay(1000) // 模拟网络延迟
  
  const { email, password, nickname } = userData
  
  // 检查邮箱是否已存在
  const existingUser = users.find(u => u.email === email)
  if (existingUser) {
    throw new Error('该邮箱已被注册')
  }
  
  // 检查昵称是否已存在
  const existingNickname = users.find(u => u.nickname === nickname)
  if (existingNickname) {
    throw new Error('该昵称已被使用')
  }
  
  // 创建新用户
  const newUser = {
    id: getNextUserId(),
    email,
    password,
    nickname,
    avatar: '',
    createdAt: new Date().toISOString()
  }
  
  users.push(newUser)
  
  // 返回用户信息（不包含密码）
  const { password: _password, ...userInfo } = newUser
  
  return {
    success: true,
    message: '注册成功',
    user: userInfo
  }
}

// 获取用户信息
export const getUserInfo = async (userId) => {
  await delay(500) // 模拟网络延迟
  
  const user = users.find(u => u.id === userId)
  
  if (!user) {
    throw new Error('用户不存在')
  }
  
  // 返回用户信息（不包含密码）
  const { password: _password, ...userInfo } = user
  
  return {
    success: true,
    user: userInfo
  }
}

// 更新用户信息
export const updateUserInfo = async (userId, updateData) => {
  await delay(800) // 模拟网络延迟
  
  const userIndex = users.findIndex(u => u.id === userId)
  
  if (userIndex === -1) {
    throw new Error('用户不存在')
  }
  
  // 如果更新邮箱，检查是否已存在
  if (updateData.email && updateData.email !== users[userIndex].email) {
    const existingUser = users.find(u => u.email === updateData.email)
    if (existingUser) {
      throw new Error('该邮箱已被使用')
    }
  }
  
  // 如果更新昵称，检查是否已存在
  if (updateData.nickname && updateData.nickname !== users[userIndex].nickname) {
    const existingNickname = users.find(u => u.nickname === updateData.nickname)
    if (existingNickname) {
      throw new Error('该昵称已被使用')
    }
  }
  
  // 更新用户信息
  users[userIndex] = {
    ...users[userIndex],
    ...updateData,
    updatedAt: new Date().toISOString()
  }
  
  // 返回更新后的用户信息（不包含密码）
  const { password: _password, ...userInfo } = users[userIndex]
  
  return {
    success: true,
    message: '更新成功',
    user: userInfo
  }
}

// 修改密码
export const changePassword = async (userId, passwordData) => {
  await delay(800) // 模拟网络延迟
  
  const { oldPassword, newPassword } = passwordData
  const userIndex = users.findIndex(u => u.id === userId)
  
  if (userIndex === -1) {
    throw new Error('用户不存在')
  }
  
  if (users[userIndex].password !== oldPassword) {
    throw new Error('原密码错误')
  }
  
  // 更新密码
  users[userIndex].password = newPassword
  users[userIndex].updatedAt = new Date().toISOString()
  
  return {
    success: true,
    message: '密码修改成功'
  }
}

// 检查邮箱是否可用
export const checkEmailAvailable = async (email) => {
  await delay(300) // 模拟网络延迟
  
  const existingUser = users.find(u => u.email === email)
  
  return {
    success: true,
    available: !existingUser
  }
}

// 检查昵称是否可用
export const checkNicknameAvailable = async (nickname) => {
  await delay(300) // 模拟网络延迟
  
  const existingUser = users.find(u => u.nickname === nickname)
  
  return {
    success: true,
    available: !existingUser
  }
}

// 退出登录（清除服务端session等）
export const logoutUser = async () => {
  await delay(300) // 模拟网络延迟
  
  return {
    success: true,
    message: '退出登录成功'
  }
}

// 验证token有效性
export const validateToken = async (token) => {
  await delay(300) // 模拟网络延迟
  
  // 简单的token验证逻辑
  if (!token || !token.startsWith('mock_token_')) {
    throw new Error('无效的token')
  }
  
  const parts = token.split('_')
  if (parts.length !== 4) {
    throw new Error('无效的token格式')
  }
  
  const userId = parseInt(parts[2])
  const user = users.find(u => u.id === userId)
  
  if (!user) {
    throw new Error('用户不存在')
  }
  
  // 返回用户信息（不包含密码）
  const { password: _, ...userInfo } = user
  
  return {
    success: true,
    user: userInfo
  }
}