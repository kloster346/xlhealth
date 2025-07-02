# XLHealth 用户管理接口测试指南

## 概述

本文档提供了 XLHealth AI 心理咨询系统用户管理接口的详细测试指南，包括认证接口和用户管理接口的测试方法。

## 基础信息

- **服务器地址**: `http://localhost:8081`
- **API 前缀**: `/api`
- **认证方式**: Bearer Token (JWT)
- **内容类型**: `application/json`

## 1. 认证接口测试

### 1.1 用户注册

**接口**: `POST /api/auth/register`

**请求体**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "测试用户"
}
```

**预期响应**:
```json
{
  "success": true,
  "message": "用户注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
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
  }
}
```

### 1.2 用户登录

**接口**: `POST /api/auth/login`

**请求体**:
```json
{
  "usernameOrEmail": "testuser",
  "password": "123456"
}
```

**预期响应**: 同注册接口

### 1.3 用户登出

**接口**: `POST /api/auth/logout`

**请求头**: `Authorization: Bearer {token}`

**预期响应**:
```json
{
  "success": true,
  "message": "登出成功",
  "data": null
}
```

## 2. 用户管理接口测试

> **注意**: 以下所有接口都需要在请求头中添加 `Authorization: Bearer {token}`

### 2.1 获取当前用户资料

**接口**: `GET /api/users/profile`

**请求头**: `Authorization: Bearer {token}`

**预期响应**:
```json
{
  "success": true,
  "message": "获取用户资料成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatarUrl": null,
    "status": 1,
    "statusDescription": "活跃",
    "createdTime": "2024-01-01T10:00:00",
    "lastLoginTime": "2024-01-01T10:30:00"
  }
}
```

### 2.2 获取指定用户资料

**接口**: `GET /api/users/{userId}/profile`

**路径参数**: `userId` - 用户ID

**请求头**: `Authorization: Bearer {token}`

**示例**: `GET /api/users/1/profile`

**预期响应**: 同获取当前用户资料

### 2.3 更新当前用户资料

**接口**: `PUT /api/users/profile`

**请求头**: `Authorization: Bearer {token}`

**请求体**:
```json
{
  "email": "newemail@example.com",
  "nickname": "新昵称",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**预期响应**:
```json
{
  "success": true,
  "message": "用户资料更新成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "newemail@example.com",
    "nickname": "新昵称",
    "avatarUrl": "https://example.com/avatar.jpg",
    "status": 1,
    "statusDescription": "活跃",
    "createdTime": "2024-01-01T10:00:00",
    "lastLoginTime": "2024-01-01T10:30:00"
  }
}
```

### 2.4 修改密码

**接口**: `PUT /api/users/password`

**请求头**: `Authorization: Bearer {token}`

**请求体**:
```json
{
  "currentPassword": "123456",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**预期响应**:
```json
{
  "success": true,
  "message": "密码修改成功",
  "data": null
}
```

### 2.5 分页查询用户列表

**接口**: `GET /api/users/list`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:
- `current` (可选): 当前页码，默认 1
- `size` (可选): 每页大小，默认 10
- `keyword` (可选): 搜索关键词（用户名、邮箱、昵称）
- `status` (可选): 状态筛选（0=被禁用, 1=活跃, 2=非活跃）

**示例**: `GET /api/users/list?current=1&size=10&keyword=test&status=1`

**预期响应**:
```json
{
  "success": true,
  "message": "查询用户列表成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "nickname": "测试用户",
        "avatarUrl": null,
        "status": 1,
        "createdTime": "2024-01-01T10:00:00",
        "lastLoginTime": "2024-01-01T10:30:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10,
    "pages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### 2.6 更新用户状态

**接口**: `PUT /api/users/{userId}/status`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `userId` - 用户ID

**查询参数**: `status` - 新状态（0=被禁用, 1=活跃, 2=非活跃）

**示例**: `PUT /api/users/1/status?status=0`

**预期响应**:
```json
{
  "success": true,
  "message": "用户状态更新成功",
  "data": null
}
```

### 2.7 删除用户（软删除）

**接口**: `DELETE /api/users/{userId}`

**请求头**: `Authorization: Bearer {token}`

**路径参数**: `userId` - 用户ID

**示例**: `DELETE /api/users/1`

**预期响应**:
```json
{
  "success": true,
  "message": "用户删除成功",
  "data": null
}
```

## 3. 测试流程建议

### 3.1 基础测试流程

1. **注册新用户** → 获取 token
2. **登录用户** → 验证 token 有效性
3. **获取用户资料** → 验证用户信息
4. **更新用户资料** → 验证更新功能
5. **修改密码** → 验证密码修改
6. **查询用户列表** → 验证分页和搜索
7. **登出用户** → 验证登出功能

### 3.2 边界测试

1. **无效 token 测试**:
   - 不提供 Authorization 头
   - 提供过期的 token
   - 提供格式错误的 token

2. **参数验证测试**:
   - 邮箱格式验证
   - 密码长度验证
   - 必填字段验证

3. **业务逻辑测试**:
   - 重复邮箱注册
   - 错误密码登录
   - 修改密码时当前密码错误
   - 新密码与确认密码不一致

## 4. 错误响应格式

所有错误响应都遵循以下格式：

```json
{
  "success": false,
  "message": "错误描述",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

## 5. 状态码说明

- **200**: 请求成功
- **400**: 请求参数错误
- **401**: 未授权（token 无效或过期）
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

## 6. Apifox 配置建议

### 6.1 环境变量设置

在 Apifox 中设置以下环境变量：

- `baseUrl`: `http://localhost:8080`
- `token`: 登录后获取的 JWT token

### 6.2 全局请求头

为需要认证的接口设置全局请求头：

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 6.3 前置脚本

可以在登录接口的后置脚本中自动保存 token：

```javascript
// 登录接口后置脚本
if (pm.response.json().success) {
    pm.environment.set("token", pm.response.json().data.token);
}
```

## 7. 注意事项

1. 确保后端服务已启动并运行在 `http://localhost:8080`
2. 数据库连接正常，用户表已创建
3. 测试前请先注册用户或使用已有用户登录获取 token
4. 某些接口可能需要管理员权限，请根据实际业务需求调整
5. 建议使用不同的测试数据避免数据冲突

## 8. 常见问题

### Q1: 401 Unauthorized 错误
**A**: 检查 Authorization 头是否正确设置，token 是否有效

### Q2: 400 Bad Request 错误
**A**: 检查请求参数格式和必填字段

### Q3: 500 Internal Server Error
**A**: 检查后端日志，可能是数据库连接或业务逻辑错误

### Q4: 跨域问题
**A**: 确保前端请求域名在后端 CORS 配置中允许

---

**测试完成后，请确保所有接口都能正常响应，并且返回的数据格式符合预期。**
