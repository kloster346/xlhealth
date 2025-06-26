// AI对话模拟API服务
import { aiResponses, keywordMatches, localStorageUtils } from '@/utils/mockData'

// 生成唯一ID
function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

// 获取当前时间戳
function getCurrentTimestamp() {
  return new Date().toISOString()
}

// AI回复生成器
class AIResponseGenerator {
  // 根据用户消息生成AI回复
  generateResponse(userMessage) {
    const message = userMessage.toLowerCase()
    
    // 检查关键词匹配
    for (const [keywords, category] of Object.entries(keywordMatches)) {
      const regex = new RegExp(keywords, 'i')
      if (regex.test(message)) {
        if (category === 'greetings') {
          return this.getRandomResponse(aiResponses.greetings)
        } else if (aiResponses.emotions[category]) {
          return this.getRandomResponse(aiResponses.emotions[category])
        }
      }
    }
    
    // 如果没有匹配到特定关键词，返回通用回复
    const responseTypes = ['general', 'suggestions']
    const randomType = responseTypes[Math.floor(Math.random() * responseTypes.length)]
    return this.getRandomResponse(aiResponses[randomType])
  }
  
  // 获取随机回复
  getRandomResponse(responses) {
    return responses[Math.floor(Math.random() * responses.length)]
  }
}

const aiGenerator = new AIResponseGenerator()

// 对话数据管理
export const chatService = {
  // 获取所有对话列表
  getConversations() {
    return new Promise((resolve) => {
      setTimeout(() => {
        const conversations = localStorageUtils.getItem('conversations') || []
        resolve(conversations)
      }, 100)
    })
  },
  
  // 获取特定对话的消息
  getConversation(conversationId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const conversations = localStorageUtils.getItem('conversations') || []
        const conversation = conversations.find(c => c.id === conversationId)
        resolve(conversation || null)
      }, 100)
    })
  },
  
  // 创建新对话
  createConversation(title = '新对话') {
    return new Promise((resolve) => {
      setTimeout(() => {
        const conversations = localStorageUtils.getItem('conversations') || []
        const newConversation = {
          id: generateId(),
          title,
          messages: [],
          createdAt: getCurrentTimestamp(),
          updatedAt: getCurrentTimestamp()
        }
        
        conversations.unshift(newConversation)
        localStorageUtils.setItem('conversations', conversations)
        resolve(newConversation)
      }, 200)
    })
  },
  
  // 发送消息并获取AI回复
  sendMessage(conversationId, userMessage) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const conversations = localStorageUtils.getItem('conversations') || []
        const conversationIndex = conversations.findIndex(c => c.id === conversationId)
        
        if (conversationIndex === -1) {
          resolve({ error: '对话不存在' })
          return
        }
        
        const conversation = conversations[conversationIndex]
        
        // 添加用户消息
        const userMsg = {
          id: generateId(),
          type: 'user',
          content: userMessage,
          timestamp: getCurrentTimestamp()
        }
        
        conversation.messages.push(userMsg)
        
        // 生成AI回复
        const aiResponse = aiGenerator.generateResponse(userMessage)
        const aiMsg = {
          id: generateId(),
          type: 'ai',
          content: aiResponse,
          timestamp: getCurrentTimestamp()
        }
        
        // 模拟AI回复延迟
        setTimeout(() => {
          conversation.messages.push(aiMsg)
          conversation.updatedAt = getCurrentTimestamp()
          
          // 更新对话标题（使用第一条用户消息的前20个字符）
          if (conversation.messages.filter(m => m.type === 'user').length === 1) {
            conversation.title = userMessage.length > 20 
              ? userMessage.substring(0, 20) + '...' 
              : userMessage
          }
          
          localStorageUtils.setItem('conversations', conversations)
          resolve({ userMessage: userMsg, aiMessage: aiMsg })
        }, Math.random() * 2000 + 1000) // 1-3秒随机延迟
        
      }, 100)
    })
  },
  
  // 删除对话
  deleteConversation(conversationId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const conversations = localStorageUtils.getItem('conversations') || []
        const filteredConversations = conversations.filter(c => c.id !== conversationId)
        localStorageUtils.setItem('conversations', filteredConversations)
        resolve(true)
      }, 200)
    })
  },
  
  // 清空所有对话
  clearAllConversations() {
    return new Promise((resolve) => {
      setTimeout(() => {
        localStorageUtils.setItem('conversations', [])
        resolve(true)
      }, 200)
    })
  }
}

// 导出默认服务
export default chatService