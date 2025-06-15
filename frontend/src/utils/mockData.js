// 模拟数据文件 - 用于开发阶段的数据模拟

// 模拟消息列表数据
export const mockMessages = [
  {
    id: 1,
    title: "欢迎使用AI心理助手",
    content: "我们致力于为您提供专业的心理健康支持，随时倾听您的感受。",
    date: "2025-01-15",
    type: "welcome",
    isRead: false
  },
  {
    id: 2,
    title: "心理健康小贴士",
    content: "保持规律的作息时间，适当运动，与朋友交流，都有助于维护心理健康。",
    date: "2025-01-14",
    type: "tip",
    isRead: true
  },
  {
    id: 3,
    title: "新功能上线通知",
    content: "我们新增了对话历史记录功能，您可以随时查看之前的对话内容。",
    date: "2025-01-13",
    type: "notification",
    isRead: true
  },
  {
    id: 4,
    title: "情绪管理指南",
    content: "学会识别和管理自己的情绪是心理健康的重要组成部分。",
    date: "2025-01-12",
    type: "guide",
    isRead: false
  },
  {
    id: 5,
    title: "系统维护通知",
    content: "系统将在今晚进行例行维护，预计维护时间为1小时。",
    date: "2025-01-11",
    type: "maintenance",
    isRead: true
  }
];

// 模拟用户数据
export const mockUsers = [
  {
    id: 1,
    username: "testuser",
    email: "test@example.com",
    password: "123456", // 实际项目中不应明文存储密码
    nickname: "测试用户",
    avatar: "",
    createdAt: "2025-01-10"
  },
  {
    id: 2,
    username: "demo",
    email: "demo@example.com",
    password: "demo123",
    nickname: "演示用户",
    avatar: "",
    createdAt: "2025-01-09"
  }
];

// 消息类型配置
export const messageTypes = {
  welcome: {
    name: "欢迎消息",
    color: "#4CAF50",
    icon: "👋"
  },
  tip: {
    name: "健康贴士",
    color: "#2196F3",
    icon: "💡"
  },
  notification: {
    name: "系统通知",
    color: "#FF9800",
    icon: "🔔"
  },
  guide: {
    name: "指导建议",
    color: "#9C27B0",
    icon: "📖"
  },
  maintenance: {
    name: "维护通知",
    color: "#F44336",
    icon: "🔧"
  }
};

// 获取消息类型信息的辅助函数
export const getMessageTypeInfo = (type) => {
  return messageTypes[type] || {
    name: "未知类型",
    color: "#757575",
    icon: "❓"
  };
};

// 模拟API延迟
export const simulateApiDelay = (ms = 500) => {
  return new Promise(resolve => setTimeout(resolve, ms));
};

// 生成模拟ID的辅助函数
export const generateMockId = () => {
  return Date.now() + Math.random().toString(36).substr(2, 9);
};