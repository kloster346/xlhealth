# AI 心理咨询服务 - 前端项目

基于 Vue 3 的 AI 心理咨询网站前端应用，提供在线心理咨询服务。

## 功能特性

- 🏠 **首页展示** - 服务介绍、功能展示和最新动态
- 🤖 **AI 心理助手** - 智能对话咨询服务，支持多对话管理
- 👤 **个人中心** - 用户信息管理、对话历史、数据导出和系统设置
- 📱 **响应式设计** - 支持 PC 和移动端访问
- 🔐 **用户认证** - 安全的登录注册系统，支持路由权限控制
- 💾 **数据管理** - 对话历史存储、数据导出、清空功能
- 🎨 **主题切换** - 支持深色/浅色主题切换
- 🔔 **消息通知** - 系统消息和提醒功能

## 技术栈

- **框架**: Vue 3 + Composition API
- **UI组件库**: Element Plus 2.10.2
- **路由**: Vue Router 4
- **状态管理**: Vuex 4
- **HTTP客户端**: Axios 1.10.0
- **构建工具**: Vue CLI 5
- **样式**: CSS3 + 响应式设计
- **代码规范**: ESLint + Vue ESLint Plugin

## 项目结构

```
src/
├── api/           # API接口管理
│   ├── auth.js    # 用户认证接口
│   ├── chat.js    # 聊天相关接口
│   └── index.js   # 统一API配置
├── assets/        # 静态资源
│   └── styles/    # 样式文件
├── components/    # 公共组件
│   ├── AppFooter.vue      # 页脚组件
│   ├── AppHeader.vue      # 头部导航组件
│   └── UserProfileEdit.vue # 用户资料编辑组件
├── router/        # 路由配置
│   └── index.js   # 路由定义和权限控制
├── store/         # Vuex状态管理
│   └── index.js   # 全局状态管理
├── utils/         # 工具函数
│   ├── mockData.js    # 模拟数据
│   ├── storage.js     # 本地存储工具
│   └── validation.js  # 表单验证工具
├── views/         # 页面组件
│   ├── AIAssistant.vue  # AI心理助手页面
│   ├── HomeView.vue     # 首页
│   ├── Login.vue        # 登录页面
│   ├── Register.vue     # 注册页面
│   └── UserProfile.vue  # 个人中心页面
├── App.vue        # 根组件
└── main.js        # 入口文件
```

## 开发指南

### 环境要求

- Node.js >= 14.0.0
- npm >= 6.0.0

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run serve
```

### 构建生产版本

```bash
npm run build
```

### 代码检查和修复

```bash
npm run lint
```

## 页面说明

### 首页 (HomeView)

- 展示服务特色和功能介绍
- 提供快速开始对话入口
- 显示最新动态信息和系统公告
- 项目开发状态展示
- 服务简介和特性说明

### AI 心理助手 (AIAssistant)

- 左侧对话历史管理面板
- 实时对话界面，支持多轮对话
- 消息发送和接收，支持Enter快捷键
- 对话创建、删除和切换功能
- AI输入状态提示
- 紧急情况心理援助热线提示

### 个人中心 (UserProfile)

- **用户信息管理**：头像、昵称、邮箱等基本信息展示和编辑
- **对话统计**：总对话数、总消息数、最近活跃时间统计
- **历史记录管理**：对话历史查看、删除和快速跳转
- **数据管理**：支持清空所有数据和导出个人数据
- **系统设置**：主题切换（深色/浅色）和消息通知开关
- **账户操作**：退出登录功能

### 用户认证页面

- **登录页面 (Login)**：用户登录，支持记住登录状态
- **注册页面 (Register)**：新用户注册，表单验证

## 核心功能特性

### 用户认证系统
- 基于 Token 的身份验证
- 路由权限控制（requiresAuth/requiresGuest）
- 自动登录状态恢复
- 安全的登出机制

### 对话管理系统
- 多对话并行管理
- 对话历史持久化存储
- 实时消息发送和接收
- 对话标题自动生成
- 支持对话删除和清空

### 数据管理
- 本地存储（localStorage）
- 数据导出功能（JSON格式）
- 批量数据清理
- 用户数据统计

### 用户体验优化
- 响应式设计，适配多种设备
- 主题切换（深色/浅色模式）
- 消息通知系统
- 加载状态提示
- 错误处理和用户反馈

## API 接口设计

### 认证接口 (authAPI)
```javascript
// 用户登录
POST /api/auth/login
// 用户注册  
POST /api/auth/register
// 刷新Token
POST /api/auth/refresh
// 用户登出
POST /api/auth/logout
```

### 用户接口 (userAPI)
```javascript
// 获取用户信息
GET /api/user/profile
// 更新用户信息
PUT /api/user/profile
// 修改密码
PUT /api/user/password
```

### 聊天接口 (chatAPI)
```javascript
// 发送消息
POST /api/chat/message
// 获取对话历史
GET /api/chat/history
// 创建新对话
POST /api/chat/create
// 删除对话
DELETE /api/chat/{chatId}
```

## 开发规范

- 使用 ESLint 进行代码检查
- 组件命名采用 PascalCase（如：UserProfile.vue）
- 文件命名采用 kebab-case
- 提交信息遵循 Conventional Commits 规范
- 使用 Composition API 编写组件
- 统一使用 Element Plus 组件库
- API 请求统一通过 axios 实例处理

## 开发状态

### 已完成功能
- ✅ **TASK001**: 项目初始化和基础配置
- ✅ **TASK002**: 全局布局组件（AppHeader、AppFooter）
- ✅ **TASK003**: 用户认证系统（登录、注册、权限控制）
- ✅ **TASK004**: 首页开发（服务介绍、动态消息）
- ✅ **TASK005**: AI心理助手页面（对话管理、消息发送）
- ✅ **TASK006**: 用户认证页面（登录、注册表单）
- ✅ **TASK007**: 个人中心页面（用户信息、数据管理、系统设置）

### 开发中功能
- 🔄 后端API接口对接
- 🔄 实际AI对话功能集成

### 计划功能
- 📋 消息推送系统
- 📋 数据分析和报表
- 📋 多语言支持

## 环境配置

### 环境变量
创建 `.env` 文件配置环境变量：
```bash
# API 基础地址
VUE_APP_API_BASE_URL=http://localhost:8080/api

# 应用标题
VUE_APP_TITLE=AI心理咨询系统
```

### 开发环境要求
- Node.js >= 14.0.0
- npm >= 6.0.0 或 yarn >= 1.22.0
- 现代浏览器（Chrome 88+, Firefox 85+, Safari 14+）

## 部署说明

### 开发环境部署
1. 克隆项目到本地
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run serve`
4. 访问 `http://localhost:8080`

### 生产环境部署
1. 执行 `npm run build` 构建项目
2. 将 `dist` 目录部署到 Web 服务器
3. 配置反向代理指向后端 API 服务
4. 配置 HTTPS 和域名

## 注意事项

### 开发注意事项
- 所有组件必须遵循 ESLint 规范，组件名称使用多词命名
- API 接口调用统一使用 `src/api/` 目录下的封装方法
- 状态管理优先使用 Vuex，避免组件间直接传递复杂数据
- 路由跳转前检查用户权限，避免未授权访问

### 安全注意事项
- 敏感信息（Token、用户数据）存储在 localStorage 中
- API 请求自动添加 Authorization 头
- 401 错误自动清除本地认证信息
- 生产环境需配置 HTTPS

### 性能优化
- 使用 Vue 3 Composition API 提升组件性能
- Element Plus 按需引入减少包体积
- 图片资源使用 SVG 格式
- 合理使用 computed 和 watch 避免不必要的重渲染

## 更多配置

详见 [Vue CLI 配置参考](https://cli.vuejs.org/config/)
