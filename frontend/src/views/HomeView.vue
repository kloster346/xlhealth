<template>
  <div class="home">
    <div class="welcome-section">
      <h1>欢迎使用 AI 心理咨询系统</h1>
      <p>基于 Vue 3 + Element Plus 的现代化心理健康平台</p>
      
      <!-- 开始对话按钮 -->
      <div class="action-section">
        <el-button 
          type="primary" 
          size="large" 
          @click="handleStartChat"
          class="start-chat-btn"
        >
          <el-icon><ChatDotRound /></el-icon>
          开始对话
        </el-button>
        <p class="action-hint" v-if="!isLoggedIn">
          点击开始对话将引导您登录
        </p>
        <p class="action-hint" v-else>
          欢迎回来，{{ user?.nickname || user?.email }}！
        </p>
      </div>
      
      <!-- 动态消息展示区 -->
      <div class="news-section">
        <h3>最新动态</h3>
        <div class="news-list">
          <div 
            v-for="news in newsList" 
            :key="news.id" 
            class="news-item"
            :class="{ 'unread': !news.isRead }"
          >
            <div class="news-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="news-content">
              <h4>{{ news.title }}</h4>
              <p>{{ news.content }}</p>
              <span class="news-time">{{ formatTime(news.timestamp) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="feature-list">
        <div class="feature-item">
          <el-icon class="feature-icon"><Setting /></el-icon>
          <h3>项目架构</h3>
          <p>Vue 3 + Vue Router + Vuex + Element Plus</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><SuccessFilled /></el-icon>
          <h3>开发状态</h3>
          <p>TASK004 - 首页开发完成</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><ArrowRight /></el-icon>
          <h3>下一步</h3>
          <p>TASK005 - AI 心理助手页面开发</p>
        </div>
      </div>
      
      <!-- 服务简介 -->
      <div class="service-intro">
        <h2>我们的服务</h2>
        <div class="service-grid">
          <div class="service-item">
            <el-icon class="service-icon"><ChatDotRound /></el-icon>
            <h4>AI 智能对话</h4>
            <p>24小时在线的AI心理咨询师，随时为您提供专业建议</p>
          </div>
          <div class="service-item">
            <el-icon class="service-icon"><Document /></el-icon>
            <h4>历史记录</h4>
            <p>完整保存您的对话历史，持续跟踪心理健康状态</p>
          </div>
          <div class="service-item">
            <el-icon class="service-icon"><Lock /></el-icon>
            <h4>隐私保护</h4>
            <p>严格保护用户隐私，所有对话内容安全加密存储</p>
          </div>
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
  ChatDotRound,
  Setting,
  SuccessFilled,
  ArrowRight,
  Document,
  Lock,
  Bell
} from '@element-plus/icons-vue'

export default {
  name: 'HomeView',
  components: {
    ChatDotRound,
    Setting,
    SuccessFilled,
    ArrowRight,
    Document,
    Lock,
    Bell
  },
  setup() {
    const store = useStore()
    const router = useRouter()

    // 计算属性
    const isLoggedIn = computed(() => store.state.isLoggedIn)
    const user = computed(() => store.state.user)

    // 动态消息数据（模拟数据）
    const newsList = [
      {
        id: 1,
        title: '系统更新',
        content: 'TASK004 首页开发完成，新增动态消息展示功能',
        timestamp: new Date().getTime() - 1000 * 60 * 30, // 30分钟前
        isRead: false
      },
      {
        id: 2,
        title: '功能上线',
        content: '用户认证系统已完成，支持登录注册和权限控制',
        timestamp: new Date().getTime() - 1000 * 60 * 60 * 2, // 2小时前
        isRead: true
      },
      {
        id: 3,
        title: '开发进展',
        content: '全局布局组件开发完成，导航栏和页脚功能正常',
        timestamp: new Date().getTime() - 1000 * 60 * 60 * 24, // 1天前
        isRead: true
      }
    ]

    // 时间格式化函数
    const formatTime = (timestamp) => {
      const now = new Date().getTime()
      const diff = now - timestamp
      const minute = 1000 * 60
      const hour = minute * 60
      const day = hour * 24

      if (diff < hour) {
        return Math.floor(diff / minute) + '分钟前'
      } else if (diff < day) {
        return Math.floor(diff / hour) + '小时前'
      } else {
        return Math.floor(diff / day) + '天前'
      }
    }

    // 处理开始对话按钮点击
    const handleStartChat = () => {
      if (isLoggedIn.value) {
        // 已登录，跳转到AI助手页面
        router.push('/ai-assistant')
      } else {
        // 未登录，跳转到登录页面
        ElMessage.info('请先登录以使用AI心理助手功能')
        router.push('/login')
      }
    }

    return {
      isLoggedIn,
      user,
      newsList,
      formatTime,
      handleStartChat
    }
  },
  
  mounted() {
    console.log('Vue 3 项目启动成功')
    console.log('Element Plus 组件库已配置')
    console.log('Vue Router 路由系统已配置')
    console.log('Vuex 状态管理已配置')
    console.log('TASK002 - 全局布局组件开发完成')
  }
}
</script>

<style scoped>
.home {
  padding: 40px 20px 20px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 40px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 50px;
  padding-top: 20px;
}

.welcome-section h1 {
  color: #409EFF;
  margin-bottom: 16px;
  font-size: 2.5em;
  font-weight: 600;
}

.welcome-section > p {
  color: #666;
  font-size: 1.2em;
  margin-bottom: 30px;
  line-height: 1.6;
}

/* 开始对话按钮区域 */
.action-section {
  margin: 40px 0;
}

.start-chat-btn {
  font-size: 18px;
  padding: 15px 30px;
  border-radius: 25px;
  margin-bottom: 15px;
}

.action-hint {
  color: #909399;
  font-size: 14px;
  margin: 10px 0;
}

/* 动态消息展示区 */
.news-section {
  margin: 3rem 0;
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.news-section h3 {
  color: #303133;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  border-bottom: 2px solid #409eff;
  padding-bottom: 0.5rem;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.news-item {
  display: flex;
  align-items: flex-start;
  padding: 1rem;
  border-radius: 8px;
  background: #f8f9fa;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.news-item.unread {
  background: #e8f4fd;
  border-left-color: #409eff;
}

.news-item:hover {
  background: #e8f4fd;
  transform: translateX(5px);
}

.news-icon {
  margin-right: 1rem;
  color: #409eff;
  font-size: 1.2rem;
  margin-top: 0.2rem;
}

.news-content {
  flex: 1;
}

.news-content h4 {
  color: #303133;
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.news-content p {
  color: #606266;
  margin: 0 0 0.5rem 0;
  line-height: 1.5;
}

.news-time {
  color: #909399;
  font-size: 0.875rem;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin: 40px 0;
}

.feature-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: transform 0.2s;
  text-align: center;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.feature-icon {
  font-size: 24px;
  color: #409EFF;
  margin-bottom: 10px;
}

.feature-item h3 {
  color: #409EFF;
  margin-bottom: 10px;
}

.feature-item p {
  color: #666;
  margin: 0;
}

/* 服务简介区域 */
.service-intro {
  margin-top: 50px;
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid #e4e7ed;
}

.service-intro h2 {
  color: #303133;
  margin-bottom: 20px;
  font-size: 1.8em;
  font-weight: 600;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.service-item {
  background: white;
  padding: 30px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s;
}

.service-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.service-icon {
  font-size: 36px;
  color: #409EFF;
  margin-bottom: 15px;
}

.service-item h4 {
  color: #303133;
  margin-bottom: 15px;
  font-size: 1.2em;
}

.service-item p {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-section h1 {
    font-size: 2em;
  }
  
  .start-chat-btn {
    font-size: 16px;
    padding: 12px 24px;
  }
  
  .service-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
</style>
