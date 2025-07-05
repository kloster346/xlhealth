<template>
  <el-header class="app-header">
    <div class="header-container">
      <!-- Logo 和网站名称 -->
      <div class="logo-section">
        <el-icon class="logo-icon" :size="32">
          <ChatDotRound />
        </el-icon>
        <span class="site-name">{{ appName }}</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeIndex"
        class="header-menu"
        mode="horizontal"
        @select="handleSelect"
        :ellipsis="false"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/ai-assistant" v-if="isLoggedIn">AI心理助手</el-menu-item>
        <el-menu-item index="/profile" v-if="isLoggedIn">我的</el-menu-item>
      </el-menu>

      <!-- 用户状态区域 -->
      <div class="user-section">
        <!-- 未登录状态 -->
        <div v-if="!isLoggedIn" class="auth-buttons">
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button @click="handleRegister">注册</el-button>
        </div>

        <!-- 已登录状态 -->
        <el-dropdown v-else @command="handleCommand" class="user-dropdown">
          <span class="user-info">
            <el-avatar :size="32" class="user-avatar">
              <img v-if="userAvatarUrl" :src="userAvatarUrl" alt="头像" />
              <el-icon v-else><User /></el-icon>
            </el-avatar>
            <span class="username">{{ user?.nickname || user?.email }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logoutUser } from '../api/services'
import { buildAvatarUrl } from '../utils/url'
import {
  ChatDotRound,
  User,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

export default {
  name: 'AppHeader',
  components: {
    ChatDotRound,
    User,
    ArrowDown,
    SwitchButton
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()

    // 计算属性
    const appName = computed(() => store.state.appName)
    const isLoggedIn = computed(() => store.state.isLoggedIn)
    const user = computed(() => store.state.user)
    const activeIndex = computed(() => route.path)
    
    // 计算完整的头像URL
    const userAvatarUrl = computed(() => {
      return user.value?.avatarUrl ? buildAvatarUrl(user.value.avatarUrl) : ''
    })

    // 处理菜单选择
    const handleSelect = (index) => {
      if (index !== route.path) {
        router.push(index)
      }
    }

    // 处理登录按钮点击
    const handleLogin = () => {
      router.push('/login')
    }

    // 处理注册按钮点击
    const handleRegister = () => {
      router.push('/register')
    }

    // 处理下拉菜单命令
    const handleCommand = async (command) => {
      switch (command) {
        case 'profile':
          router.push('/profile')
          break
        case 'logout':
          try {
            // 调用退出登录API
            await logoutUser()
            
            // 清除本地状态
            await store.dispatch('logout')
            
            ElMessage.success('已退出登录')
            
            // 如果当前在需要登录的页面，跳转到首页
            if (route.meta?.requiresAuth) {
              router.push('/')
            }
          } catch (error) {
            ElMessage.error('退出登录失败，请重试')
          }
          break
      }
    }

    return {
      appName,
      isLoggedIn,
      user,
      userAvatarUrl,
      activeIndex,
      handleSelect,
      handleLogin,
      handleRegister,
      handleCommand
    }
  }
}
</script>

<style scoped>
.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 0;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.logo-icon {
  color: #409eff;
}

.site-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
}

.header-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
  justify-content: center;
}

.header-menu .el-menu-item {
  border-bottom: 2px solid transparent;
  height: 60px;
  line-height: 60px;
}

.header-menu .el-menu-item:hover,
.header-menu .el-menu-item.is-active {
  border-bottom-color: #409eff;
  background-color: transparent;
}

.user-section {
  flex-shrink: 0;
}

.auth-buttons {
  display: flex;
  gap: 12px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }
  
  .header-menu {
    margin: 0 20px;
  }
  
  .site-name {
    font-size: 18px;
  }
  
  .auth-buttons {
    gap: 8px;
  }
  
  .auth-buttons .el-button {
    padding: 8px 12px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .header-menu {
    display: none;
  }
  
  .site-name {
    font-size: 16px;
  }
}
</style>