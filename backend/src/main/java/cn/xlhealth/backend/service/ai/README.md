# AI服务模块

## 概述

AI服务模块为XLHealth心理健康平台提供智能对话和心理咨询支持。该模块采用模块化设计，支持多种AI服务提供商，并提供完整的质量评估、监控和降级机制。

## 架构设计

### 核心组件

```
AI服务模块
├── AIService (接口)
│   └── MockAIService (模拟实现)
├── AIServiceManager (服务管理器)
├── ContextManager (上下文管理)
├── QualityAssessor (质量评估)
├── ServiceMonitor (服务监控)
└── AIController (REST接口)
```

### 组件职责

- **AIService**: 核心AI服务接口，定义生成回复的标准方法
- **AIServiceManager**: 协调各组件，处理完整的请求流程
- **ContextManager**: 管理对话历史和上下文信息
- **QualityAssessor**: 评估AI回复的质量和安全性
- **ServiceMonitor**: 监控服务性能和健康状态
- **AIController**: 提供HTTP REST接口

## 功能特性

### 1. 智能对话
- 支持多轮对话
- 上下文感知
- 情感状态识别
- 个性化回复

### 2. 质量保证
- 多维度质量评估
  - 相关性评估
  - 专业性检查
  - 情感适宜性
  - 安全性检查
  - 完整性评估
- 质量阈值控制
- 自动降级机制

### 3. 服务监控
- 实时性能监控
- 健康状态检查
- 错误率统计
- 响应时间分析

### 4. 上下文管理
- 对话历史存储
- 智能上下文摘要
- 消息权重计算
- 缓存优化

## 使用指南

### 1. 基本配置

在 `application-ai.yml` 中配置AI服务：

```yaml
ai:
  service:
    mock-mode: true
    provider: MOCK
    context:
      enabled: true
      max-messages: 20
    quality:
      enabled: true
      min-score: 60
    monitoring:
      enabled: true
    fallback:
      enabled: true
```

### 2. API使用

#### 发送消息

```http
POST /api/ai/chat
Content-Type: application/json
X-User-Id: user123

{
  "conversationId": "conv-456",
  "message": "我感到很焦虑，该怎么办？",
  "emotionalState": "焦虑"
}
```

#### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "我理解您的焦虑感受...",
    "qualityScore": 85,
    "replyType": "EMOTIONAL_SUPPORT",
    "success": true,
    "provider": "MOCK",
    "responseTime": 1200,
    "timestamp": "2024-01-15T10:30:00"
  }
}
```

#### 健康检查

```http
GET /api/ai/health
```

#### 获取统计信息

```http
GET /api/ai/stats
```

### 3. 编程接口

#### 直接使用AIServiceManager

```java
@Autowired
private AIServiceManager aiServiceManager;

public void handleUserMessage() {
    AIRequest request = new AIRequest();
    request.setUserId("user123");
    request.setConversationId("conv456");
    request.setUserMessage("用户消息");
    
    AIResponse response = aiServiceManager.processRequest(request);
    
    if (response.isSuccess()) {
        System.out.println("AI回复: " + response.getContent());
    }
}
```

## 扩展开发

### 1. 添加新的AI服务提供商

实现 `AIService` 接口：

```java
@Service
public class OpenAIService implements AIService {
    
    @Override
    public AIResponse generateReply(AIRequest request) {
        // 实现OpenAI API调用
        return AIResponse.success(content, "OPENAI", replyType);
    }
    
    @Override
    public boolean isHealthy() {
        // 实现健康检查
        return true;
    }
    
    // 其他方法实现...
}
```

在配置类中注册：

```java
@Bean
@ConditionalOnProperty(name = "ai.service.provider", havingValue = "OPENAI")
public AIService openAIService() {
    return new OpenAIService();
}
```

### 2. 自定义质量评估器

扩展 `QualityAssessor` 接口：

```java
@Service
public class CustomQualityAssessor implements QualityAssessor {
    
    @Override
    public int assessQuality(AIRequest request, AIResponse response) {
        // 自定义质量评估逻辑
        return score;
    }
    
    // 其他方法实现...
}
```

### 3. 自定义上下文管理

实现 `ContextManager` 接口：

```java
@Service
public class RedisContextManager implements ContextManager {
    
    @Override
    public List<ContextMessage> getContext(String userId, String conversationId) {
        // 从Redis获取上下文
        return messages;
    }
    
    // 其他方法实现...
}
```

## 监控和运维

### 1. 健康检查端点

- `/actuator/health` - Spring Boot健康检查
- `/api/ai/health` - AI服务专用健康检查

### 2. 监控指标

- 成功率
- 平均响应时间
- 错误率
- 质量评分分布

### 3. 日志配置

```yaml
logging:
  level:
    cn.xlhealth.backend.service.ai: DEBUG
```

### 4. 性能调优

- 调整上下文缓存大小
- 优化质量评估算法
- 配置合适的线程池
- 设置合理的超时时间

## 安全考虑

### 1. 输入验证
- 消息长度限制
- 特殊字符过滤
- 恶意内容检测

### 2. 输出安全
- 不当内容过滤
- 敏感信息脱敏
- 免责声明添加

### 3. 访问控制
- 用户身份验证
- 请求频率限制
- 权限检查

## 故障排除

### 常见问题

1. **AI服务不可用**
   - 检查配置是否正确
   - 查看健康检查状态
   - 检查网络连接

2. **质量评分过低**
   - 调整质量阈值
   - 检查评估算法
   - 查看具体评估维度

3. **响应时间过长**
   - 检查AI服务性能
   - 优化上下文大小
   - 调整超时设置

### 调试技巧

1. 启用DEBUG日志
2. 使用健康检查接口
3. 查看统计信息
4. 检查配置参数

## 版本历史

- **v1.0.0** - 初始版本，支持Mock AI服务
- 后续版本将支持更多AI服务提供商

## 贡献指南

1. 遵循现有代码风格
2. 添加完整的单元测试
3. 更新相关文档
4. 确保向后兼容性

## 许可证

本项目采用 MIT 许可证。