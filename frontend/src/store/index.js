import { createStore } from 'vuex'

// 从localStorage恢复状态
const savedUser = localStorage.getItem('user')
const savedLoginStatus = localStorage.getItem('isLoggedIn')

export default createStore({
  state: {
    // 基础应用状态
    appName: 'AI心理咨询系统',
    version: '1.0.0',
    
    // 基础用户状态（为后续扩展预留）
    user: savedUser ? JSON.parse(savedUser) : null,
    isLoggedIn: savedLoginStatus === 'true'
  },
  
  getters: {
    getAppInfo: state => ({
      name: state.appName,
      version: state.version
    }),
    isAuthenticated: state => state.isLoggedIn
  },
  
  mutations: {
    // 基础状态管理
    SET_APP_NAME(state, name) {
      state.appName = name
    },
    
    SET_USER(state, user) {
      state.user = user
      state.isLoggedIn = !!user
      
      // 同步到localStorage
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
        localStorage.setItem('isLoggedIn', 'true')
      } else {
        localStorage.removeItem('user')
        localStorage.setItem('isLoggedIn', 'false')
      }
    },
    
    CLEAR_USER(state) {
      state.user = null
      state.isLoggedIn = false
      
      // 清除localStorage
      localStorage.removeItem('user')
      localStorage.setItem('isLoggedIn', 'false')
    }
  },
  
  actions: {
    // 基础操作（为后续扩展预留）
    updateAppName({ commit }, name) {
      commit('SET_APP_NAME', name)
    },
    
    setUser({ commit }, user) {
      commit('SET_USER', user)
    },
    
    clearUser({ commit }) {
      commit('CLEAR_USER')
    }
  },
  
  modules: {
    // 为后续模块化开发预留
  }
})