# AI 服务架构说明

## 概述

本项目的 AI 服务采用了清晰的职责分离架构，确保各组件专注于自己的核心功能。

## 控制器职责分工

### AIController (`/api/ai/*`)

**职责**：AI 服务本身的管理和监控

- **专注领域**：AI 服务管理、监控、配置
- **不涉及**：消息存储、对话管理

#### 主要接口

1. `POST /api/ai/chat` - 纯 AI 服务调用（不保存消息）
   - 用途：AI 服务测试、独立调用
   - 特点：仅调用 AI 服务，不保存到数据库
2. `GET /api/ai/health` - 服务健康检查
3. `GET /api/ai/config` - 获取 AI 服务配置
4. `GET /api/ai/stats` - 获取服务统计信息
5. `DELETE /api/ai/context` - 清除对话上下文

### MessageController (`/api/v1/conversations/{conversationId}/messages/*`)

**职责**：完整的消息和对话管理

- **专注领域**：消息 CRUD、对话历史、AI 回复集成
- **包含功能**：消息存储、状态管理、对话维护

#### 主要接口

1. `POST /api/v1/conversations/{conversationId}/messages/ai-reply` - 生成 AI 回复（推荐）
   - 用途：生产环境 AI 对话
   - 特点：完整的消息生命周期管理
   - 功能：保存用户消息 → 调用 AI 服务 → 保存 AI 回复 → 更新对话状态
2. `POST /api/v1/conversations/{conversationId}/messages` - 发送消息
3. `GET /api/v1/conversations/{conversationId}/messages` - 获取消息列表
4. 其他消息管理接口...

## 使用建议

### 生产环境

- **推荐使用**：`MessageController` 的 AI 回复接口
- **原因**：提供完整的对话管理功能，包括消息持久化、状态管理等

### 开发测试

- **AI 服务测试**：使用 `AIController` 的 chat 接口
- **完整功能测试**：使用 `MessageController` 的 AI 回复接口

### 监控运维

- **服务监控**：使用 `AIController` 的健康检查、统计等接口
- **配置管理**：使用 `AIController` 的配置接口

## 架构优势

1. **职责清晰**：每个控制器专注于自己的核心功能
2. **易于维护**：功能边界明确，降低耦合度
3. **灵活使用**：可根据不同场景选择合适的接口
4. **便于测试**：可以独立测试 AI 服务和消息管理功能

## 集成流程

### MessageController AI 回复流程

```
用户请求 → MessageController.generateAIReply()
    ↓
保存用户消息 → MessageService.generateAIReply()
    ↓
调用AI服务 → AIServiceManager.processRequest()
    ↓
保存AI回复 → 更新消息状态 → 返回结果
```

### AIController 服务调用流程

```
用户请求 → AIController.chat()
    ↓
直接调用 → AIServiceManager.processRequest()
    ↓
返回AI回复（不保存）
```

## 注意事项

1. **AIController 的 chat 接口**仅用于 AI 服务测试，不会保存消息到数据库
2. **MessageController 的 ai-reply 接口**是生产环境推荐的 AI 对话接口
3. 两个控制器功能互补，不存在冲突
4. 可根据具体需求选择合适的接口使用
