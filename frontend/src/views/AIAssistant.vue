<template>
  <div class="ai-assistant">
    <!-- 左侧历史记录面板 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>对话历史</h3>
        <el-button 
          type="primary" 
          size="small" 
          @click="createNewConversation"
          :loading="creating"
        >
          新建对话
        </el-button>
      </div>
      
      <div class="conversation-list">
        <div 
          v-for="conversation in conversations" 
          :key="conversation.id"
          class="conversation-item"
          :class="{ active: currentConversationId === conversation.id }"
          @click="selectConversation(conversation.id)"
        >
          <div class="conversation-title">{{ conversation.title }}</div>
          <div class="conversation-time">{{ formatTime(conversation.updatedAt) }}</div>
          <el-button 
            type="danger" 
            size="small" 
            class="delete-btn"
            @click.stop="deleteConversation(conversation.id)"
          >
            删除
          </el-button>
        </div>
        
        <div v-if="conversations.length === 0" class="empty-state">
          <p>暂无对话记录</p>
          <p>点击"新建对话"开始您的第一次咨询</p>
        </div>
      </div>
    </div>
    
    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <div class="chat-header">
        <h2>AI 心理助手</h2>
        <p>您的专属心理健康顾问，随时为您提供支持</p>
      </div>
      
      <div class="chat-messages" ref="messagesContainer">
        <div v-if="!currentConversation" class="welcome-message">
          <div class="welcome-content">
            <el-icon size="48" color="#409EFF"><ChatDotRound /></el-icon>
            <h3>欢迎使用 AI 心理助手</h3>
            <p>请选择一个对话或创建新对话开始咨询</p>
          </div>
        </div>
        
        <div v-else>
          <div 
            v-for="message in currentConversation.messages" 
            :key="message.id"
            class="message-wrapper"
            :class="message.type"
          >
            <div class="message-bubble">
              <div v-if="message.type === 'ai'" class="ai-avatar">
                <el-icon><Robot /></el-icon>
              </div>
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
              </div>
            </div>
          </div>
          
          <!-- AI正在输入提示 -->
          <div v-if="aiTyping" class="message-wrapper ai">
            <div class="message-bubble">
              <div class="ai-avatar">
                <el-icon><Robot /></el-icon>
              </div>
              <div class="message-content">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-input-area">
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题或想法..."
            :disabled="!currentConversation || sending"
            @keydown.enter.exact="handleEnterKey"
            @keydown.enter.shift.exact="handleShiftEnter"
            maxlength="500"
            show-word-limit
          />
          <div class="input-actions">
            <el-button 
              type="primary" 
              @click="sendMessage"
              :disabled="!inputMessage.trim() || !currentConversation || sending"
              :loading="sending"
            >
              发送
            </el-button>
          </div>
        </div>
        
        <div class="help-text">
          <p><el-icon><Warning /></el-icon> 如遇紧急情况，请拨打心理援助热线：400-161-9995</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, computed } from 'vue'
import { ChatDotRound, Robot, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import chatService from '@/api/chat'

export default {
  name: 'AIAssistant',
  components: {
    ChatDotRound,
    Robot,
    Warning
  },
  setup() {
    // 响应式数据
    const conversations = ref([])
    const currentConversationId = ref(null)
    const inputMessage = ref('')
    const sending = ref(false)
    const creating = ref(false)
    const aiTyping = ref(false)
    const messagesContainer = ref(null)
    
    // 计算属性
    const currentConversation = computed(() => {
      return conversations.value.find(c => c.id === currentConversationId.value) || null
    })
    
    // 格式化时间
    const formatTime = (timestamp) => {
      const date = new Date(timestamp)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
      } else if (diff < 86400000) { // 24小时内
        return `${Math.floor(diff / 3600000)}小时前`
      } else {
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
      }
    }
    
    // 滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }
    
    // 加载对话列表
    const loadConversations = async () => {
      try {
        const response = await chatService.getConversations()
        if (response.success) {
          // 从分页响应中提取对话数组，并确保每个对话都有 messages 数组
          const conversationList = response.data?.records || []
          conversations.value = conversationList.map(conversation => ({
            ...conversation,
            messages: conversation.messages || [] // 确保 messages 数组存在
          }))
        } else {
          console.error('加载对话列表失败:', response.message)
          conversations.value = []
        }
        
        // 如果有对话且没有选中的对话，选中第一个
        if (conversations.value.length > 0 && !currentConversationId.value) {
          await selectConversation(conversations.value[0].id)
        }
      } catch (error) {
        console.error('加载对话列表失败:', error)
        ElMessage.error('加载对话列表失败')
        conversations.value = []
      }
    }
    
    // 创建新对话
    const createNewConversation = async () => {
      if (creating.value) return
      
      creating.value = true
      try {
        const response = await chatService.createConversation({
          title: '新对话'
        })
        if (response.success) {
          const newConversation = response.data || response.conversation
          // 确保新对话有 messages 数组
          const conversationWithMessages = {
            ...newConversation,
            messages: newConversation.messages || []
          }
          conversations.value.unshift(conversationWithMessages)
          await selectConversation(conversationWithMessages.id)
          ElMessage.success('新对话创建成功')
        } else {
          console.error('创建对话失败:', response.message)
          ElMessage.error(response.message || '创建对话失败')
        }
      } catch (error) {
        console.error('创建对话失败:', error)
        ElMessage.error('创建对话失败')
      } finally {
        creating.value = false
      }
    }
    
    // 选择对话
    const selectConversation = async (conversationId) => {
      currentConversationId.value = conversationId
      
      // 加载该对话的消息历史
      const conversation = conversations.value.find(c => c.id === conversationId)
      if (conversation && (!conversation.messages || conversation.messages.length === 0)) {
        try {
          const response = await chatService.getMessages(conversationId)
          if (response.success) {
            // 正确处理分页响应格式：response.data.records 是消息数组
            const messages = response.data?.records || []
            // 为每个消息设置正确的type字段，用于前端显示
            conversation.messages = messages.map(message => ({
              ...message,
              type: message.role === 'USER' ? 'user' : 'ai',
              timestamp: message.createdTime || message.createdAt
            }))
          } else {
            console.error('加载对话消息失败:', response.message)
            // 确保 messages 数组存在，即使加载失败
            if (!conversation.messages) {
              conversation.messages = []
            }
          }
        } catch (error) {
          console.error('加载对话消息失败:', error)
          // 确保 messages 数组存在，即使加载失败
          if (!conversation.messages) {
            conversation.messages = []
          }
        }
      }
      
      scrollToBottom()
    }
    
    // 删除对话
    const deleteConversation = async (conversationId) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这个对话吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        const response = await chatService.deleteConversation(conversationId)
        if (response.success) {
          conversations.value = conversations.value.filter(c => c.id !== conversationId)
          
          // 如果删除的是当前对话，选择第一个可用对话
          if (currentConversationId.value === conversationId) {
            if (conversations.value.length > 0) {
              await selectConversation(conversations.value[0].id)
            } else {
              currentConversationId.value = null
            }
          }
          
          ElMessage.success('对话删除成功')
        } else {
          console.error('删除对话失败:', response.message)
          ElMessage.error(response.message || '删除对话失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除对话失败:', error)
          ElMessage.error('删除对话失败')
        }
      }
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || !currentConversationId.value || sending.value) {
        return
      }
      
      const message = inputMessage.value.trim()
      inputMessage.value = ''
      sending.value = true
      
      // 立即添加用户消息到界面
      const conversation = conversations.value.find(c => c.id === currentConversationId.value)
      if (conversation) {
        // 确保 messages 数组存在
        if (!conversation.messages) {
          conversation.messages = []
        }
        
        // 创建用户消息对象
        const userMessage = {
          id: Date.now(), // 临时ID
          content: message,
          role: 'USER',
          contentType: 'TEXT',
          type: 'user',
          createdAt: new Date().toISOString()
        }
        
        // 立即显示用户消息
        conversation.messages.push(userMessage)
        scrollToBottom()
        
        // 更新对话标题（如果是第一条用户消息）
        if (conversation.messages.filter(m => m.type === 'user').length === 1) {
          const newTitle = message.length > 20 
            ? message.substring(0, 20) + '...' 
            : message
          conversation.title = newTitle
          
          // 调用后端API更新对话标题
          try {
            await chatService.updateConversation(currentConversationId.value, {
              title: newTitle
            })
          } catch (error) {
            console.error('更新对话标题失败:', error)
            // 即使后端更新失败，前端标题仍然保持更新，不影响用户体验
          }
        }
      }
      
      aiTyping.value = true
      
      try {
        const response = await chatService.sendMessage(currentConversationId.value, {
          content: message,
          role: 'USER',
          contentType: 'TEXT'
        })
        
        if (!response.success) {
          ElMessage.error(response.message || '发送消息失败')
          // 发送失败时移除刚添加的用户消息
          if (conversation && conversation.messages && conversation.messages.length > 0) {
            conversation.messages.pop()
          }
          return
        }
        
        // 用户消息发送成功后，调用AI回复生成接口
        if (conversation) {
          try {
            // 调用AI回复生成接口
             const aiResponse = await chatService.generateAIReply(currentConversationId.value, {
               message: message,
               emotionalState: null, // 可以根据需要添加情绪状态检测
               context: {
                 includeHistory: true,
                 historyLimit: 10,
                 includeUserProfile: true
               },
               preferences: {
                 preferredType: 'EMOTIONAL_SUPPORT',
                 length: 'MEDIUM',
                 tone: 'WARM',
                 includeAdvice: true,
                 includeQuestions: true
               }
             })
            
            if (aiResponse.success && aiResponse.data) {
               // 确保 messages 数组存在
               if (!conversation.messages) {
                 conversation.messages = []
               }
               
               // 添加AI回复消息到对话中
               const aiMessage = {
                 id: aiResponse.data.id || Date.now() + 1,
                 content: aiResponse.data.content,
                 role: aiResponse.data.role || 'ASSISTANT',
                 contentType: aiResponse.data.contentType || 'TEXT',
                 type: 'ai',
                 timestamp: aiResponse.data.createdTime || new Date().toISOString()
               }
               
               conversation.messages.push(aiMessage)
               scrollToBottom()
             } else {
               console.error('AI回复生成失败:', aiResponse.message)
               ElMessage.error(aiResponse.message || 'AI回复生成失败')
             }
          } catch (aiError) {
            console.error('调用AI回复接口失败:', aiError)
            ElMessage.error('AI回复生成失败，请稍后重试')
          }
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送消息失败')
        // 发送失败时移除刚添加的用户消息
        if (conversation && conversation.messages && conversation.messages.length > 0) {
          conversation.messages.pop()
        }
      } finally {
        sending.value = false
        aiTyping.value = false
        scrollToBottom()
      }
    }
    
    // 处理回车键
    const handleEnterKey = (event) => {
      event.preventDefault()
      sendMessage()
    }
    
    // 处理Shift+回车键（换行）
    const handleShiftEnter = (_event) => {
      // 允许默认行为（换行）
    }
    
    // 组件挂载时加载数据
    onMounted(() => {
      loadConversations()
    })
    
    return {
      conversations,
      currentConversationId,
      currentConversation,
      inputMessage,
      sending,
      creating,
      aiTyping,
      messagesContainer,
      formatTime,
      createNewConversation,
      selectConversation,
      deleteConversation,
      sendMessage,
      handleEnterKey,
      handleShiftEnter,
      scrollToBottom
    }
  }
}
</script>

<style scoped>
.ai-assistant {
  height: 100vh;
  display: flex;
  background: #f5f7fa;
}

/* 左侧对话列表 */
.sidebar {
  width: 300px;
  background: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafbfc;
}

.sidebar-header h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.conversation-item {
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  position: relative;
}

.conversation-item:hover {
  background: #f0f2f5;
}

.conversation-item.active {
  background: #e6f7ff;
  border-color: #1890ff;
}

.conversation-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-time {
  font-size: 12px;
  color: #909399;
}

.delete-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.conversation-item:hover .delete-btn {
  opacity: 1;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state p {
  margin: 8px 0;
  font-size: 14px;
}

/* 右侧聊天区域 */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafbfc;
}

.chat-header h2 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.chat-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8f9fa;
}

.welcome-message {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-content {
  text-align: center;
  color: #909399;
}

.welcome-content h3 {
  margin: 16px 0 8px 0;
  font-size: 18px;
  color: #606266;
}

.welcome-content p {
  margin: 0;
  font-size: 14px;
}

.message-wrapper {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-wrapper.user {
  flex-direction: row-reverse;
}

.message-bubble {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 70%;
}

.message-wrapper.user .message-bubble {
  flex-direction: row-reverse;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e6f7ff;
  color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message-content {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-wrapper.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
  word-wrap: break-word;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: right;
}

.message-wrapper.user .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-6px);
  }
}

.chat-input-area {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background: white;
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
}

.help-text {
  margin-top: 12px;
  padding: 12px 16px;
  background: #fff2e8;
  border: 1px solid #ffb366;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.help-text p {
  margin: 0;
  font-size: 13px;
  color: #e6a23c;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-assistant {
    flex-direction: column;
    height: 100vh;
  }
  
  .sidebar {
    width: 100%;
    height: 200px;
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
  }
  
  .chat-area {
    flex: 1;
  }
  
  .message-bubble {
    max-width: 85%;
  }
}
</style>