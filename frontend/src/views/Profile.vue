<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息和偏好设置</p>
    </div>
    
    <div class="profile-content">
      <!-- 用户信息展示 -->
      <el-card v-if="!isEditing" class="user-info-card">
        <template #header>
          <div class="card-header">
            <span>用户信息</span>
          </div>
        </template>
        
        <div class="user-info" v-if="user">
          <div class="user-avatar">
            <el-avatar :size="80">
              <img v-if="user.avatar" :src="user.avatar" alt="头像" />
              <el-icon v-else><User /></el-icon>
            </el-avatar>
          </div>
          
          <div class="user-details">
            <h3>{{ user?.nickname }}</h3>
            <p class="user-email">{{ user?.email }}</p>
            <p class="user-id">用户ID: {{ user?.id }}</p>
            <p class="user-date">注册时间: {{ formatDate(user?.createdAt) }}</p>
          </div>
        </div>
        
        <div class="user-actions">
          <el-button type="primary" @click="startEdit">编辑资料</el-button>
          <el-button @click="testLogout">测试退出登录</el-button>
        </div>
      </el-card>
      
      <!-- 用户资料编辑 -->
      <div v-else class="edit-section">
        <div class="edit-header">
          <el-button 
            type="text" 
            @click="cancelEdit"
            class="back-button"
          >
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
        <UserProfileEdit @save="handleSave" @cancel="cancelEdit" />
      </div>
      
      <!-- 系统设置 -->
      <el-card v-if="!isEditing" class="settings-card">
        <template #header>
          <div class="card-header">
            <span>系统设置</span>
          </div>
        </template>
        
        <div class="settings-content">
          <div class="setting-item">
            <div class="setting-info">
              <h4>主题设置</h4>
              <p>选择您喜欢的界面主题</p>
            </div>
            <el-switch
              v-model="darkMode"
              active-text="深色"
              inactive-text="浅色"
              @change="handleThemeChange"
            />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h4>消息通知</h4>
              <p>接收系统消息和提醒</p>
            </div>
            <el-switch
              v-model="notifications"
              @change="handleNotificationChange"
            />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h4>数据导出</h4>
              <p>导出您的个人数据</p>
            </div>
            <el-button size="small" @click="exportData">导出数据</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User, ArrowLeft } from '@element-plus/icons-vue'
import UserProfileEdit from '../components/UserProfileEdit.vue'

export default {
  name: 'ProfilePage',
  components: {
    User,
    ArrowLeft,
    UserProfileEdit
  },
  setup() {
    const store = useStore()
    const isEditing = ref(false)
    const darkMode = ref(false)
    const notifications = ref(true)
    
    // 计算属性
    const user = computed(() => store.state.user)
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }
    
    // 开始编辑
    const startEdit = () => {
      isEditing.value = true
    }
    
    // 取消编辑
    const cancelEdit = () => {
      isEditing.value = false
    }
    
    // 处理保存
    const handleSave = () => {
      isEditing.value = false
      ElMessage.success('资料更新成功')
    }
    
    // 测试退出登录功能
    const testLogout = () => {
      store.dispatch('clearUser')
      ElMessage.success('已退出登录')
    }
    
    // 主题切换
    const handleThemeChange = (value) => {
      ElMessage.info(`主题已切换为${value ? '深色' : '浅色'}模式`)
    }
    
    // 通知设置
    const handleNotificationChange = (value) => {
      ElMessage.info(`消息通知已${value ? '开启' : '关闭'}`)
    }
    
    // 导出数据
    const exportData = () => {
      ElMessage.info('数据导出功能将在后续版本中实现')
    }
    
    return {
      user,
      isEditing,
      darkMode,
      notifications,
      formatDate,
      startEdit,
      cancelEdit,
      handleSave,
      testLogout,
      handleThemeChange,
      handleNotificationChange,
      exportData
    }
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
  font-size: 20px;
  font-weight: 600;
}

.user-email {
  margin: 0 0 4px 0;
  color: #606266;
  font-size: 14px;
}

.user-id,
.user-date {
  margin: 0 0 4px 0;
  color: #909399;
  font-size: 12px;
}

.user-date {
  margin: 0;
}

.edit-section {
  margin-bottom: 20px;
}

.edit-header {
  margin-bottom: 20px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  color: #409eff;
}

.back-button:hover {
  color: #66b1ff;
}

.settings-content {
  padding: 0;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info h4 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.setting-info p {
  margin: 0;
  color: #909399;
  font-size: 12px;
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