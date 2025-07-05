<template>
  <!-- eslint-disable vue/multi-word-component-names -->
  <div class="user-profile-container">
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
            <el-avatar :size="80" class="avatar-container" @click="showAvatarPreview">
              <img v-if="userAvatarUrl" :src="userAvatarUrl" alt="头像" />
              <el-icon v-else><User /></el-icon>
            </el-avatar>
            <div class="avatar-overlay" @click="showAvatarPreview">
              <el-icon><ZoomIn /></el-icon>
            </div>
          </div>
          
          <div class="user-details">
            <h3>{{ user?.nickname }}</h3>
            <p class="user-username">用户名: {{ user?.username }}</p>
            <p class="user-email">{{ user?.email }}</p>
            <p class="user-id">用户ID: {{ user?.id }}</p>
            <p class="user-status">账户状态: {{ getStatusText(user?.status) }}</p>
            <p class="user-date">注册时间: {{ formatDate(user?.createdTime) }}</p>
            <p class="user-last-login" v-if="user?.lastLoginTime">最后登录: {{ formatDate(user?.lastLoginTime) }}</p>
          </div>
        </div>
        
        <!-- 对话统计信息 -->
        <div class="user-stats">
          <div class="stat-item">
            <span class="stat-label">总对话数</span>
            <span class="stat-value">{{ conversationStats.totalConversations }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总消息数</span>
            <span class="stat-value">{{ conversationStats.totalMessages }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">最近活跃</span>
            <span class="stat-value">{{ formatDate(conversationStats.lastActiveTime) }}</span>
          </div>
        </div>
        
        <div class="user-actions">
          <el-button type="primary" @click="startEdit">编辑资料</el-button>
          <el-button @click="handleLogout">退出登录</el-button>
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
        <UserProfileEdit @save="handleSave" @cancel="cancelEdit" @profile-updated="handleProfileUpdated" />
      </div>
      
      <!-- 历史记录管理 -->
      <el-card v-if="!isEditing" class="history-card">
        <template #header>
          <div class="card-header">
            <span>历史记录管理</span>
            <el-button size="small" type="primary" @click="goToAIAssistant">前往AI助手</el-button>
          </div>
        </template>
        
        <div class="history-content">
          <div v-if="conversations.length === 0" class="empty-state">
            <el-icon class="empty-icon"><ChatDotRound /></el-icon>
            <p>暂无对话记录</p>
            <el-button type="primary" @click="goToAIAssistant">开始对话</el-button>
          </div>
          
          <div v-else class="conversation-list">
            <div 
              v-for="conversation in conversations.slice(0, 5)" 
              :key="conversation.id" 
              class="conversation-item"
              @click="goToConversation(conversation.id)"
            >
              <div class="conversation-info">
                <h4>{{ conversation.title || '未命名对话' }}</h4>
                <p>{{ formatDate(conversation.createdAt) }} · {{ conversation.messages?.length || 0 }} 条消息</p>
              </div>
              <el-button 
                size="small" 
                type="danger" 
                text 
                @click.stop="deleteConversation(conversation.id)"
              >
                删除
              </el-button>
            </div>
            
            <div v-if="conversations.length > 5" class="more-conversations">
              <p>还有 {{ conversations.length - 5 }} 个对话...</p>
              <el-button type="primary" link @click="goToAIAssistant">查看全部</el-button>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 数据管理 -->
      <el-card v-if="!isEditing" class="data-card">
        <template #header>
          <div class="card-header">
            <span>数据管理</span>
          </div>
        </template>
        
        <div class="data-content">
          <div class="data-item">
            <div class="data-info">
              <h4>清空历史数据</h4>
              <p>删除所有对话记录，此操作不可恢复</p>
            </div>
            <el-button 
              size="small" 
              type="danger" 
              @click="clearAllData"
              :disabled="conversations.length === 0"
            >
              清空数据
            </el-button>
          </div>
          
          <div class="data-item">
            <div class="data-info">
              <h4>导出数据</h4>
              <p>导出您的个人数据和对话记录</p>
            </div>
            <el-button size="small" @click="exportData">导出数据</el-button>
          </div>
        </div>
      </el-card>
      
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
        </div>
      </el-card>
    </div>
    
    <!-- 头像预览弹窗 -->
    <el-dialog
      v-model="avatarPreviewVisible"
      title="头像预览"
      width="500px"
      center
      :show-close="true"
    >
      <div class="avatar-preview-content">
        <img v-if="userAvatarUrl" :src="userAvatarUrl" alt="头像预览" class="preview-image" />
        <div v-else class="no-avatar">
          <el-icon class="no-avatar-icon"><User /></el-icon>
          <p>暂无头像</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
           <el-button @click="avatarPreviewVisible = false">关闭</el-button>
           <el-button type="primary" @click="editAvatar">编辑头像</el-button>
         </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  /* eslint-disable vue/multi-word-component-names */
  import { ref, computed, onMounted } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { User, ChatDotRound, ZoomIn, ArrowLeft } from '@element-plus/icons-vue'
  import UserProfileEdit from '@/components/UserProfileEdit.vue'
  import { chatService } from '@/api/chat'
  import { getCurrentUserInfo } from '@/api/services'
  import { buildAvatarUrl } from '@/utils/url'

const router = useRouter()
const store = useStore()
const isEditing = ref(false)
const darkMode = ref(false)
const notifications = ref(true)
const conversations = ref([])
const conversationStats = ref({
  totalConversations: 0,
  totalMessages: 0,
  lastActiveTime: null
})
const avatarPreviewVisible = ref(false)


const user = computed(() => store.state.user)

// 计算完整的头像URL
const userAvatarUrl = computed(() => {
  return user.value?.avatarUrl ? buildAvatarUrl(user.value.avatarUrl) : ''
})

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '正常',
    'INACTIVE': '未激活',
    'SUSPENDED': '已暂停',
    'BANNED': '已封禁'
  }
  return statusMap[status] || '未知'
}

// 加载用户资料
const loadUserProfile = async () => {
  try {
    const response = await getCurrentUserInfo()
    if (response && response.success && response.user) {
      // 更新store中的用户信息
      store.commit('SET_USER', response.user)
    } else if (response && response.data) {
      // 兼容不同的响应格式
      store.commit('SET_USER', response.data)
    } else {
      console.warn('获取用户资料响应格式不正确:', response)
    }
  } catch (error) {
    console.error('获取用户资料失败:', error)
    ElMessage.error('获取用户资料失败')
  }
}

// 加载对话数据和统计信息
const loadConversations = async () => {
  try {
    const response = await chatService.getConversations()
    
    // 确保数据是数组格式
    let data = []
    if (response && response.success) {
      // 优先使用conversations字段
      if (Array.isArray(response.conversations)) {
        data = response.conversations
      } else if (Array.isArray(response.data)) {
        data = response.data
      }
    } else if (Array.isArray(response)) {
      data = response
    } else if (response && Array.isArray(response.data)) {
      data = response.data
    } else {
      console.warn('API返回的对话数据格式不正确:', response)
      data = []
    }
    
    conversations.value = data
    
    // 计算统计信息
    conversationStats.value.totalConversations = data.length
    conversationStats.value.totalMessages = data.reduce((total, conv) => {
      return total + (conv.messages?.length || 0)
    }, 0)
    
    // 找到最近活跃时间
    if (data.length > 0) {
      const latestConv = data.reduce((latest, conv) => {
        return new Date(conv.updatedAt || conv.createdAt) > new Date(latest.updatedAt || latest.createdAt) ? conv : latest
      })
      conversationStats.value.lastActiveTime = latestConv.updatedAt || latestConv.createdAt
    }
  } catch (error) {
    console.error('加载对话数据失败:', error)
    // 确保即使出错也设置为空数组
    conversations.value = []
    conversationStats.value = {
      totalConversations: 0,
      totalMessages: 0,
      lastActiveTime: null
    }
    ElMessage.error('加载对话数据失败')
  }
}

const startEdit = () => {
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
}

const handleSave = () => {
  isEditing.value = false
  ElMessage.success('保存成功')
}

const handleProfileUpdated = async () => {
  await loadUserProfile()
  ElMessage.success('个人资料已更新')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '确认退出',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 清除用户状态
     store.dispatch('logout')
     ElMessage.success('已退出登录')
    
    // 跳转到登录页
    router.push('/login')
  } catch {
    // 用户取消退出
  }
}

const goToAIAssistant = () => {
  router.push('/ai-assistant')
}

const goToConversation = (conversationId) => {
  router.push(`/ai-assistant?conversation=${conversationId}`)
}

const deleteConversation = async (conversationId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个对话吗？此操作不可恢复。',
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await chatService.deleteConversation(conversationId)
    ElMessage.success('对话已删除')
    
    // 重新加载数据
    await loadConversations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除对话失败:', error)
      ElMessage.error('删除对话失败')
    }
  }
}

const clearAllData = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有历史数据吗？此操作将删除所有对话记录，且不可恢复。',
      '确认清空',
      {
        confirmButtonText: '清空',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    await chatService.clearAllConversations()
    ElMessage.success('所有数据已清空')
    
    // 重新加载数据
    await loadConversations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空数据失败:', error)
      ElMessage.error('清空数据失败')
    }
  }
}

const exportData = () => {
  try {
    const exportData = {
      user: user.value,
      conversations: conversations.value,
      stats: conversationStats.value,
      exportTime: new Date().toISOString()
    }
    
    const dataStr = JSON.stringify(exportData, null, 2)
    const dataBlob = new Blob([dataStr], { type: 'application/json' })
    
    const link = document.createElement('a')
    link.href = URL.createObjectURL(dataBlob)
    link.download = `xlhealth_data_${new Date().toISOString().split('T')[0]}.json`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  }
}

const handleThemeChange = (value) => {
  ElMessage.info(`主题已切换为${value ? '深色' : '浅色'}模式`)
}

const handleNotificationChange = (value) => {
  ElMessage.info(`消息通知已${value ? '开启' : '关闭'}`)
}

const showAvatarPreview = () => {
   avatarPreviewVisible.value = true
 }
 
 const editAvatar = () => {
   avatarPreviewVisible.value = false
   startEdit()
 }



onMounted(() => {
  // 加载用户资料和对话数据
  loadUserProfile()
  loadConversations()
})
</script>

<style scoped>
.user-profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
}

.profile-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.profile-header p {
  color: #7f8c8d;
  font-size: 16px;
}

.profile-content {
  display: grid;
  gap: 20px;
}

.user-info-card,
.history-card,
.data-card,
.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #2c3e50;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.user-avatar {
  flex-shrink: 0;
  position: relative;
  display: inline-block;
}

.avatar-container {
  border: 3px solid #409eff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-container:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
  border-radius: 50%;
  color: white;
  font-size: 24px;
}

.user-avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-preview-content {
  text-align: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.no-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
}

.no-avatar-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.dialog-footer {
  text-align: center;
}

.user-details h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 24px;
}

.user-details p {
  margin: 5px 0;
  color: #7f8c8d;
}

.user-email {
  font-size: 16px;
  font-weight: 500;
}

.user-username,
.user-id,
.user-status,
.user-date,
.user-last-login {
  font-size: 14px;
}

.user-status {
  font-weight: 500;
}

/* 对话统计信息样式 */
.user-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin: 20px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 5px;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.user-actions {
  display: flex;
  gap: 10px;
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

/* 历史记录管理样式 */
.history-content {
  min-height: 200px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #bdc3c7;
}

.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.conversation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.conversation-item:hover {
  background-color: #f8f9fa;
  border-color: #409eff;
}

.conversation-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 16px;
}

.conversation-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.more-conversations {
  text-align: center;
  padding: 15px;
  color: #7f8c8d;
  border-top: 1px solid #e1e8ed;
  margin-top: 10px;
}

.more-conversations p {
  margin: 0 0 10px 0;
}

/* 数据管理样式 */
.data-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.data-item:last-child {
  border-bottom: none;
}

.data-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 16px;
}

.data-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 系统设置样式 */
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 16px;
}

.setting-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-container {
    padding: 15px;
  }
  
  .user-info {
    flex-direction: column;
    text-align: center;
  }
  
  .user-stats {
    grid-template-columns: 1fr;
  }
  
  .user-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .conversation-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .data-item,
  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .user-actions {
    flex-direction: column;
  }
  
  .conversation-item {
    padding: 10px;
  }
}
</style>