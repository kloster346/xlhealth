import { createStore } from 'vuex'

// 从localStorage恢复状态
const savedUser = localStorage.getItem('user')
const savedLoginStatus = localStorage.getItem('isLoggedIn')

export default createStore({
  state: {
    // 基础应用状态
    appName: 'AI心理咨询系统',
    version: '1.0.0',
    
    // 用户认证状态
    user: savedUser ? JSON.parse(savedUser) : null,
    isLoggedIn: savedLoginStatus === 'true',
    token: localStorage.getItem('token') || null,
    
    // 加载状态
    loading: {
      login: false,
      register: false,
      userInfo: false
    }
  },
  
  getters: {
    getAppInfo: state => ({
      name: state.appName,
      version: state.version
    }),
    isAuthenticated: state => state.isLoggedIn,
    currentUser: state => state.user,
    userToken: state => state.token,
    isLoading: state => loadingType => state.loading[loadingType] || false
  },
  
  mutations: {
    // 基础状态管理
    SET_APP_NAME(state, name) {
      state.appName = name
    },
    
    // 用户认证相关
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
    
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    
    CLEAR_USER(state) {
      state.user = null
      state.isLoggedIn = false
      state.token = null
      
      // 清除localStorage
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      localStorage.setItem('isLoggedIn', 'false')
    },
    
    UPDATE_USER(state, userData) {
      if (state.user) {
        state.user = { ...state.user, ...userData }
        localStorage.setItem('user', JSON.stringify(state.user))
      }
    },
    
    // 加载状态管理
    SET_LOADING(state, { type, status }) {
      state.loading[type] = status
    }
  },
  
  actions: {
    // 基础操作
    updateAppName({ commit }, name) {
      commit('SET_APP_NAME', name)
    },
    
    // 用户认证操作
    async login({ commit }, { user, token }) {
      commit('SET_USER', user)
      commit('SET_TOKEN', token)
    },
    
    async logout({ commit }) {
      commit('CLEAR_USER')
    },
    
    setUser({ commit }, user) {
      commit('SET_USER', user)
    },
    
    clearUser({ commit }) {
      commit('CLEAR_USER')
    },
    
    updateUser({ commit }, userData) {
      commit('UPDATE_USER', userData)
    },
    
    // 加载状态操作
    setLoading({ commit }, { type, status }) {
      commit('SET_LOADING', { type, status })
    }
  },
  
  modules: {
    // 为后续模块化开发预留
  }
})