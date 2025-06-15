<template>
  <div class="profile">
    <div class="page-container">
      <div class="page-header">
        <h1>个人中心</h1>
        <p>管理您的账户信息和使用记录</p>
      </div>
      
      <div class="content-area">
        <div class="profile-grid">
          <!-- 用户信息卡片 -->
          <el-card class="user-info-card">
            <template #header>
              <div class="card-header">
                <span>用户信息</span>
              </div>
            </template>
            <div class="user-info">
              <el-avatar :size="80" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-details">
                <h3>{{ user?.nickname || '未设置昵称' }}</h3>
                <p>{{ user?.email }}</p>
                <p class="user-id">用户ID: {{ user?.id }}</p>
              </div>
            </div>
          </el-card>

          <!-- 功能说明卡片 -->
          <el-card class="features-card">
            <template #header>
              <div class="card-header">
                <span>功能说明</span>
              </div>
            </template>
            <div class="features-content">
              <p>个人中心页面将在 TASK007 中完整实现，包括：</p>
              <ul>
                <li>用户信息管理</li>
                <li>历史对话记录</li>
                <li>账号设置</li>
                <li>数据统计</li>
                <li>隐私设置</li>
              </ul>
            </div>
          </el-card>

          <!-- 快速操作卡片 -->
          <el-card class="actions-card">
            <template #header>
              <div class="card-header">
                <span>快速操作</span>
              </div>
            </template>
            <div class="actions-content">
              <el-button type="primary" @click="goToAI" style="width: 100%; margin-bottom: 12px;">
                <el-icon><ChatDotRound /></el-icon>
                开始AI对话
              </el-button>
              <el-button @click="testLogout" style="width: 100%;">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  ChatDotRound,
  SwitchButton
} from '@element-plus/icons-vue'

export default {
  name: 'UserProfile',
  components: {
    User,
    ChatDotRound,
    SwitchButton
  },
  setup() {
    const store = useStore()
    const router = useRouter()

    // 计算属性
    const user = computed(() => store.state.user)

    // 跳转到AI助手
    const goToAI = () => {
      router.push('/ai-assistant')
    }

    // 测试退出登录
    const testLogout = () => {
      store.dispatch('clearUser')
      ElMessage.success('已退出登录')
      router.push('/')
    }

    return {
      user,
      goToAI,
      testLogout
    }
  },
  mounted() {
    console.log('个人中心页面已加载 - 将在TASK007中完整实现')
  }
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 600;
}

.page-header p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.content-area {
  max-width: 1000px;
  margin: 0 auto;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

/* 用户信息卡片 */
.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-details h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 1.3rem;
}

.user-details p {
  margin: 4px 0;
  color: #606266;
}

.user-id {
  font-size: 0.9rem;
  color: #909399;
}

/* 功能说明卡片 */
.features-content p {
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.6;
}

.features-content ul {
  color: #606266;
  padding-left: 20px;
  line-height: 1.8;
}

.features-content li {
  margin-bottom: 8px;
}

/* 快速操作卡片 */
.actions-content {
  display: flex;
  flex-direction: column;
}

@media (max-width: 768px) {
  .profile {
    padding: 20px 16px;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .page-header p {
    font-size: 1rem;
  }
  
  .profile-grid {
    grid-template-columns: 1fr;
  }
  
  .user-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>