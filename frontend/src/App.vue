<template>
  <div id="app">
    <el-container class="app-container">
      <!-- 顶部导航栏 -->
      <el-header class="header-wrapper">
        <AppHeader />
      </el-header>
      
      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view/>
      </el-main>
      
      <!-- 页脚 -->
      <el-footer class="footer-wrapper">
        <AppFooter />
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'

export default {
  name: 'App',
  components: {
    AppHeader,
    AppFooter
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f5f5f5;
}

.app-container {
  min-height: 100vh;
}

.header-wrapper {
  padding: 0;
  height: auto;
}

.main-content {
  flex: 1;
  padding: 20px 0;
  background-color: #f8f9fa;
}

.footer-wrapper {
  padding: 0;
  height: auto;
}

/* Element Plus 组件样式覆盖 */
.el-header {
  padding: 0;
}

.el-footer {
  padding: 0;
  height: auto;
}

.el-main {
  padding: 0;
}
</style>
