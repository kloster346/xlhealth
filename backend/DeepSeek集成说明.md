# DeepSeek AI 服务集成说明

## 概述

本项目已成功集成 DeepSeek AI 服务，提供真实的 AI 对话能力。DeepSeek 是一个高性能的大语言模型，特别适合心理健康咨询场景。

## 快速开始

### 1. 获取 DeepSeek API 密钥

1. 访问 [DeepSeek 官网](https://www.deepseek.com/)
2. 注册账号并登录
3. 在控制台中创建 API 密钥
4. 复制您的 API 密钥（格式类似：`sk-xxxxxxxxxxxxxxxxxx`）

### 2. 配置 API 密钥

#### 方法一：环境变量（推荐）

在系统环境变量中设置：
```bash
DEEPSEEK_API_KEY=sk-your-actual-api-key-here
```

#### 方法二：直接修改配置文件

编辑 `application-ai.yml` 文件：
```yaml
ai:
  service:
    deepseek:
      api-key: sk-your-actual-api-key-here
```

### 3. 启用 DeepSeek 服务

修改 `application-ai.yml` 配置：
```yaml
ai:
  service:
    # 关闭模拟模式
    mock-mode: false
    # 设置提供商为 DeepSeek
    provider: DEEPSEEK
```

### 4. 重启应用

重启 Spring Boot 应用，DeepSeek AI 服务将自动生效。

## 配置参数说明

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `api-key` | 无 | DeepSeek API 密钥（必填） |
| `api-base` | `https://api.deepseek.com` | API 基础 URL |
| `model` | `deepseek-chat` | 使用的模型名称 |
| `max-tokens` | `2048` | 单次回复最大 token 数 |
| `temperature` | `0.7` | 创造性参数（0.0-2.0） |
| `timeout` | `30000` | 请求超时时间（毫秒） |

## 使用示例

### API 调用示例

```bash
# 测试 AI 服务
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "我最近感到很焦虑，该怎么办？",
    "conversationId": 1
  }'
```

### 预期响应

```json
{
  "success": true,
  "data": {
    "message": "我理解您现在的感受，焦虑确实会让人感到不适...",
    "provider": "DeepSeek",
    "model": "deepseek-chat",
    "timestamp": "2024-01-20T10:30:00",
    "success": true,
    "metadata": {
      "prompt_tokens": 45,
      "completion_tokens": 128,
      "total_tokens": 173
    }
  }
}
```

## 健康检查

### 检查服务状态

```bash
# 检查 AI 服务健康状态
curl http://localhost:8080/api/ai/health
```

### 查看配置信息

```bash
# 查看当前 AI 服务配置
curl http://localhost:8080/api/ai/config
```

## 故障排除

### 常见问题

1. **API 密钥错误**
   - 错误信息：`DeepSeek API key is not configured`
   - 解决方案：检查环境变量或配置文件中的 API 密钥

2. **网络连接问题**
   - 错误信息：`Failed to generate reply: Connection timeout`
   - 解决方案：检查网络连接，确保可以访问 `api.deepseek.com`

3. **配额不足**
   - 错误信息：`API quota exceeded`
   - 解决方案：检查 DeepSeek 账户余额和使用配额

### 日志查看

查看应用日志以获取详细错误信息：
```bash
tail -f logs/application.log | grep DeepSeek
```

## 性能优化建议

1. **调整 temperature 参数**
   - 较低值（0.1-0.3）：更一致、专业的回复
   - 较高值（0.7-1.0）：更有创造性的回复

2. **优化 max-tokens**
   - 根据实际需求调整，避免不必要的 token 消耗

3. **设置合理的超时时间**
   - 平衡响应速度和成功率

## 安全注意事项

1. **保护 API 密钥**
   - 不要将 API 密钥提交到版本控制系统
   - 使用环境变量或安全的配置管理工具

2. **监控使用量**
   - 定期检查 API 使用量和费用
   - 设置合理的使用限制

3. **数据隐私**
   - 确保用户数据的安全传输和处理
   - 遵守相关的数据保护法规

## 从 Mock 服务迁移

如果您之前使用的是 Mock 服务，迁移到 DeepSeek 非常简单：

1. 获取并配置 DeepSeek API 密钥
2. 修改配置文件：
   ```yaml
   ai:
     service:
       mock-mode: false  # 关闭模拟模式
       provider: DEEPSEEK  # 切换到 DeepSeek
   ```
3. 重启应用

所有现有的 API 接口保持不变，无需修改客户端代码。

## 技术支持

如果您在集成过程中遇到问题，可以：

1. 查看应用日志获取详细错误信息
2. 检查 DeepSeek 官方文档
3. 联系技术支持团队

---

**注意**：请确保您有足够的 DeepSeek API 配额来支持您的应用需求。建议在生产环境中设置适当的监控和告警机制。