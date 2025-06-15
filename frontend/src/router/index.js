import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AIAssistant from '../views/AIAssistant.vue'
import Profile from '../views/Profile.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/ai-assistant',
    name: 'ai-assistant',
    component: AIAssistant
  },
  {
    path: '/profile',
    name: 'profile',
    component: Profile
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫 - 基础权限控制
router.beforeEach((to, from, next) => {
  // 需要登录的页面
  const requiresAuth = ['/ai-assistant', '/profile']
  
  // 检查是否需要登录
  if (requiresAuth.includes(to.path)) {
    // 这里暂时从 localStorage 检查登录状态
    // 在实际应用中，应该从 Vuex store 检查
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    
    if (!isLoggedIn) {
      // 未登录，跳转到首页
      console.log('访问受保护页面，但未登录，跳转到首页')
      next('/')
      return
    }
  }
  
  next()
})

export default router
