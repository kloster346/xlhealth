# 认证接口测试指南

## 测试环境
- 服务器地址: http://localhost:8081
- 基础路径: /api/v1/auth

## 接口测试

### 1. 用户注册接口
**POST** `/api/v1/auth/register`

**请求头:**
```
Content-Type: application/json
```

**请求体:**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test123456",
  "confirmPassword": "Test123456",
  "nickname": "测试用户"
}
```

**预期响应:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "注册成功",
  "data": {
    "accessToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatarUrl": null,
      "status": "ACTIVE"
    }
  },
  "timestamp": "2025-07-01T20:16:24"
}
```

### 2. 用户登录接口
**POST** `/api/v1/auth/login`

**请求头:**
```
Content-Type: application/json
```

**请求体:**
```json
{
  "usernameOrEmail": "testuser",
  "password": "Test123456",
  "rememberMe": false
}
```

**预期响应:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "登录成功",
  "data": {
    "accessToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatarUrl": null,
      "status": "ACTIVE"
    }
  },
  "timestamp": "2025-07-01T20:16:24"
}
```

### 3. Token验证接口
**GET** `/api/v1/auth/validate`

**请求头:**
```
Authorization: Bearer {从登录接口获取的token}
```

**预期响应:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "令牌有效",
  "data": true,
  "timestamp": "2025-07-01T20:16:24"
}
```

### 4. 用户登出接口
**POST** `/api/v1/auth/logout`

**请求头:**
```
Authorization: Bearer {从登录接口获取的token}
```

**成功响应（首次登出）:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "登出成功",
  "data": "登出成功",
  "timestamp": "2025-07-01T20:16:24"
}
```

**错误响应（重复登出）:**
```json
{
  "success": false,
  "code": "ALREADY_LOGGED_OUT",
  "message": "用户已经登出，无需重复操作",
  "data": null,
  "timestamp": "2025-07-01T20:16:24"
}
```

**错误响应（会话不存在）:**
```json
{
  "success": false,
  "code": "SESSION_NOT_FOUND",
  "message": "会话不存在",
  "data": null,
  "timestamp": "2025-07-01T20:16:24"
}
```

**错误响应（会话已过期）:**
```json
{
  "success": false,
  "code": "SESSION_EXPIRED",
  "message": "会话已过期",
  "data": null,
  "timestamp": "2025-07-01T20:16:24"
}
```

### 5. Token刷新接口
**POST** `/api/v1/auth/refresh`

**请求头:**
```
Authorization: Bearer {从登录接口获取的token}
```

**请求体:**
```json
{
  "refreshToken": "0e86ccc555a347a..." // 可选，如果不提供则使用会话中存储的refreshToken
}
```

**预期响应:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "刷新成功",
  "data": {
    "accessToken": "eyJ...", // 新的访问令牌
    "refreshToken": "0f8221ac0ecf4c03...", // 新的刷新令牌
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatarUrl": null,
      "status": "ACTIVE"
    }
  },
  "timestamp": "2025-07-01T20:16:24"
}
```

**错误响应（刷新令牌无效）:**
```json
{
  "success": false,
  "code": "INVALID_REFRESH_TOKEN",
  "message": "刷新令牌无效或已过期",
  "data": null,
  "timestamp": "2025-07-01T20:16:24"
}
```

**错误响应（会话已过期）:**
```json
{
  "success": false,
  "code": "SESSION_EXPIRED",
  "message": "会话已过期，请重新登录",
  "data": null,
  "timestamp": "2025-07-01T20:16:24"
}
```

## 测试步骤

1. **首先测试注册接口**
   - 使用Apifox发送注册请求
   - 检查响应是否包含token和用户信息
   - 保存返回的token用于后续测试

2. **测试登录接口**
   - 使用相同的用户名和密码登录
   - 验证返回的token和用户信息

3. **测试token验证接口**
   - 使用获取到的token调用验证接口
   - 确认返回true表示token有效

4. **测试登出接口**
   - 使用有效token调用登出接口，确认返回"登出成功"
   - 立即使用相同token再次调用登出接口，确认返回"用户已经登出，无需重复操作"
   - 使用已登出的token访问其他需要认证的接口，确认返回401错误
   - 使用过期token调用登出接口，确认返回"会话已过期"
   - 使用不存在的token调用登出接口，确认返回"会话不存在"

5. **测试token刷新接口**
   - 使用有效token调用刷新接口
   - 验证返回新的访问令牌(accessToken)和刷新令牌(refreshToken)
   - 使用新的访问令牌访问需要认证的接口，确认能够正常访问
   - 等待一段时间后（但不超过刷新令牌的有效期），再次使用刷新令牌获取新的访问令牌
   - 测试使用旧的刷新令牌是否会返回错误（刷新令牌通常是一次性使用）

## 错误测试

1. **注册时使用重复用户名**
2. **登录时使用错误密码**
3. **使用无效token访问需要认证的接口**
4. **不提供token访问需要认证的接口**
5. **登出接口错误测试**
   - 重复登出测试（应返回ALREADY_LOGGED_OUT）
   - 使用过期token登出（应返回SESSION_EXPIRED）
   - 使用不存在的token登出（应返回SESSION_NOT_FOUND）
   - 不提供token调用登出接口（应返回UNAUTHORIZED）

## 测试说明

### 重要说明：JWT + Session 混合认证机制

本系统采用 JWT + Session 混合认证机制，专为心理健康咨询平台的安全需求设计：

1. **双重安全保障**：JWT 提供无状态认证，Session 表提供安全控制和审计功能
2. **真正的登出**：登出时会删除服务端 session 记录，实现真正的会话终止
3. **设备管理**：记录用户登录的 IP 地址、设备信息，支持多设备管理
4. **安全审计**：所有登录会话都有完整的审计日志，符合医疗健康行业合规要求
5. **会话撤销**：支持强制登出所有设备、撤销特定会话等安全功能

### Token刷新机制说明

本系统采用双Token认证机制，包括访问令牌(Access Token)和刷新令牌(Refresh Token)：

1. **访问令牌(Access Token)**：
   - 短期有效（通常为1-2小时）
   - 用于访问受保护的API资源
   - 无状态，减轻服务器负担
   - 过期后需要使用刷新令牌获取新的访问令牌

2. **刷新令牌(Refresh Token)**：
   - 长期有效（通常为7-30天）
   - 仅用于获取新的访问令牌，不能直接访问资源
   - 存储在服务器数据库中，可以被撤销
   - 通常是一次性使用，每次刷新都会生成新的刷新令牌
   - 提高安全性，即使访问令牌泄露，攻击者也只能在短时间内使用

3. **刷新流程**：
   - 当访问令牌过期时，客户端使用刷新令牌请求新的访问令牌
   - 服务器验证刷新令牌的有效性，并检查关联会话的状态
   - 验证通过后，服务器生成新的访问令牌和刷新令牌
   - 旧的刷新令牌被标记为已使用，防止重放攻击
   - 客户端使用新的访问令牌继续访问资源，无需用户重新登录

4. **安全优势**：
   - 减少用户重复登录的频率，提升用户体验
   - 缩短访问令牌的有效期，降低令牌被盗用的风险
   - 支持撤销特定用户的所有会话（如密码更改、检测到异常行为时）
   - 符合OAuth 2.0和OpenID Connect等现代认证标准

### 测试注意事项

#### 登出测试
- **逻辑删除机制**：登出成功后，session 记录不会被物理删除，而是通过设置 `deleted=1` 和 `status=0` 标记为已删除和失效
- **重复登出检测**：对已登出的用户再次执行登出操作时，返回"用户已经登出，无需重复操作"而不是"登出成功"
- **会话状态检查**：登出接口会检查会话状态，区分"有效"、"已登出"、"已过期"、"不存在"等不同情况
- **安全验证**：使用已登出的 token 访问受保护接口应返回 401 错误
- **数据库验证**：可以查询 `user_sessions` 表验证 session 记录的 `deleted` 和 `status` 字段是否被正确更新
- **审计保留**：所有会话记录都会保留在数据库中，便于安全审计和问题排查

#### Session 管理测试
- **创建验证**：登录成功后检查 `user_sessions` 表是否创建了对应记录
- **字段验证**：验证 session 记录包含正确的用户 ID、IP 地址、设备信息
- **状态字段**：新创建的 session 应该有 `deleted=0`（未删除）和 `status=1`（有效）
- **过期时间**：测试 session 过期时间是否与 JWT 过期时间一致
- **多设备管理**：测试多设备登录时的 session 管理
- **逻辑删除验证**：登出后验证 session 记录的 `deleted` 和 `status` 字段更新
- **查询过滤**：验证只有 `deleted=0 AND status=1` 的会话被认为是有效会话
- **刷新令牌验证**：验证刷新操作后，`refresh_token` 字段是否更新，旧的刷新令牌是否失效

#### 安全审计测试
- 验证登录时是否正确记录 IP 地址和 User-Agent
- 测试异常登录行为的检测和记录
- 验证会话活动时间的更新机制

## 注意事项

- 确保数据库连接正常
- 第一次注册前确保用户表为空或使用不同的用户名
- 保存token用于后续接口测试
- 注意访问令牌的有效期（默认2小时）和刷新令牌的有效期（默认7天）
- **数据库字段要求**：确保 `user_sessions` 表包含 `deleted`、`status`、`session_token`和`refresh_token`字段
- **逻辑删除测试**：登出后检查数据库记录是否正确标记为已删除（`deleted=1, status=0`）
- **重复操作测试**：测试重复登出、使用已登出token等边界情况
- **会话状态测试**：验证不同会话状态下的接口响应是否正确
- **刷新令牌测试**：验证刷新令牌是否为一次性使用，使用过的刷新令牌再次请求应返回错误

## 刷新令牌测试步骤

1. **基本刷新流程测试**
   - 登录获取初始访问令牌和刷新令牌
   - 记录数据库中的刷新令牌值
   - 使用刷新令牌调用刷新接口
   - 验证返回新的访问令牌和刷新令牌
   - 检查数据库中刷新令牌是否已更新

2. **令牌有效性测试**
   - 使用新的访问令牌访问受保护资源，确认可以正常访问
   - 尝试使用旧的访问令牌访问受保护资源，确认返回401错误
   - 尝试使用旧的刷新令牌获取新的访问令牌，确认返回错误

3. **边界情况测试**
   - 使用无效的刷新令牌调用刷新接口
   - 使用已过期的刷新令牌调用刷新接口
   - 在用户登出后使用刷新令牌调用刷新接口
   - 不提供刷新令牌调用刷新接口（使用会话中存储的刷新令牌）

4. **安全测试**
   - 测试修改密码后刷新令牌是否失效
   - 测试管理员禁用用户后刷新令牌是否失效
   - 测试从不同IP地址或设备使用刷新令牌
