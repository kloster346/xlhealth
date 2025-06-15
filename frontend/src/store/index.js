import { createStore } from 'vuex'

export default createStore({
  state: {
    // 用户状态
    user: null,
    isLoggedIn: false,
    token: localStorage.getItem('token') || null,
    
    // 对话状态
    currentChat: null,
    chatHistory: []
  },
  
  getters: {
    isAuthenticated: state => !!state.token,
    currentUser: state => state.user,
    getChatHistory: state => state.chatHistory
  },
  
  mutations: {
    // 用户相关
    SET_USER(state, user) {
      state.user = user
      state.isLoggedIn = true
    },
    
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    
    CLEAR_AUTH(state) {
      state.user = null
      state.isLoggedIn = false
      state.token = null
      localStorage.removeItem('token')
    },
    
    // 对话相关
    SET_CURRENT_CHAT(state, chat) {
      state.currentChat = chat
    },
    
    ADD_MESSAGE(state, message) {
      if (state.currentChat) {
        state.currentChat.messages.push(message)
      }
    },
    
    SET_CHAT_HISTORY(state, history) {
      state.chatHistory = history
    },
    
    ADD_CHAT_TO_HISTORY(state, chat) {
      state.chatHistory.unshift(chat)
    },
    
    REMOVE_CHAT_FROM_HISTORY(state, chatId) {
      state.chatHistory = state.chatHistory.filter(chat => chat.id !== chatId)
    },
    
    CLEAR_CHAT_HISTORY(state) {
      state.chatHistory = []
    }
  },
  
  actions: {
    // 用户登录
    login({ commit }, { user, token }) {
      commit('SET_USER', user)
      commit('SET_TOKEN', token)
    },
    
    // 用户退出
    logout({ commit }) {
      commit('CLEAR_AUTH')
      commit('SET_CURRENT_CHAT', null)
      commit('CLEAR_CHAT_HISTORY')
    },
    
    // 初始化用户状态
    initAuth({ commit, state }) {
      if (state.token) {
        // 这里可以验证token有效性
        // 暂时简单处理
        commit('SET_USER', { id: 1, nickname: '用户' })
      }
    },
    
    // 创建新对话
    createNewChat({ commit }) {
      const newChat = {
        id: Date.now(),
        title: '新对话',
        messages: [],
        createdAt: new Date().toISOString()
      }
      commit('SET_CURRENT_CHAT', newChat)
      return newChat
    },
    
    // 保存对话到历史
    saveChatToHistory({ commit, state }) {
      if (state.currentChat && state.currentChat.messages.length > 0) {
        // 更新标题为第一条消息的前20个字符
        const firstMessage = state.currentChat.messages[0]
        if (firstMessage && firstMessage.content) {
          state.currentChat.title = firstMessage.content.substring(0, 20) + '...'
        }
        commit('ADD_CHAT_TO_HISTORY', { ...state.currentChat })
      }
    }
  },
  
  modules: {
    // 预留模块扩展
  }
})