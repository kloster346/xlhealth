<template>
  <el-footer class="app-footer">
    <div class="footer-container">
      <div class="footer-content">
        <!-- 网站信息 -->
        <div class="footer-section">
          <h4>{{ appName }}</h4>
          <p>专业的AI心理咨询平台</p>
          <p>为您提供24小时心理健康支持</p>
        </div>

        <!-- 快速链接 -->
        <div class="footer-section">
          <h4>快速链接</h4>
          <ul class="footer-links">
            <li><a href="#" @click.prevent="goToHome">首页</a></li>
            <li><a href="#" @click.prevent="goToAI" v-if="isLoggedIn">AI 心理助手</a></li>
            <li><a href="#" @click.prevent="goToProfile" v-if="isLoggedIn">个人中心</a></li>
          </ul>
        </div>

        <!-- 联系信息 -->
        <div class="footer-section">
          <h4>联系我们</h4>
          <p>心理援助热线：400-161-9995</p>
          <p>邮箱：support@xlhealth.com</p>
          <p>工作时间：24小时全天候服务</p>
        </div>

        <!-- 技术信息 -->
        <div class="footer-section">
          <h4>技术支持</h4>
          <p>基于 Vue 3 + Element Plus</p>
          <p>版本：{{ version }}</p>
          <p>© 2024 AI心理咨询系统</p>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="copyright">
          <p>© 2024 AI心理咨询系统. 保留所有权利.</p>
          <p class="disclaimer">本平台仅提供心理健康咨询建议，不能替代专业医疗诊断</p>
        </div>
        <div class="tech-info">
          <span>Powered by Vue 3 & Element Plus</span>
        </div>
      </div>
    </div>
  </el-footer>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'AppFooter',
  setup() {
    const store = useStore()
    const router = useRouter()

    // 计算属性
    const appName = computed(() => store.state.appName)
    const version = computed(() => store.state.version)
    const isLoggedIn = computed(() => store.state.isLoggedIn)

    // 导航方法
    const goToHome = () => {
      router.push('/')
    }

    const goToAI = () => {
      if (isLoggedIn.value) {
        router.push('/ai-assistant')
      } else {
        ElMessage.warning('请先登录')
      }
    }

    const goToProfile = () => {
      if (isLoggedIn.value) {
        router.push('/profile')
      } else {
        ElMessage.warning('请先登录')
      }
    }

    return {
      appName,
      version,
      isLoggedIn,
      goToHome,
      goToAI,
      goToProfile
    }
  }
}
</script>

<style scoped>
.app-footer {
  background: #2c3e50;
  color: #ecf0f1;
  margin-top: auto;
  padding: 0;
  border-top: 3px solid #3498db;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.footer-section h4 {
  color: #3498db;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.footer-section p {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #bdc3c7;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 8px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #3498db;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.copyright p {
  margin: 0;
  font-size: 13px;
  color: #95a5a6;
}

.disclaimer {
  font-size: 12px !important;
  color: #7f8c8d !important;
  margin-top: 5px !important;
}

.tech-info {
  font-size: 12px;
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-container {
    padding: 30px 16px 15px;
  }
  
  .footer-content {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 25px;
    margin-bottom: 25px;
  }
  
  .footer-bottom {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .footer-section h4 {
    font-size: 15px;
  }
  
  .footer-section p,
  .footer-links a {
    font-size: 13px;
  }
}
</style>