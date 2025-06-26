// 基础模拟数据服务
// 为后续功能开发提供数据结构模板

// 应用配置数据
export const appConfig = {
  name: 'AI心理咨询系统',
  version: '1.0.0',
  description: '基于AI技术的心理健康咨询平台',
  features: [
    '智能对话咨询',
    '心理健康评估',
    '情绪状态跟踪',
    '个性化建议'
  ]
}

// 基础数据结构模板（为后续开发预留）
export const dataTemplates = {
  user: {
    id: null,
    username: '',
    email: '',
    nickname: '',
    createdAt: ''
  },
  
  message: {
    id: null,
    type: '',
    title: '',
    content: '',
    timestamp: '',
    isRead: false
  },
  
  chatMessage: {
    id: null,
    type: 'user', // 'user' | 'ai'
    content: '',
    timestamp: ''
  },
  
  conversation: {
    id: null,
    title: '',
    messages: [],
    createdAt: '',
    updatedAt: ''
  }
}

// AI回复模拟数据库
export const aiResponses = {
  // 问候语回复
  greetings: [
    '您好！我是您的AI心理助手，很高兴为您服务。请告诉我您今天的感受或遇到的困扰。',
    '欢迎来到心理咨询室！我会认真倾听您的想法，请放心与我分享。',
    '您好！我在这里陪伴您，请随时告诉我您想聊的话题。'
  ],
  
  // 情绪相关回复
  emotions: {
    anxiety: [
      '我理解您现在感到焦虑。焦虑是一种很常见的情绪反应。您能告诉我是什么让您感到焦虑吗？',
      '焦虑感确实不好受。让我们一起来分析一下，试试深呼吸，慢慢地吸气，然后缓缓呼出。',
      '当我们感到焦虑时，身体和心理都在提醒我们需要关注。您愿意和我分享更多细节吗？'
    ],
    depression: [
      '我能感受到您现在的低落情绪。请记住，您并不孤单，我会陪伴您度过这段时光。',
      '抑郁的感觉很沉重，但请相信这种状态是可以改善的。您最近有什么特别的压力吗？',
      '您的感受是真实且重要的。让我们一步步来，先从今天开始，有什么小事让您感到一丝温暖吗？'
    ],
    stress: [
      '压力确实会让人感到疲惫。您能告诉我压力的主要来源是什么吗？',
      '面对压力时，我们的身心都需要额外的关爱。您平时有什么放松的方式吗？',
      '压力是生活的一部分，但我们可以学会更好地管理它。您愿意尝试一些减压技巧吗？'
    ]
  },
  
  // 通用回复
  general: [
    '我很认真地在倾听您的话。您的感受对我来说很重要，请继续分享。',
    '谢谢您与我分享这些。每个人的经历都是独特的，您的感受完全可以理解。',
    '您提到的这个问题很值得深入探讨。您觉得这种情况从什么时候开始的呢？',
    '我能感受到您内心的复杂情感。处理这些感受需要时间，我们可以慢慢来。',
    '您的想法很有意思。从另一个角度来看，您觉得这个情况还有其他的可能性吗？'
  ],
  
  // 建议和技巧
  suggestions: [
    '我建议您可以尝试正念冥想，每天花10分钟专注于当下的感受。',
    '保持规律的作息和适量运动对心理健康很有帮助。您愿意制定一个小目标吗？',
    '写日记是一个很好的情绪管理方式，可以帮助您更好地了解自己的想法。',
    '与信任的朋友或家人分享您的感受，社会支持对心理健康非常重要。'
  ]
}

// 关键词匹配规则
export const keywordMatches = {
  '焦虑|紧张|担心|害怕': 'anxiety',
  '抑郁|沮丧|难过|悲伤|低落': 'depression',
  '压力|疲惫|累|忙': 'stress',
  '你好|您好|hi|hello': 'greetings'
}

// 本地存储工具类
export const localStorageUtils = {
  // 设置数据
  setItem(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
      return true
    } catch (error) {
      console.error('LocalStorage设置失败:', error)
      return false
    }
  },
  
  // 获取数据
  getItem(key) {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error('LocalStorage获取失败:', error)
      return null
    }
  },
  
  // 删除数据
  removeItem(key) {
    try {
      localStorage.removeItem(key)
      return true
    } catch (error) {
      console.error('LocalStorage删除失败:', error)
      return false
    }
  },
  
  // 清空所有数据
  clear() {
    try {
      localStorage.clear()
      return true
    } catch (error) {
      console.error('LocalStorage清空失败:', error)
      return false
    }
  }
}