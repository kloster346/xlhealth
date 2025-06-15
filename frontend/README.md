# AI 心理咨询服务 - 前端项目

基于 Vue 3 的 AI 心理咨询网站前端应用，提供在线心理咨询服务。

## 功能特性

- 🏠 **首页展示** - 服务介绍和功能展示
- 🤖 **AI 心理助手** - 智能对话咨询服务
- 👤 **个人中心** - 用户信息管理和对话历史
- 📱 **响应式设计** - 支持 PC 和移动端访问
- 🔐 **用户认证** - 安全的登录注册系统

## 技术栈

- **框架**: Vue 3 + Composition API
- **路由**: Vue Router 4
- **状态管理**: Vuex 4
- **构建工具**: Vue CLI 5
- **样式**: CSS3 + 响应式设计

## 项目结构

```
src/
├── components/     # 公共组件
├── views/         # 页面组件
├── router/        # 路由配置
├── store/         # Vuex状态管理
├── utils/         # 工具函数
├── assets/        # 静态资源
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
- 显示最新动态信息

### AI 心理助手 (ChatView)

- 实时对话界面
- 消息发送和接收
- 对话历史管理

### 个人中心 (ProfileView)

- 用户信息展示和编辑
- 对话历史查看
- 账户设置管理

## 开发规范

- 使用 ESLint 进行代码检查
- 组件命名采用 PascalCase
- 文件命名采用 kebab-case
- 提交信息遵循 Conventional Commits 规范

## 部署说明

1. 执行 `npm run build` 构建项目
2. 将 `dist` 目录部署到 Web 服务器
3. 配置反向代理指向后端 API 服务

## 更多配置

详见 [Vue CLI 配置参考](https://cli.vuejs.org/config/)
