<template>
  <div class="ai-assistant">
    <div class="page-container">
      <div class="page-header">
        <h1>AI 心理助手</h1>
        <p>您的专属心理健康顾问，随时为您提供支持</p>
      </div>
      
      <div class="content-area">
        <el-card class="info-card">
          <h3>功能说明</h3>
          <p>AI 心理助手页面将在 TASK005 中完整实现，包括：</p>
          <ul>
            <li>智能对话界面</li>
            <li>历史记录管理</li>
            <li>个性化建议</li>
            <li>情绪分析</li>
          </ul>
          <el-button type="primary" style="margin-top: 20px;" disabled>
            开始对话（TASK005 中实现）
          </el-button>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AIAssistant',
  mounted() {
    console.log('AI助手页面已加载 - 将在TASK005中完整实现')
  }
}
</script>

<style scoped>
.ai-assistant {
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
  display: flex;
  justify-content: center;
}

.info-card {
  max-width: 600px;
  width: 100%;
}

.info-card h3 {
  color: #303133;
  margin-bottom: 15px;
  font-size: 1.3rem;
}

.info-card p {
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.6;
}

.info-card ul {
  color: #606266;
  padding-left: 20px;
  line-height: 1.8;
}

.info-card li {
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .ai-assistant {
    padding: 20px 16px;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .page-header p {
    font-size: 1rem;
  }
}
</style>