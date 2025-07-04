# AI 接口测试文档

## 目录

1. [文档概述](#文档概述)
2. [环境配置](#环境配置)
3. [通用规范](#通用规范)
4. [接口详情](#接口详情)
   - [4.1 聊天功能](#41-聊天功能)
   - [4.2 系统监控](#42-系统监控)
   - [4.3 上下文管理](#43-上下文管理)
   - [4.4 统计功能](#44-统计功能)
5. [测试场景](#测试场景)
6. [错误处理](#错误处理)
7. [Apifox 使用指南](#apifox使用指南)
8. [常见问题](#常见问题)

## 文档概述

本文档提供了 XL Health 后端 AI 服务的完整 API 接口测试规范，包含所有接口的详细信息、测试用例和使用 Apifox 进行手动测试的指南。

### 功能模块

- **聊天功能**: AI 对话处理，支持情感识别和个性化回复
- **系统监控**: 服务健康状态检查和配置信息获取
- **上下文管理**: 对话上下文的管理和摘要生成
- **统计功能**: 服务使用统计和性能监控

## 环境配置

### 基础信息

- **基础 URL**: `http://localhost:8081`
- **API 前缀**: `/api/ai`
- **内容类型**: `application/json`
- **字符编码**: `UTF-8`

### 服务端口

- 开发环境: `8081`
- 测试环境: `8081`
- 生产环境: 根据部署配置

## 通用规范

### 请求头

| 字段名       | 类型   | 必填 | 说明                               |
| ------------ | ------ | ---- | ---------------------------------- |
| Content-Type | string | 是   | application/json                   |
| X-User-Id    | string | 否   | 用户 ID，如不提供会自动生成临时 ID |

### 响应格式

所有接口都遵循统一的响应格式：

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功",
  "data": {}
}
```

#### 响应字段说明

| 字段    | 类型    | 说明               |
| ------- | ------- | ------------------ |
| success | boolean | 操作是否成功       |
| code    | integer | 状态码，0 表示成功 |
| message | string  | 响应消息           |
| data    | object  | 响应数据           |

## 接口详情

### 4.1 聊天功能

#### 4.1.1 AI 聊天接口

**基本信息**

- **接口路径**: `/api/ai/chat`
- **请求方法**: `POST`
- **功能描述**: 处理用户消息，返回 AI 回复

**请求参数**

| 参数名         | 类型   | 必填 | 说明                        |
| -------------- | ------ | ---- | --------------------------- |
| conversationId | string | 否   | 对话 ID，如不提供会自动生成 |
| message        | string | 否   | 用户消息内容                |
| emotionalState | string | 否   | 用户情感状态                |

**请求示例**

```json
{
  "conversationId": "test-conv-001",
  "message": "我感到很焦虑",
  "emotionalState": "焦虑"
}
```

**响应示例**

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功",
  "data": {
    "content": "这是一个模拟的AI回复，我理解您的感受。",
    "success": true,
    "provider": "EMOTIONAL_SUPPORT",
    "qualityScore": 85,
    "replyType": "MOCK",
    "responseTime": 1200,
    "timestamp": 1625472000000
  }
}
```

**测试用例**

1. **正常聊天**

   - 请求头: `X-User-Id: test-user-001`
   - 请求体: 包含完整的 conversationId、message、emotionalState
   - 预期: 返回 200 状态码和 AI 回复

2. **缺少用户 ID**

   - 请求头: 不包含 X-User-Id
   - 请求体: 正常消息
   - 预期: 系统自动生成临时用户 ID，正常处理

3. **空消息处理**

   - 请求体: 所有字段为空或不提供
   - 预期: 系统自动处理空字段，返回默认回复

4. **长消息测试**

   - 请求体: message 字段包含超长文本（>1000 字符）
   - 预期: 系统正常处理长文本

5. **特殊字符测试**

   - 请求体: message 包含特殊字符和 emoji
   - 预期: 正确处理特殊字符

6. **不同情感状态**
   - 测试情感: 焦虑、抑郁、愤怒、恐惧、平静、快乐
   - 预期: 根据情感状态返回相应回复

### 4.2 系统监控

#### 4.2.1 健康检查接口

**基本信息**

- **接口路径**: `/api/ai/health`
- **请求方法**: `GET`
- **功能描述**: 检查 AI 服务健康状态

**请求参数**: 无

**响应示例（健康状态）**

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功",
  "data": {
    "healthy": true
  }
}
```

**响应示例（不健康状态）**

```json
{
  "success": false,
  "code": 99999,
  "message": "服务不健康",
  "data": {
    "healthy": false
  }
}
```

#### 4.2.2 配置信息接口

**基本信息**

- **接口路径**: `/api/ai/config`
- **请求方法**: `GET`
- **功能描述**: 获取 AI 服务配置信息

**请求参数**: 无

**响应示例**

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功",
  "data": {
    "mockMode": true,
    "provider": "MOCK",
    "contextEnabled": true,
    "qualityEnabled": true,
    "monitoringEnabled": true
  }
}
```

### 4.3 上下文管理

#### 4.3.1 清除上下文接口

**基本信息**

- **接口路径**: `/api/ai/context`
- **请求方法**: `DELETE`
- **功能描述**: 清除指定对话的上下文信息

**请求参数**

| 参数名         | 类型   | 必填 | 位置  | 说明    |
| -------------- | ------ | ---- | ----- | ------- |
| userId         | string | 是   | query | 用户 ID |
| conversationId | string | 是   | query | 对话 ID |

**请求示例**

```
DELETE /api/ai/context?userId=test-user-005&conversationId=test-conv-005
```

**响应示例（当前实现返回 500 错误）**

```json
{
  "success": false,
  "code": 50001,
  "message": "内部服务器错误"
}
```

#### 4.3.2 获取上下文摘要接口

**基本信息**

- **接口路径**: `/api/ai/context/summary`
- **请求方法**: `GET`
- **功能描述**: 获取指定对话的上下文摘要

**请求参数**

| 参数名         | 类型   | 必填 | 位置  | 说明    |
| -------------- | ------ | ---- | ----- | ------- |
| userId         | string | 是   | query | 用户 ID |
| conversationId | string | 是   | query | 对话 ID |

**请求示例**

```
GET /api/ai/context/summary?userId=test-user-007&conversationId=test-conv-007
```

**响应示例（当前实现返回 500 错误）**

```json
{
  "success": false,
  "code": 50001,
  "message": "内部服务器错误"
}
```

### 4.4 统计功能

#### 4.4.1 获取统计信息接口

**基本信息**

- **接口路径**: `/api/ai/stats`
- **请求方法**: `GET`
- **功能描述**: 获取 AI 服务使用统计信息

**请求参数**: 无

**响应示例**

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功",
  "data": "统计数据字符串"
}
```

#### 4.4.2 重置统计信息接口

**基本信息**

- **接口路径**: `/api/ai/stats/reset`
- **请求方法**: `POST`
- **功能描述**: 重置 AI 服务统计信息

**请求参数**: 无

**响应示例**

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功"
}
```

## 测试场景

### 场景 1: 完整对话流程

1. 检查服务健康状态 (`GET /api/ai/health`)
2. 获取服务配置 (`GET /api/ai/config`)
3. 发起聊天对话 (`POST /api/ai/chat`)
4. 获取对话统计 (`GET /api/ai/stats`)
5. 清除对话上下文 (`DELETE /api/ai/context`)

### 场景 2: 异常处理测试

1. 测试缺少必要参数的请求
2. 测试无效的用户 ID
3. 测试服务不健康状态
4. 测试网络超时情况

### 场景 3: 性能测试

1. 并发聊天请求测试
2. 长消息处理测试
3. 频繁请求压力测试

## 错误处理

### 常见错误码

| 错误码 | 说明           | 处理建议               |
| ------ | -------------- | ---------------------- |
| 0      | 成功           | 正常处理               |
| 50001  | 内部服务器错误 | 检查服务状态，重试请求 |
| 99999  | 服务异常       | 检查服务健康状态       |

### HTTP 状态码

| 状态码 | 说明       | 常见原因     |
| ------ | ---------- | ------------ |
| 200    | 成功       | 请求正常处理 |
| 400    | 请求错误   | 参数格式错误 |
| 500    | 服务器错误 | 服务内部异常 |

## Apifox 使用指南

### 1. 环境配置

1. 打开 Apifox，创建新项目
2. 设置环境变量：
   - `base_url`: `http://localhost:8080`
   - `api_prefix`: `/api/ai`

### 2. 导入接口

#### 方法一：手动创建

1. 点击"新建接口"
2. 设置接口基本信息：

   - 接口名称：如"AI 聊天接口"
   - 请求方法：POST
   - 接口路径：`{{base_url}}{{api_prefix}}/chat`

3. 配置请求头：

   - Content-Type: application/json
   - X-User-Id: test-user-001（可选）

4. 配置请求体（JSON 格式）：

```json
{
  "conversationId": "test-conv-001",
  "message": "我感到很焦虑",
  "emotionalState": "焦虑"
}
```

#### 方法二：批量导入

1. 将本文档中的接口信息整理成 OpenAPI 格式
2. 使用 Apifox 的导入功能导入

### 3. 测试执行

#### 单接口测试

1. 选择要测试的接口
2. 检查请求参数和请求体
3. 点击"发送"按钮
4. 查看响应结果
5. 验证响应格式和数据

#### 批量测试

1. 创建测试集合
2. 添加多个测试用例
3. 设置测试数据
4. 执行批量测试
5. 查看测试报告

### 4. 测试用例示例

#### 聊天接口测试用例

**用例 1：正常聊天**

- 请求 URL: `{{base_url}}{{api_prefix}}/chat`
- 请求方法: POST
- 请求头:
  ```
  Content-Type: application/json
  X-User-Id: test-user-001
  ```
- 请求体:
  ```json
  {
    "conversationId": "test-conv-001",
    "message": "我感到很焦虑",
    "emotionalState": "焦虑"
  }
  ```
- 预期响应: 200 状态码，success 为 true

**用例 2：缺少用户 ID**

- 请求 URL: `{{base_url}}{{api_prefix}}/chat`
- 请求方法: POST
- 请求头:
  ```
  Content-Type: application/json
  ```
- 请求体:
  ```json
  {
    "conversationId": "test-conv-002",
    "message": "测试消息",
    "emotionalState": "中性"
  }
  ```
- 预期响应: 200 状态码，系统自动处理

### 5. 断言设置

在 Apifox 中设置响应断言：

1. **状态码断言**：

   ```
   状态码 等于 200
   ```

2. **JSON 路径断言**：

   ```
   $.success 等于 true
   $.code 等于 0
   $.data.content 存在
   ```

3. **响应时间断言**：
   ```
   响应时间 小于 5000ms
   ```

### 6. 数据驱动测试

1. 创建测试数据文件（CSV 或 JSON）
2. 在 Apifox 中导入测试数据
3. 配置参数化测试
4. 执行数据驱动测试

**示例数据文件（CSV）**：

```csv
userId,conversationId,message,emotionalState
test-user-001,test-conv-001,我感到焦虑,焦虑
test-user-002,test-conv-002,我很开心,快乐
test-user-003,test-conv-003,我感到愤怒,愤怒
```

## 常见问题

### Q1: 接口返回 500 错误怎么办？

**A**: 当前某些接口（如上下文管理相关接口）可能返回 500 错误，这是正常的测试行为。检查：

1. 服务是否正常启动
2. 参数是否正确
3. 查看服务日志获取详细错误信息

### Q2: 如何处理认证问题？

**A**: 当前测试环境已禁用安全认证，如果遇到认证问题：

1. 确认测试环境配置
2. 检查请求头设置
3. 联系开发团队确认认证要求

### Q3: 响应时间过长怎么办？

**A**: 如果接口响应时间过长：

1. 检查网络连接
2. 确认服务器负载
3. 调整 Apifox 超时设置
4. 联系运维团队检查服务状态

### Q4: 如何调试接口问题？

**A**: 调试步骤：

1. 检查请求格式是否正确
2. 查看 Apifox 控制台日志
3. 检查服务器日志
4. 使用浏览器开发者工具验证
5. 对比测试用例和实际请求

### Q5: 如何验证数据格式？

**A**: 数据验证方法：

1. 使用 JSON Schema 验证
2. 设置 Apifox 断言
3. 手动检查关键字段
4. 使用正则表达式验证格式

---

**文档版本**: v1.0  
**最后更新**: 2025-01-04  
**维护人员**: XL Health 开发团队

如有问题或建议，请联系开发团队。
