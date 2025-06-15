<template>
  <div class="home">
    <div class="welcome-section">
      <h1>欢迎使用 AI 心理咨询系统</h1>
      <p>基于 Vue 3 + Element Plus 的现代化心理健康平台</p>
      <div class="feature-list">
        <div class="feature-item">
          <h3>项目架构</h3>
          <p>Vue 3 + Vue Router + Vuex + Element Plus</p>
        </div>
        <div class="feature-item">
          <h3>开发状态</h3>
          <p>TASK001 - 基础架构搭建完成</p>
        </div>
        <div class="feature-item">
          <h3>下一步</h3>
          <p>TASK002 - 全局布局组件开发</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  data() {
    return {
      message: 'TASK001 基础架构搭建完成'
    }
  },
  
  mounted() {
    console.log('Vue 3 项目启动成功')
    console.log('Element Plus 组件库已配置')
    console.log('Vue Router 路由系统已配置')
    console.log('Vuex 状态管理已配置')
    console.log('项目基础架构搭建完成')
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.welcome-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  text-align: center;
  max-width: 800px;
  width: 100%;
}

.welcome-section h1 {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-section > p {
  font-size: 18px;
  color: #666;
  margin-bottom: 40px;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.feature-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 30px 20px;
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.feature-item h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.feature-item p {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-section {
    padding: 40px 20px;
  }
  
  .welcome-section h1 {
    font-size: 28px;
  }
  
  .welcome-section > p {
    font-size: 16px;
  }
  
  .feature-list {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

@media (max-width: 480px) {
  .home {
    padding: 10px;
  }
  
  .welcome-section {
    padding: 30px 15px;
  }
  
  .welcome-section h1 {
    font-size: 24px;
  }
}
</style>
