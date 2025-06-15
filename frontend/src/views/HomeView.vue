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
      
      <div class="feature-list">
        <div class="feature-item">
          <el-icon class="feature-icon"><Setting /></el-icon>
          <h3>项目架构</h3>
          <p>Vue 3 + Vue Router + Vuex + Element Plus</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><SuccessFilled /></el-icon>
          <h3>开发状态</h3>
          <p>TASK002 - 全局布局组件开发完成</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><ArrowRight /></el-icon>
          <h3>下一步</h3>
          <p>TASK003 - 用户认证系统开发</p>
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
  Lock
} from '@element-plus/icons-vue'

export default {
  name: 'HomeView',
  components: {
    ChatDotRound,
    Setting,
    SuccessFilled,
    ArrowRight,
    Document,
    Lock
  },
  setup() {
    const store = useStore()
    const router = useRouter()

    // 计算属性
    const isLoggedIn = computed(() => store.state.isLoggedIn)
    const user = computed(() => store.state.user)

    // 处理开始对话按钮点击
    const handleStartChat = () => {
      if (isLoggedIn.value) {
        // 已登录，跳转到AI助手页面
        router.push('/ai-assistant')
      } else {
        // 未登录，提示登录（在TASK003中会实现登录弹窗）
        ElMessage.info('请先登录以使用AI心理助手功能')
        // 暂时模拟登录
        const mockUser = {
          id: 1,
          email: 'demo@example.com',
          nickname: '演示用户'
        }
        store.dispatch('setUser', mockUser)
        ElMessage.success('演示登录成功！')
        setTimeout(() => {
          router.push('/ai-assistant')
        }, 1000)
      }
    }

    return {
      isLoggedIn,
      user,
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
