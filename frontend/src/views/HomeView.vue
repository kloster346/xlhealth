<template>
  <div class="home">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：登录/注册表单 -->
      <div class="auth-section">
        <div class="auth-container">
          <h2 class="auth-title">{{ isLoginMode ? '登录' : '注册' }}</h2>
          
          <!-- 登录表单 -->
          <form v-if="isLoginMode" @submit.prevent="handleLogin" class="auth-form">
            <div class="form-group">
              <label for="loginUsername">用户名</label>
              <input 
                id="loginUsername"
                v-model="loginForm.username" 
                type="text" 
                placeholder="请输入用户名"
                required
              >
            </div>
            <div class="form-group">
              <label for="loginPassword">密码</label>
              <input 
                id="loginPassword"
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
                required
              >
            </div>
            <button type="submit" class="auth-btn" :disabled="isLoading">{{ isLoading ? '登录中...' : '登录' }}</button>
          </form>
          
          <!-- 注册表单 -->
          <form v-else @submit.prevent="handleRegister" class="auth-form">
            <div class="form-group">
              <label for="registerUsername">用户名</label>
              <input 
                id="registerUsername"
                v-model="registerForm.username" 
                type="text" 
                placeholder="请输入用户名"
                required
              >
            </div>
            <div class="form-group">
              <label for="registerEmail">邮箱</label>
              <input 
                id="registerEmail"
                v-model="registerForm.email" 
                type="email" 
                placeholder="请输入邮箱"
                required
              >
            </div>
            <div class="form-group">
              <label for="registerPassword">密码</label>
              <input 
                id="registerPassword"
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码"
                required
              >
            </div>
            <div class="form-group">
              <label for="confirmPassword">确认密码</label>
              <input 
                id="confirmPassword"
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                required
              >
            </div>
            <button type="submit" class="auth-btn" :disabled="isLoading">{{ isLoading ? '注册中...' : '注册' }}</button>
          </form>
          
          <!-- 切换登录/注册模式 -->
          <div class="auth-switch">
            <span v-if="isLoginMode">还没有账号？</span>
            <span v-else>已有账号？</span>
            <button @click="toggleAuthMode" class="switch-btn">
              {{ isLoginMode ? '立即注册' : '立即登录' }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- 右侧：消息列表和开始对话 -->
      <div class="content-section">
        <!-- 欢迎区域 -->
        <div class="welcome-header">
          <h1 class="title">AI心理助手</h1>
          <p class="subtitle">随时倾听您的感受</p>
          <button class="start-chat-btn" @click="startChat">开始对话</button>
        </div>
        
        <!-- 消息列表 -->
        <div class="messages-section">
          <h3 class="section-title">最新消息</h3>
          <div class="messages-list">
            <div 
              v-for="message in displayMessages" 
              :key="message.id"
              class="message-item"
              :class="{ 'unread': !message.isRead }"
              @click="markAsRead(message.id)"
            >
              <div class="message-header">
                <span class="message-type-icon">{{ getMessageTypeInfo(message.type).icon }}</span>
                <h4 class="message-title">{{ message.title }}</h4>
                <span class="message-date">{{ message.date }}</span>
              </div>
              <p class="message-content">{{ message.content }}</p>
              <div class="message-type" :style="{ color: getMessageTypeInfo(message.type).color }">
                {{ getMessageTypeInfo(message.type).name }}
              </div>
            </div>
          </div>
          
          <!-- 查看更多按钮 -->
          <div v-if="mockMessages.length > displayLimit" class="load-more">
            <button @click="loadMoreMessages" class="load-more-btn">
              查看更多消息 ({{ mockMessages.length - displayLimit }})
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mockMessages, mockUsers, getMessageTypeInfo, simulateApiDelay, generateMockId } from '@/utils/mockData'

export default {
  name: 'HomeView',
  data() {
    return {
      // 认证相关
      isLoginMode: true,
      isLoading: false,
      
      // 登录表单
      loginForm: {
        username: '',
        password: ''
      },
      
      // 注册表单
      registerForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      
      // 消息列表相关
      mockMessages: [...mockMessages],
      displayLimit: 3
    }
  },
  
  computed: {
    // 显示的消息列表（限制数量）
    displayMessages() {
      return this.mockMessages.slice(0, this.displayLimit)
    },
    
    // 检查是否已登录
    isAuthenticated() {
      return this.$store.getters.isAuthenticated
    }
  },
  
  methods: {
    // 获取消息类型信息
    getMessageTypeInfo,
    
    // 切换登录/注册模式
    toggleAuthMode() {
      this.isLoginMode = !this.isLoginMode
      this.clearForms()
    },
    
    // 清空表单
    clearForms() {
      this.loginForm = { username: '', password: '' }
      this.registerForm = { username: '', email: '', password: '', confirmPassword: '' }
    },
    
    // 处理登录
    async handleLogin() {
      if (this.isLoading) return
      
      this.isLoading = true
      
      try {
        // 模拟API调用延迟
        await simulateApiDelay(800)
        
        // 验证用户凭据（模拟）
        const user = mockUsers.find(u => 
          u.username === this.loginForm.username && 
          u.password === this.loginForm.password
        )
        
        if (user) {
          // 生成模拟token
          const mockToken = `mock_token_${generateMockId()}`
          
          // 保存到Vuex store
          this.$store.dispatch('login', {
            user: {
              id: user.id,
              username: user.username,
              email: user.email,
              nickname: user.nickname
            },
            token: mockToken
          })
          
          this.$message?.success?.('登录成功！') || alert('登录成功！')
          this.clearForms()
        } else {
          this.$message?.error?.('用户名或密码错误') || alert('用户名或密码错误')
        }
      } catch (error) {
        console.error('登录失败:', error)
        this.$message?.error?.('登录失败，请重试') || alert('登录失败，请重试')
      } finally {
        this.isLoading = false
      }
    },
    
    // 处理注册
    async handleRegister() {
      if (this.isLoading) return
      
      // 验证密码确认
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.$message?.error?.('两次输入的密码不一致') || alert('两次输入的密码不一致')
        return
      }
      
      // 检查用户名是否已存在
      const existingUser = mockUsers.find(u => u.username === this.registerForm.username)
      if (existingUser) {
        this.$message?.error?.('用户名已存在') || alert('用户名已存在')
        return
      }
      
      this.isLoading = true
      
      try {
        // 模拟API调用延迟
        await simulateApiDelay(1000)
        
        // 创建新用户（模拟）
        const newUser = {
          id: Date.now(),
          username: this.registerForm.username,
          email: this.registerForm.email,
          password: this.registerForm.password,
          nickname: this.registerForm.username,
          avatar: '',
          createdAt: new Date().toISOString().split('T')[0]
        }
        
        // 添加到模拟用户列表
        mockUsers.push(newUser)
        
        // 生成模拟token
        const mockToken = `mock_token_${generateMockId()}`
        
        // 自动登录
        this.$store.dispatch('login', {
          user: {
            id: newUser.id,
            username: newUser.username,
            email: newUser.email,
            nickname: newUser.nickname
          },
          token: mockToken
        })
        
        this.$message?.success?.('注册成功！已自动登录') || alert('注册成功！已自动登录')
        this.clearForms()
        this.isLoginMode = true
      } catch (error) {
        console.error('注册失败:', error)
        this.$message?.error?.('注册失败，请重试') || alert('注册失败，请重试')
      } finally {
        this.isLoading = false
      }
    },
    
    // 开始对话
    startChat() {
      // 检查登录状态
      if (!this.isAuthenticated) {
        this.$message?.warning?.('请先登录后再开始对话') || alert('请先登录后再开始对话')
        return
      }
      // 跳转到AI助手页面
      this.$router.push('/chat')
    },
    
    // 标记消息为已读
    markAsRead(messageId) {
      const message = this.mockMessages.find(m => m.id === messageId)
      if (message) {
        message.isRead = true
      }
    },
    
    // 加载更多消息
    loadMoreMessages() {
      this.displayLimit = Math.min(this.displayLimit + 3, this.mockMessages.length)
    }
  },
  
  // 组件挂载时初始化
  mounted() {
    // 初始化用户状态
    this.$store.dispatch('initializeAuth')
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 40px;
  min-height: calc(100vh - 40px);
}

/* 左侧认证区域 */
.auth-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  padding: 0;
  height: fit-content;
  position: sticky;
  top: 20px;
}

.auth-container {
  padding: 32px;
}

.auth-title {
  text-align: center;
  margin-bottom: 32px;
  font-size: 1.8rem;
  color: #333;
  font-weight: 600;
}

.auth-form {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.auth-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.auth-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-switch {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
  color: #666;
}

.switch-btn {
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-weight: 600;
  margin-left: 8px;
  text-decoration: underline;
}

.switch-btn:hover {
  color: #764ba2;
}

/* 右侧内容区域 */
.content-section {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.welcome-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 48px 40px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.2);
}

.title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.subtitle {
  font-size: 1.2rem;
  margin-bottom: 32px;
  opacity: 0.9;
}

.start-chat-btn {
  background: white;
  color: #667eea;
  border: none;
  padding: 14px 32px;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.start-chat-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.2);
}

/* 消息列表区域 */
.messages-section {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.section-title {
  margin-bottom: 24px;
  font-size: 1.5rem;
  color: #333;
  font-weight: 600;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  padding: 20px;
  border: 2px solid #f0f2f5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.message-item:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
}

.message-item.unread {
  border-color: #667eea;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.message-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.message-type-icon {
  font-size: 1.2rem;
}

.message-title {
  flex: 1;
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.message-date {
  color: #999;
  font-size: 0.9rem;
}

.message-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.message-type {
  font-size: 0.85rem;
  font-weight: 500;
  text-align: right;
}

.load-more {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.load-more-btn {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  color: #667eea;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .auth-section {
    position: static;
    order: 2;
  }
  
  .content-section {
    order: 1;
  }
}

@media (max-width: 768px) {
  .home {
    padding: 12px;
  }
  
  .auth-container {
    padding: 24px;
  }
  
  .welcome-header {
    padding: 32px 24px;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .messages-section {
    padding: 24px;
  }
  
  .message-header {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .message-date {
    order: 3;
    width: 100%;
  }
}

@media (max-width: 480px) {
  .auth-container {
    padding: 20px;
  }
  
  .welcome-header {
    padding: 24px 20px;
  }
  
  .messages-section {
    padding: 20px;
  }
  
  .title {
    font-size: 1.8rem;
  }
}
</style>
