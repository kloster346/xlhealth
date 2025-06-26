import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AIAssistant from '../views/AIAssistant.vue'
import Profile from '../views/Profile.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: { requiresGuest: true }
  },
  {
    path: '/ai-assistant',
    name: 'ai-assistant',
    component: AIAssistant,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: Profile,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫 - 权限控制
router.beforeEach((to, from, next) => {
  // 从 localStorage 检查登录状态
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      // 未登录，跳转到登录页面，并保存原始路径
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  // 只允许游客访问的页面（如登录、注册）
  if (to.meta.requiresGuest) {
    if (isLoggedIn) {
      // 已登录，跳转到首页
      next('/')
      return
    }
  }
  
  next()
})

export default router
