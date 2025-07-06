# XLHealth AI 心理咨询系统 - 后端服务

## 项目简介

XLHealth AI 心理咨询系统是一个专业的AI驱动心理健康平台，致力于为用户提供智能化、个性化的心理咨询服务。后端服务基于 Spring Boot 3.2.0 构建，采用现代化的微服务架构设计，提供完整的用户管理、智能对话、消息处理、AI 集成、质量评估和服务监控等核心功能。

### 核心特性

- **🤖 智能AI对话**: 集成先进的AI模型，提供专业的心理咨询对话服务
- **🔐 安全认证**: 基于JWT的用户认证和授权机制，确保数据安全
- **💬 多轮对话**: 支持上下文感知的多轮对话，提供连贯的咨询体验
- **📊 质量保证**: 多维度AI回复质量评估，确保专业性和安全性
- **📈 实时监控**: 完整的服务监控和健康检查机制
- **🗄️ 数据管理**: 完善的用户数据、对话历史和系统配置管理
- **🔄 数据库迁移**: 基于Flyway的数据库版本控制和迁移
- **📝 审计日志**: 完整的操作审计和日志记录

## 技术架构

### 核心框架
- **Spring Boot 3.2.0** - 主框架，提供自动配置和快速开发能力
- **Java 17** - 开发语言，支持最新的语言特性
- **Maven** - 项目构建和依赖管理工具
- **MyBatis Plus 3.5.5** - 增强版ORM框架，简化数据库操作

### 数据库技术
- **MySQL 8.0+** - 主数据库，支持JSON字段和高性能查询
- **HikariCP** - 高性能数据库连接池（Spring Boot 默认）
- **Flyway** - 数据库版本控制和迁移工具

### 安全与认证
- **Spring Security** - 安全框架，提供认证和授权
- **JWT (jjwt 0.11.5)** - JSON Web Token，用于无状态身份认证
- **BCrypt** - 密码加密算法

### 核心依赖
- **FastJSON 1.2.35** - 高性能JSON处理库
- **MySQL Connector/J** - MySQL数据库驱动
- **JAXB API 2.3.1** - XML绑定支持
- **Lombok** - 代码生成工具，减少样板代码
- **Spring Boot Actuator** - 生产就绪功能，提供监控和管理
- **SpringDoc OpenAPI** - API文档生成工具（Swagger）
- **Spring Boot Validation** - 数据验证框架

### AI服务架构
- **模块化设计** - 支持多种AI服务提供商
- **质量评估系统** - 多维度AI回复质量检测
- **服务监控** - 实时性能监控和健康检查
- **上下文管理** - 智能对话历史管理
- **降级机制** - 服务故障时的自动降级处理

## 项目结构

```
backend/
├── src/main/java/cn/xlhealth/backend/
│   ├── BackendApplication.java          # 主启动类
│   ├── common/                          # 公共组件
│   │   └── ErrorCode.java               # 错误码定义
│   ├── config/                          # 配置类
│   │   ├── AIServiceConfig.java         # AI服务配置
│   │   ├── JwtUtils.java                # JWT工具类
│   │   ├── MyBatisPlusConfig.java       # MyBatis Plus配置
│   │   ├── SecurityConfig.java          # Spring Security配置
│   │   ├── WebConfig.java               # Web配置
│   │   └── properties/                  # 配置属性类
│   │       ├── AIServiceProperties.java # AI服务属性配置
│   │       └── FileUploadProperties.java # 文件上传属性配置
│   ├── entity/                          # 实体类（对应数据库表）
│   │   ├── AuditLog.java                # 审计日志实体
│   │   ├── Conversation.java            # 对话实体
│   │   ├── Message.java                 # 消息实体
│   │   ├── SystemConfig.java            # 系统配置实体
│   │   ├── User.java                    # 用户实体
│   │   └── UserSession.java             # 用户会话实体
│   ├── mapper/                          # 数据访问层（MyBatis Plus）
│   │   ├── AuditLogMapper.java          # 审计日志数据访问
│   │   ├── ConversationMapper.java      # 对话数据访问
│   │   ├── MessageMapper.java           # 消息数据访问
│   │   ├── SystemConfigMapper.java      # 系统配置数据访问
│   │   ├── UserMapper.java              # 用户数据访问
│   │   └── UserSessionMapper.java       # 用户会话数据访问
│   ├── service/                         # 业务逻辑层
│   │   ├── ConversationService.java     # 对话服务接口
│   │   ├── FileStorageService.java      # 文件存储服务接口
│   │   ├── MessageService.java          # 消息服务接口
│   │   ├── UserService.java             # 用户服务接口
│   │   ├── UserSessionService.java      # 用户会话服务接口
│   │   ├── ai/                          # AI服务模块
│   │   │   ├── AIService.java           # AI服务接口
│   │   │   ├── AIServiceManager.java    # AI服务管理器
│   │   │   ├── README.md                # AI服务模块说明文档
│   │   │   ├── context/                 # 上下文管理
│   │   │   │   ├── ContextManager.java  # 上下文管理器接口
│   │   │   │   └── impl/                # 上下文管理实现
│   │   │   │       └── ContextManagerImpl.java # 上下文管理器实现
│   │   │   ├── demo/                    # AI服务演示
│   │   │   │   └── AIServiceDemo.java   # AI服务演示类
│   │   │   ├── dto/                     # AI服务数据传输对象
│   │   │   │   ├── AIRequest.java       # AI请求对象
│   │   │   │   ├── AIResponse.java      # AI响应对象
│   │   │   │   └── ContextMessage.java  # 上下文消息对象
│   │   │   ├── exception/               # AI服务异常
│   │   │   │   └── AIServiceException.java # AI服务异常类
│   │   │   ├── health/                  # 健康检查
│   │   │   │   └── AIServiceHealthIndicator.java # AI服务健康指示器
│   │   │   ├── impl/                    # AI服务实现
│   │   │   │   └── MockAIService.java   # 模拟AI服务实现
│   │   │   ├── monitor/                 # 服务监控
│   │   │   │   ├── ServiceMonitor.java  # 服务监控接口
│   │   │   │   ├── ServiceStats.java    # 服务统计数据
│   │   │   │   └── impl/                # 监控实现
│   │   │   │       └── ServiceMonitorImpl.java # 服务监控实现
│   │   │   └── quality/                 # 质量评估
│   │   │       ├── QualityAssessor.java # 质量评估器接口
│   │   │       └── impl/                # 质量评估实现
│   │   │           └── QualityAssessorImpl.java # 质量评估器实现
│   │   └── impl/                        # 服务实现类
│   │       ├── ConversationServiceImpl.java # 对话服务实现
│   │       ├── FileStorageServiceImpl.java # 文件存储服务实现
│   │       ├── MessageServiceImpl.java  # 消息服务实现
│   │       ├── UserDetailsServiceImpl.java # 用户详情服务实现
│   │       ├── UserServiceImpl.java     # 用户服务实现
│   │       └── UserSessionServiceImpl.java # 用户会话服务实现
│   ├── ui/                              # 控制层（用户界面层）
│   │   ├── advice/                      # 全局异常处理
│   │   │   ├── BusinessException.java   # 业务异常类
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   ├── controller/                  # REST控制器
│   │   │   ├── AIController.java        # AI服务控制器
│   │   │   ├── AuthController.java      # 认证控制器
│   │   │   ├── ConversationController.java # 对话控制器
│   │   │   ├── HealthController.java    # 健康检查控制器
│   │   │   ├── MessageController.java   # 消息控制器
│   │   │   └── UserController.java      # 用户控制器
│   │   ├── dto/                         # 控制层数据传输对象
│   │   │   ├── ApiResponse.java         # 统一API响应对象
│   │   │   ├── AuthResponse.java        # 认证响应对象
│   │   │   ├── ChangePasswordRequest.java # 修改密码请求对象
│   │   │   ├── ConversationCreateRequest.java # 创建对话请求对象
│   │   │   ├── ConversationResponse.java # 对话响应对象
│   │   │   ├── ConversationUpdateRequest.java # 更新对话请求对象
│   │   │   ├── LoginRequest.java        # 登录请求对象
│   │   │   ├── MessageListRequest.java  # 消息列表请求对象
│   │   │   ├── MessageResponse.java     # 消息响应对象
│   │   │   ├── MessageSendRequest.java  # 发送消息请求对象
│   │   │   ├── MessageStatisticsResponse.java # 消息统计响应对象
│   │   │   ├── PageResponse.java        # 分页响应对象
│   │   │   ├── RegisterRequest.java     # 注册请求对象
│   │   │   ├── UpdateUserProfileRequest.java # 更新用户资料请求对象
│   │   │   ├── UserProfileDTO.java      # 用户资料数据传输对象
│   │   │   └── request/                 # 请求对象子包
│   │   │       ├── AIReplyRequest.java  # AI回复请求对象
│   │   │       ├── BatchDeleteMessageRequest.java # 批量删除消息请求对象
│   │   │       └── MessageStatusUpdateRequest.java # 消息状态更新请求对象
│   │   └── interceptor/                 # 拦截器
│   │       ├── JwtAuthenticationEntryPoint.java # JWT认证入口点
│   │       └── JwtRequestFilter.java    # JWT请求过滤器
│   └── util/                            # 工具类
│       └── EntityConverter.java         # 实体转换工具
├── src/main/resources/
│   ├── application.yml                  # 主配置文件
│   ├── application-ai.yml               # AI服务配置文件
│   ├── db/migration/                    # 数据库迁移脚本
│   │   ├── V1__Create_initial_tables.sql # 初始表结构
│   │   ├── V2__Insert_initial_data.sql  # 初始数据
│   │   ├── V3__Add_missing_fields.sql   # 添加缺失字段
│   │   ├── V4__Fix_enum_fields.sql      # 修复枚举字段
│   │   └── V5__Complete_enum_migration.sql # 完成枚举迁移
│   ├── static/                          # 静态资源
│   │   └── ai-demo.html                 # AI演示页面
│   └── templates/                       # 模板文件
├── src/test/java/                       # 测试代码
│   └── cn/xlhealth/backend/             # 测试包结构
├── pom.xml                              # Maven 配置文件
└── README.md                            # 项目说明文档
```

## 环境要求

### 开发环境
- **JDK 17** 或更高版本
- **Maven 3.6+**
- **MySQL 8.0+**
- **IDE**: IntelliJ IDEA 或 Eclipse

### 运行环境
- **JRE 17** 或更高版本
- **MySQL 8.0+**
- **内存**: 最小 512MB，推荐 1GB+

## 数据库设计

### 🗄️ 核心表结构

#### 👤 用户表 (users)
| 字段名 | 类型 | 长度 | 约束 | 描述 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 用户唯一标识 |
| username | VARCHAR | 50 | UNIQUE, NOT NULL | 用户名，3-50字符，字母数字下划线 |
| email | VARCHAR | 100 | UNIQUE, NOT NULL | 邮箱地址 |
| password_hash | VARCHAR | 255 | NOT NULL | 密码哈希值（BCrypt加密） |
| phone | VARCHAR | 20 | NULL | 手机号 |
| nickname | VARCHAR | 50 | NULL | 用户昵称 |
| avatar_url | VARCHAR | 500 | NULL | 头像URL |
| gender | ENUM | - | DEFAULT 'OTHER' | 性别：MALE, FEMALE, OTHER |
| birth_date | DATE | - | NULL | 出生日期 |
| profile | TEXT | - | NULL | 个人简介 |
| status | ENUM | - | NOT NULL, DEFAULT 'ACTIVE' | 用户状态：ACTIVE, INACTIVE, SUSPENDED, DELETED |
| last_login_time | DATETIME | - | NULL | 最后登录时间 |
| last_login_ip | VARCHAR | 45 | NULL | 最后登录IP |
| created_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 逻辑删除：0-未删除, 1-已删除 |

**索引：**
- `idx_username` (username)
- `idx_email` (email)
- `idx_phone` (phone)
- `idx_status` (status)
- `idx_created_time` (created_time)

#### 💬 对话表 (conversations)
| 字段名 | 类型 | 长度 | 约束 | 描述 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 对话唯一标识 |
| user_id | BIGINT | - | NOT NULL, FK | 用户ID，外键关联users.id |
| title | VARCHAR | 200 | NULL | 对话标题 |
| summary | TEXT | - | NULL | 对话摘要 |
| status | ENUM | - | NOT NULL, DEFAULT 'ACTIVE' | 对话状态：ACTIVE, ARCHIVED, DELETED |
| message_count | INT | - | DEFAULT 0 | 消息数量统计 |
| total_tokens | INT | - | DEFAULT 0 | 总Token消耗 |
| start_time | DATETIME | - | NULL | 对话开始时间 |
| end_time | DATETIME | - | NULL | 对话结束时间 |
| created_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 逻辑删除：0-未删除, 1-已删除 |
| metadata | JSON | - | NULL | 对话元数据（标签、优先级等） |
| last_message_at | DATETIME | - | NULL | 最后消息时间 |

**索引：**
- `idx_user_id` (user_id)
- `idx_status` (status)
- `idx_created_time` (created_time)
- `idx_start_time` (start_time)
- `idx_last_message_at` (last_message_at)

#### 📝 消息表 (messages)
| 字段名 | 类型 | 长度 | 约束 | 描述 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 消息唯一标识 |
| conversation_id | BIGINT | - | NOT NULL, FK | 对话ID，外键关联conversations.id |
| user_id | BIGINT | - | NOT NULL, FK | 用户ID，外键关联users.id |
| role | ENUM | - | NOT NULL | 消息角色：USER, ASSISTANT, SYSTEM |
| content | TEXT | - | NOT NULL | 消息内容 |
| content_type | ENUM | - | NOT NULL, DEFAULT 'TEXT' | 内容类型：TEXT, IMAGE, FILE |
| metadata | JSON | - | NULL | 消息元数据（情感状态、质量评分等） |
| token_count | INT | - | DEFAULT 0 | 消息Token数量 |
| model_name | VARCHAR | 100 | NULL | 使用的AI模型名称 |
| prompt_tokens | INT | - | DEFAULT 0 | 提示Token数量 |
| completion_tokens | INT | - | DEFAULT 0 | 完成Token数量 |
| total_tokens | INT | - | DEFAULT 0 | 总Token数量 |
| response_time | INT | - | DEFAULT 0 | AI响应时间（毫秒） |
| status | ENUM | - | NOT NULL, DEFAULT 'SUCCESS' | 消息状态：PROCESSING, SUCCESS, FAILED |
| error_message | TEXT | - | NULL | 错误信息 |
| created_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 逻辑删除：0-未删除, 1-已删除 |

**索引：**
- `idx_conversation_id` (conversation_id)
- `idx_user_id` (user_id)
- `idx_role` (role)
- `idx_created_time` (created_time)
- `idx_status` (status)

#### 🔐 用户会话表 (user_sessions)
| 字段名 | 类型 | 长度 | 约束 | 描述 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 会话唯一标识 |
| user_id | BIGINT | - | NOT NULL, FK | 用户ID，外键关联users.id |
| session_token | VARCHAR | 255 | UNIQUE, NOT NULL | 会话令牌 |
| refresh_token | VARCHAR | 255 | NULL | 刷新令牌 |
| device_info | VARCHAR | 500 | NULL | 设备信息 |
| ip_address | VARCHAR | 45 | NULL | 客户端IP地址 |
| user_agent | TEXT | - | NULL | 用户代理信息 |
| location | VARCHAR | 200 | NULL | 地理位置 |
| status | ENUM | - | NOT NULL, DEFAULT 'ACTIVE' | 会话状态：ACTIVE, EXPIRED, INVALID |
| expires_at | DATETIME | - | NOT NULL | 过期时间 |
| last_activity_time | DATETIME | - | NULL | 最后活动时间 |
| created_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 逻辑删除：0-未删除, 1-已删除 |

**索引：**
- `idx_user_id` (user_id)
- `idx_session_token` (session_token)
- `idx_status` (status)
- `idx_expires_at` (expires_at)
- `idx_last_activity_time` (last_activity_time)

#### ⚙️ 系统配置表 (system_configs)
| 字段名 | 类型 | 长度 | 约束 | 描述 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 配置唯一标识 |
| config_key | VARCHAR | 100 | UNIQUE, NOT NULL | 配置键名 |
| config_value | TEXT | - | NULL | 配置值 |
| config_type | ENUM | - | NOT NULL, DEFAULT 'STRING' | 配置类型：STRING, INTEGER, BOOLEAN, JSON |
| description | VARCHAR | 500 | NULL | 配置描述 |
| category | VARCHAR | 50 | NULL | 配置分类 |
| is_public | TINYINT | - | DEFAULT 0 | 是否公开：0-私有, 1-公开 |
| is_editable | TINYINT | - | DEFAULT 1 | 是否可编辑：0-不可编辑, 1-可编辑 |
| sort_order | INT | - | DEFAULT 0 | 排序顺序 |
| created_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 逻辑删除：0-未删除, 1-已删除 |

**索引：**
- `idx_config_key` (config_key)
- `idx_category` (category)
- `idx_is_public` (is_public)
- `idx_sort_order` (sort_order)

#### 📋 审计日志表 (audit_logs)
| 字段名 | 类型 | 长度 | 约束 | 描述 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 日志唯一标识 |
| user_id | BIGINT | - | NULL, FK | 用户ID，外键关联users.id |
| action | VARCHAR | 100 | NOT NULL | 操作类型 |
| resource_type | VARCHAR | 50 | NULL | 资源类型 |
| resource_id | VARCHAR | 100 | NULL | 资源ID |
| old_values | JSON | - | NULL | 操作前的值 |
| new_values | JSON | - | NULL | 操作后的值 |
| ip_address | VARCHAR | 45 | NULL | 客户端IP地址 |
| user_agent | TEXT | - | NULL | 用户代理信息 |
| request_method | VARCHAR | 10 | NULL | 请求方法 |
| request_url | VARCHAR | 500 | NULL | 请求URL |
| request_params | JSON | - | NULL | 请求参数 |
| response_status | INT | - | NULL | 响应状态码 |
| execution_time | INT | - | NULL | 执行时间（毫秒） |
| error_message | TEXT | - | NULL | 错误信息 |
| created_time | DATETIME | - | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- `idx_user_id` (user_id)
- `idx_action` (action)
- `idx_resource_type` (resource_type)
- `idx_resource_id` (resource_id)
- `idx_created_time` (created_time)
- `idx_ip_address` (ip_address)

### 🔗 表关系
- `conversations.user_id` → `users.id` (多对一)
- `messages.conversation_id` → `conversations.id` (多对一)
- `messages.user_id` → `users.id` (多对一)
- `user_sessions.user_id` → `users.id` (多对一)
- `audit_logs.user_id` → `users.id` (多对一)

### 📊 数据库迁移
项目使用Flyway进行数据库版本管理，迁移脚本位于 `src/main/resources/db/migration/`：

- `V1__Create_initial_tables.sql` - 创建初始表结构
- `V2__Insert_initial_data.sql` - 插入初始数据
- `V3__Add_missing_fields.sql` - 添加缺失字段
- `V4__Fix_enum_fields.sql` - 修复枚举字段
- `V5__Complete_enum_migration.sql` - 完成枚举迁移

### 🎯 性能优化
1. **索引策略**：为常用查询字段创建合适索引
2. **分页查询**：使用LIMIT和OFFSET进行分页
3. **软删除**：使用deleted字段标记删除，避免物理删除
4. **JSON字段**：使用JSON类型存储元数据，提高查询灵活性
5. **连接池**：使用HikariCP连接池优化数据库连接

## 开发规范

### 📝 代码规范

#### Java代码规范
- **命名规范**：
  - 类名：大驼峰命名法 (PascalCase)
  - 方法名：小驼峰命名法 (camelCase)
  - 常量：全大写下划线分隔 (UPPER_SNAKE_CASE)
  - 包名：全小写，使用点分隔

- **注释规范**：
  - 类和接口必须有JavaDoc注释
  - 公共方法必须有JavaDoc注释
  - 复杂业务逻辑必须有行内注释

```java
/**
 * AI服务管理器
 * 负责管理多个AI服务提供商，提供统一的服务接口
 * 
 * @author XLHealth Team
 * @since 1.0.0
 */
@Service
public class AIServiceManager {
    
    /**
     * 发送消息到AI服务
     * 
     * @param request 消息请求对象
     * @return AI响应结果
     * @throws AIServiceException AI服务异常
     */
    public AIResponse sendMessage(AIRequest request) throws AIServiceException {
        // 实现逻辑
    }
}
```

#### 数据库规范
- **表名**：小写下划线分隔 (snake_case)
- **字段名**：小写下划线分隔 (snake_case)
- **索引名**：`idx_` + 字段名
- **外键名**：`fk_` + 表名 + 字段名

#### API设计规范
- **RESTful风格**：使用标准HTTP方法
- **URL命名**：小写，使用连字符分隔
- **响应格式**：统一使用ApiResponse包装
- **错误处理**：使用标准HTTP状态码

```java
// 正确示例
@GetMapping("/conversations/{id}/messages")
public ApiResponse<List<MessageDTO>> getMessages(@PathVariable Long id) {
    // 实现逻辑
}

// 错误示例
@GetMapping("/getConversationMessages")
public List<MessageDTO> getConversationMessages(@RequestParam Long conversationId) {
    // 不推荐的写法
}
```

### 🔄 Git工作流

#### 分支策略
- **main**: 主分支，用于生产环境
- **develop**: 开发分支，用于集成测试
- **feature/***: 功能分支，用于新功能开发
- **hotfix/***: 热修复分支，用于紧急修复
- **release/***: 发布分支，用于版本发布

#### 提交规范
```bash
# 提交格式
<type>(<scope>): <subject>

# 类型说明
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动

# 示例
feat(ai): 添加AI服务质量评估功能
fix(auth): 修复JWT令牌过期时间计算错误
docs(readme): 更新API文档说明
```

### 🧪 测试规范

#### 单元测试
- 使用JUnit 5 + Mockito
- 测试覆盖率要求：核心业务逻辑 > 80%
- 测试类命名：`被测试类名 + Test`

```java
@ExtendWith(MockitoExtension.class)
class AIServiceManagerTest {
    
    @Mock
    private AIProvider mockProvider;
    
    @InjectMocks
    private AIServiceManager aiServiceManager;
    
    @Test
    @DisplayName("发送消息成功测试")
    void testSendMessage_Success() {
        // Given
        AIRequest request = new AIRequest("Hello");
        AIResponse expectedResponse = new AIResponse("Hi there!");
        when(mockProvider.sendMessage(request)).thenReturn(expectedResponse);
        
        // When
        AIResponse actualResponse = aiServiceManager.sendMessage(request);
        
        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(mockProvider).sendMessage(request);
    }
}
```

#### 集成测试
- 使用@SpringBootTest
- 使用TestContainers进行数据库测试
- 测试真实的HTTP请求响应

### 📦 依赖管理
- 使用Maven进行依赖管理
- 定期更新依赖版本，关注安全漏洞
- 避免引入不必要的依赖
- 使用dependencyManagement统一版本管理

## 故障排除

### 🚨 常见问题

#### 1. 数据库连接问题
**问题现象**：
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**解决方案**：
1. 检查MySQL服务是否启动
2. 验证数据库连接配置
3. 检查防火墙设置
4. 确认数据库用户权限

```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 测试数据库连接
mysql -h localhost -u xlhealth -p xlhealth

# 检查端口是否开放
telnet localhost 3306
```

#### 2. JWT令牌问题
**问题现象**：
```
401 Unauthorized: JWT token is expired
```

**解决方案**：
1. 检查系统时间是否正确
2. 验证JWT密钥配置
3. 确认令牌过期时间设置

```yaml
# 检查JWT配置
jwt:
  secret: ${JWT_SECRET:your-secret-key-here}
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
```

#### 3. AI服务调用失败
**问题现象**：
```
AIServiceException: Failed to call AI service
```

**解决方案**：
1. 检查API密钥是否正确
2. 验证网络连接
3. 查看AI服务配置
4. 检查请求频率限制

```bash
# 测试AI服务连接
curl -H "Authorization: Bearer $OPENAI_API_KEY" \
     https://api.openai.com/v1/models

# 检查AI服务健康状态
curl http://localhost:8080/api/ai/health
```

#### 4. 文件上传问题
**问题现象**：
```
MaxUploadSizeExceededException: Maximum upload size exceeded
```

**解决方案**：
1. 检查文件大小限制配置
2. 确认磁盘空间充足
3. 验证文件类型是否允许

```yaml
# 调整文件上传配置
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

file:
  upload:
    max-size: 10485760
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx
```

### 📊 性能优化

#### 1. 数据库优化
- **索引优化**：为常用查询字段添加索引
- **查询优化**：避免N+1查询，使用批量查询
- **连接池优化**：调整HikariCP参数

```yaml
# HikariCP优化配置
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

#### 2. 缓存策略
- 使用Redis缓存热点数据
- 实现多级缓存架构
- 合理设置缓存过期时间

#### 3. AI服务优化
- 实现请求去重
- 使用连接池管理HTTP连接
- 实现智能重试机制
- 添加熔断器防止雪崩

### 🔍 监控和日志

#### 应用监控
```bash
# 查看应用状态
curl http://localhost:8080/actuator/health

# 查看指标信息
curl http://localhost:8080/actuator/metrics

# 查看AI服务统计
curl http://localhost:8080/api/ai/stats
```

#### 日志分析
```bash
# 查看应用日志
tail -f /app/logs/application.log

# 查看错误日志
grep "ERROR" /app/logs/application.log

# 查看AI服务调用日志
grep "AIService" /app/logs/application.log
```

## 安全配置

### 🔐 认证和授权

#### JWT安全配置
```yaml
# 使用强密钥
jwt:
  secret: ${JWT_SECRET}  # 至少32位随机字符串
  expiration: 86400000   # 24小时
  refresh-expiration: 604800000  # 7天
```

#### 密码安全
- 使用BCrypt加密存储密码
- 密码强度要求：至少8位，包含大小写字母、数字和特殊字符
- 实现密码重试限制

```java
@Component
public class PasswordEncoder {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 🛡️ 数据安全

#### 敏感数据保护
- API密钥使用环境变量存储
- 数据库密码加密存储
- 用户隐私数据脱敏处理

#### SQL注入防护
- 使用MyBatis Plus参数化查询
- 禁用动态SQL拼接
- 输入参数验证和过滤

```java
// 安全的查询方式
@Select("SELECT * FROM users WHERE username = #{username}")
User findByUsername(@Param("username") String username);

// 危险的查询方式（禁止使用）
// String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

### 🌐 网络安全

#### HTTPS配置
```yaml
# SSL配置
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: xlhealth
```

#### CORS配置
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("https://*.xlhealth.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### 🔒 访问控制

#### API访问限制
- 实现请求频率限制
- IP白名单/黑名单
- 用户权限分级管理

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        String clientIp = getClientIp(request);
        String key = "rate_limit:" + clientIp;
        
        // 检查请求频率
        if (isRateLimited(key)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        
        return true;
    }
}
```

### 📋 安全检查清单

- [ ] JWT密钥使用强随机字符串
- [ ] 数据库密码定期更换
- [ ] API密钥安全存储
- [ ] 启用HTTPS加密传输
- [ ] 实现请求频率限制
- [ ] 配置CORS策略
- [ ] 启用SQL注入防护
- [ ] 实现访问日志记录
- [ ] 定期安全漏洞扫描
- [ ] 备份数据加密存储

## 快速启动

### 1. 数据库配置

创建数据库：
```sql
CREATE DATABASE xlhealth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

配置数据库连接（`src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xlhealth?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
```

### 2. 启动后端服务

#### 方式一：IDE 启动
1. 导入项目到 IDE
2. 运行 `BackendApplication.java` 主类

#### 方式二：命令行启动
```bash
# 进入项目目录
cd backend

# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

#### 方式三：打包运行
```bash
# 打包项目
mvn clean package

# 运行 JAR 文件
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 3. 验证启动

服务启动后，访问以下地址验证：
- **服务地址**: http://localhost:8081
- **健康检查**: http://localhost:8081/actuator/health（需要添加 actuator 依赖）

## 开发指南

### 代码规范
- 遵循阿里巴巴 Java 开发规范
- 使用驼峰命名法
- 类名使用大驼峰，方法名和变量名使用小驼峰
- 常量使用全大写，单词间用下划线分隔

### 包结构说明
- `config`: 配置类，如数据库配置、安全配置等
- `entity`: 实体类，对应数据库表结构
- `mapper`: 数据访问层，MyBatis Plus 接口
- `service`: 业务逻辑层接口
- `service.impl`: 业务逻辑层实现
- `ui.controller`: 控制器，处理 HTTP 请求
- `ui.dto`: 数据传输对象，用于 API 交互
- `ui.advice`: 全局异常处理
- `ui.interceptor`: 拦截器，如认证拦截器

### 开发流程
1. 根据需求设计数据库表结构
2. 创建对应的实体类（Entity）
3. 开发数据访问层（Mapper）
4. 实现业务逻辑层（Service）
5. 开发控制器（Controller）
6. 编写单元测试
7. 集成测试验证

## API 接口文档

### 🔐 用户认证模块
| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| `/api/v1/auth/login` | POST | 用户登录获取访问令牌 | ❌ |
| `/api/v1/auth/register` | POST | 新用户注册账号 | ❌ |
| `/api/v1/auth/logout` | POST | 用户登出，使当前令牌失效 | ✅ |
| `/api/v1/auth/refresh` | POST | 刷新访问令牌 | ✅ |
| `/api/v1/auth/validate` | GET | 验证当前访问令牌的有效性 | ❌ |

**登录请求示例：**
```json
{
  "usernameOrEmail": "user001",
  "password": "password123"
}
```

**注册请求示例：**
```json
{
  "username": "user001",
  "email": "user@example.com",
  "password": "password123",
  "nickname": "小明"
}
```

### 👤 用户管理模块
| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| `/api/v1/users/profile` | GET | 获取当前登录用户的详细信息 | ✅ |
| `/api/v1/users/profile` | PUT | 更新当前用户信息 | ✅ |
| `/api/v1/users/avatar` | POST | 上传用户头像 | ✅ |

### 💬 对话管理模块
| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| `/api/v1/conversations` | GET | 获取当前用户的对话列表（支持分页） | ✅ |
| `/api/v1/conversations` | POST | 创建一个新的AI对话会话 | ✅ |
| `/api/v1/conversations/{id}` | GET | 获取指定对话的详细信息 | ✅ |
| `/api/v1/conversations/{id}` | PUT | 更新对话信息（如标题） | ✅ |
| `/api/v1/conversations/{id}` | DELETE | 删除指定对话 | ✅ |
| `/api/v1/conversations/{id}/activate` | PUT | 激活对话 | ✅ |
| `/api/v1/conversations/stats` | GET | 获取对话统计信息 | ✅ |
| `/api/v1/conversations/status/{status}` | GET | 按状态获取对话列表 | ✅ |

**创建对话请求示例：**
```json
{
  "title": "新的心理咨询",
  "metadata": {
    "tags": ["anxiety", "stress"],
    "priority": "normal"
  }
}
```

### 📝 消息管理模块
| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| `/api/v1/conversations/{id}/messages` | GET | 获取指定对话的消息列表（支持分页） | ✅ |
| `/api/v1/conversations/{id}/messages` | POST | 向对话发送消息并获取AI回复 | ✅ |
| `/api/v1/conversations/{id}/messages/ai-reply` | POST | 生成AI回复 | ✅ |
| `/api/v1/conversations/{id}/messages/count` | GET | 统计对话消息数量 | ✅ |
| `/api/v1/conversations/{id}/messages/last` | GET | 获取对话最后一条消息 | ✅ |
| `/api/v1/conversations/{id}/messages/clear` | DELETE | 清空对话所有消息 | ✅ |
| `/api/v1/conversations/{id}/messages/statistics` | GET | 获取用户消息统计 | ✅ |
| `/api/v1/conversations/{id}/messages/{messageId}/status` | PUT | 更新消息状态 | ✅ |

**发送消息请求示例：**
```json
{
  "content": "我最近工作压力很大，经常失眠",
  "contentType": "TEXT",
  "metadata": {
    "emotionalState": "anxious",
    "urgency": "normal"
  }
}
```

**AI回复请求示例：**
```json
{
  "message": "你还记得我的名字和职业吗？",
  "emotionalState": "期待",
  "context": {
    "includeHistory": true,
    "historyLimit": 10,
    "includeUserProfile": true,
    "summary": "用户之前提到过工作压力问题"
  },
  "preferences": {
    "preferredType": "EMOTIONAL_SUPPORT",
    "length": "MEDIUM",
    "tone": "WARM",
    "includeAdvice": true,
    "includeQuestions": true
  },
  "parameters": {
    "maxTokens": 500,
    "temperature": 0.7
  }
}
```

### 🤖 AI服务模块
| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| `/api/ai/chat` | POST | AI聊天接口（直接对话） | ✅ |
| `/api/ai/health` | GET | 检查AI服务健康状态 | ✅ |
| `/api/ai/stats` | GET | 获取AI服务统计信息 | ✅ |
| `/api/ai/config` | GET | 获取AI服务配置信息 | ✅ |

**AI聊天请求示例：**
```json
{
  "conversationId": "conv_123",
  "message": "我觉得活着没有意义",
  "emotionalState": "绝望",
  "parameters": {
    "maxTokens": 300,
    "temperature": 0.8
  }
}
```

### 🏥 系统健康检查模块
| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| `/api/v1/health` | GET | 检查服务运行状态 | ❌ |
| `/api/v1/health/version` | GET | 获取应用版本信息 | ❌ |

### 📊 响应格式

**成功响应格式：**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**错误响应格式：**
```json
{
  "code": 10001,
  "message": "请求参数错误",
  "details": "username字段不能为空",
  "timestamp": "2024-01-01T10:00:00Z",
  "path": "/api/v1/users"
}
```

### 🔑 认证说明
- 需要认证的接口请在请求头中添加：`Authorization: Bearer {token}`
- 令牌通过登录接口获取，有效期为24小时
- 可通过刷新接口延长令牌有效期

### 📄 分页参数
- `current`: 页码，从1开始，默认为1
- `size`: 每页数量，默认为20，最大为100
- `sort`: 排序方式，格式为 `字段名,排序方向`（如：`createdAt,desc`）

### 状态码规范
- **200**: 成功
- **400**: 请求参数错误
- **401**: 未认证
- **403**: 无权限
- **404**: 资源不存在
- **500**: 服务器内部错误

## 配置说明

### 应用配置
```yaml
server:
  port: 8081                    # 服务端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xlhealth?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
  
  # MyBatis Plus 配置
  mybatis-plus:
    configuration:
      map-underscore-to-camel-case: true
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    global-config:
      db-config:
        logic-delete-field: deleted
        logic-delete-value: 1
        logic-not-delete-value: 0
```

### 环境配置
- **开发环境**: `application-dev.yml`
- **测试环境**: `application-test.yml`
- **生产环境**: `application-prod.yml`

## 测试

### 单元测试
```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=BackendApplicationTests
```

### 集成测试
```bash
# 运行集成测试
mvn verify
```

## 部署说明

### 🛠️ 环境要求

#### 基础环境
- **Java**: OpenJDK 17+ 或 Oracle JDK 17+
- **MySQL**: 8.0+ (推荐 8.0.33+)
- **Maven**: 3.6+ (推荐 3.9+)
- **操作系统**: Linux/Windows/macOS

#### 硬件要求
- **CPU**: 最小 1核，推荐 2核+
- **内存**: 最小 512MB，推荐 1GB+
- **存储**: 最小 1GB，推荐 5GB+
- **网络**: 稳定的互联网连接（AI服务调用）

### 📋 配置文件说明

#### application.yml (主配置)
```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # API路径前缀

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xlhealth?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20     # 最大连接池大小
      minimum-idle: 5           # 最小空闲连接数
      connection-timeout: 30000 # 连接超时时间
      idle-timeout: 600000      # 空闲超时时间
      max-lifetime: 1800000     # 连接最大生命周期

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

  servlet:
    multipart:
      max-file-size: 10MB       # 单文件最大大小
      max-request-size: 50MB    # 请求最大大小

  jackson:
    time-zone: Asia/Shanghai
    date-format: yyyy-MM-dd HH:mm:ss

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET:your-secret-key-here}
  expiration: 86400000          # 24小时
  refresh-expiration: 604800000 # 7天

# 文件存储配置
file:
  upload:
    path: ${FILE_UPLOAD_PATH:./uploads}
    max-size: 10485760          # 10MB
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx
```

#### application-ai.yml (AI服务配置)
```yaml
# AI服务相关配置
ai:
  service:
    enabled: true
    default-provider: openai
    timeout: 30000              # 30秒超时
    retry:
      max-attempts: 3
      delay: 1000
    
  providers:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key}
      base-url: ${OPENAI_BASE_URL:https://api.openai.com}
      model: gpt-3.5-turbo
      max-tokens: 2048
      temperature: 0.7
    
  quality:
    enabled: true
    min-score: 0.6              # 最低质量分数
    
  monitor:
    enabled: true
    metrics-interval: 60000     # 指标收集间隔
```

### 🚀 部署步骤

#### 1. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE xlhealth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'xlhealth'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON xlhealth.* TO 'xlhealth'@'%';
FLUSH PRIVILEGES;
```

#### 2. 环境变量配置
```bash
# 数据库配置
export DB_USERNAME=xlhealth
export DB_PASSWORD=your_password
export DB_URL=jdbc:mysql://localhost:3306/xlhealth

# JWT配置
export JWT_SECRET=your-very-secure-secret-key-here

# AI服务配置
export OPENAI_API_KEY=your-openai-api-key
export OPENAI_BASE_URL=https://api.openai.com

# 文件存储配置
export FILE_UPLOAD_PATH=/opt/xlhealth/uploads

# 日志配置
export LOG_LEVEL=INFO
export LOG_PATH=/opt/xlhealth/logs
```

#### 3. 构建和运行

**开发环境：**
```bash
# 克隆项目
git clone <repository-url>
cd xlhealth/backend

# 安装依赖
mvn clean install

# 运行应用
mvn spring-boot:run

# 或者使用IDE直接运行 BackendApplication.java
```

**生产环境：**
```bash
# 构建JAR包
mvn clean package -DskipTests

# 运行JAR包
java -jar target/backend-1.0.0.jar

# 或者使用nohup后台运行
nohup java -jar target/backend-1.0.0.jar > app.log 2>&1 &
```

#### 4. Docker部署（推荐）

**Dockerfile:**
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制JAR文件
COPY target/backend-1.0.0.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: xlhealth
      MYSQL_USER: xlhealth
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_USERNAME: xlhealth
      DB_PASSWORD: password
      DB_URL: jdbc:mysql://mysql:3306/xlhealth?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      JWT_SECRET: your-jwt-secret-key
      OPENAI_API_KEY: your-openai-api-key
    depends_on:
      - mysql
    volumes:
      - ./uploads:/app/uploads
      - ./logs:/app/logs

volumes:
  mysql_data:
```

**启动命令：**
```bash
# 构建并启动
docker-compose up -d

# 查看日志
docker-compose logs -f backend

# 停止服务
docker-compose down
```

### 🔧 配置优化

#### JVM参数优化
```bash
java -Xms512m -Xmx1g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/app/logs/ \
     -Dspring.profiles.active=prod \
     -jar backend-1.0.0.jar
```

#### Nginx反向代理配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 文件上传大小限制
        client_max_body_size 50M;
        
        # 超时设置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
    
    # 静态文件服务
    location /uploads/ {
        alias /opt/xlhealth/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

### 📊 监控和健康检查

#### 健康检查端点
- **基础健康检查**: `GET /api/v1/health`
- **详细健康信息**: `GET /actuator/health`
- **应用信息**: `GET /actuator/info`
- **指标监控**: `GET /actuator/metrics`
- **AI服务状态**: `GET /api/ai/health`

#### 日志配置
```yaml
logging:
  level:
    cn.xlhealth.backend: INFO
    org.springframework.security: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /app/logs/application.log
    max-size: 100MB
    max-history: 30
```

### 生产部署
1. 配置生产环境数据库
2. 设置环境变量
3. 配置反向代理（Nginx）
4. 配置日志收集
5. 配置监控告警

## 常见问题

### Q: 启动时数据库连接失败？
A: 检查以下配置：
- 数据库服务是否启动
- 数据库连接信息是否正确
- 数据库用户权限是否足够
- 防火墙是否阻止连接

### Q: JWT Token 验证失败？
A: 检查以下问题：
- Token 是否过期
- Token 格式是否正确
- 密钥配置是否一致
- 请求头是否包含 Authorization

### Q: 接口返回 404 错误？
A: 检查以下配置：
- 控制器路径是否正确
- 请求方法是否匹配
- 参数是否正确传递

## 开发计划

详细的开发任务和进度请参考：[后端开发任务文档](../docs/backend-tasks.md)

## 贡献指南

### 🤝 如何贡献

我们欢迎所有形式的贡献，包括但不限于：
- 🐛 报告Bug
- 💡 提出新功能建议
- 📝 改进文档
- 🔧 提交代码修复
- ✨ 添加新功能

### 📋 贡献流程

1. **Fork项目**
   ```bash
   # Fork项目到你的GitHub账户
   # 克隆你的Fork
   git clone https://github.com/your-username/xlhealth.git
   cd xlhealth/backend
   ```

2. **创建功能分支**
   ```bash
   # 创建并切换到新分支
   git checkout -b feature/your-feature-name
   
   # 或者修复分支
   git checkout -b fix/your-fix-name
   ```

3. **开发和测试**
   ```bash
   # 进行开发
   # 运行测试确保代码质量
   mvn test
   
   # 检查代码覆盖率
   mvn jacoco:report
   ```

4. **提交代码**
   ```bash
   # 添加修改的文件
   git add .
   
   # 提交代码（遵循提交规范）
   git commit -m "feat(ai): 添加新的AI服务提供商支持"
   
   # 推送到你的Fork
   git push origin feature/your-feature-name
   ```

5. **创建Pull Request**
   - 在GitHub上创建Pull Request
   - 填写详细的PR描述
   - 等待代码审查

### 📝 Pull Request指南

#### PR标题格式
```
<type>(<scope>): <description>

# 示例
feat(ai): 添加OpenAI GPT-4支持
fix(auth): 修复JWT令牌刷新逻辑
docs(readme): 更新API文档
```

#### PR描述模板
```markdown
## 变更类型
- [ ] 新功能 (feature)
- [ ] Bug修复 (fix)
- [ ] 文档更新 (docs)
- [ ] 代码重构 (refactor)
- [ ] 性能优化 (perf)
- [ ] 测试相关 (test)

## 变更描述
简要描述本次变更的内容和目的。

## 测试说明
- [ ] 已添加单元测试
- [ ] 已添加集成测试
- [ ] 已进行手动测试
- [ ] 测试覆盖率 > 80%

## 相关Issue
关闭 #issue_number

## 截图（如适用）
如果有UI变更，请提供截图。

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 已添加必要的注释
- [ ] 已更新相关文档
- [ ] 所有测试通过
- [ ] 无安全漏洞
```

### 🔍 代码审查标准

#### 代码质量
- 代码风格符合项目规范
- 变量和方法命名清晰
- 适当的注释和文档
- 无明显的代码异味

#### 功能性
- 功能实现正确
- 边界条件处理
- 错误处理完善
- 性能考虑

#### 测试
- 单元测试覆盖核心逻辑
- 集成测试验证功能
- 测试用例充分
- 测试代码质量

#### 安全性
- 无安全漏洞
- 输入验证
- 权限检查
- 敏感信息保护

### 🐛 Bug报告

使用以下模板报告Bug：

```markdown
## Bug描述
简要描述遇到的问题。

## 复现步骤
1. 进入页面...
2. 点击按钮...
3. 输入数据...
4. 看到错误...

## 预期行为
描述你期望发生的行为。

## 实际行为
描述实际发生的行为。

## 环境信息
- 操作系统: [例如 Windows 10]
- Java版本: [例如 OpenJDK 17]
- 浏览器: [例如 Chrome 91]
- 项目版本: [例如 v1.0.0]

## 错误日志
```
粘贴相关的错误日志
```

## 截图
如果适用，添加截图来帮助解释问题。
```

### 💡 功能建议

使用以下模板提出功能建议：

```markdown
## 功能描述
简要描述建议的功能。

## 问题背景
描述当前存在的问题或需求。

## 解决方案
描述你建议的解决方案。

## 替代方案
描述你考虑过的其他解决方案。

## 附加信息
添加任何其他相关信息或截图。
```

### 🏆 贡献者

感谢所有为项目做出贡献的开发者：

<!-- 这里会自动生成贡献者列表 -->

## 版本历史

### v1.0.0 (2024-01-XX)
- ✨ 初始版本发布
- 🔐 用户认证和授权系统
- 💬 AI智能对话功能
- 📊 对话和消息管理
- 🔍 系统监控和健康检查
- 📝 完整的API文档

### 未来计划
- 🚀 支持更多AI服务提供商
- 📱 移动端适配优化
- 🔄 实时消息推送
- 📈 高级数据分析
- 🌐 多语言支持
- 🎨 UI/UX优化

## 许可证

本项目采用 [MIT License](LICENSE) 许可证。

```
MIT License

Copyright (c) 2024 XLHealth Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 联系我们

- 📧 邮箱：contact@xlhealth.com
- 🌐 官网：https://www.xlhealth.com
- 📱 微信群：扫描二维码加入开发者群
- 💬 QQ群：123456789
- 📋 问题反馈：[GitHub Issues](https://github.com/xlhealth/xlhealth/issues)

---

<div align="center">
  <p>⭐ 如果这个项目对你有帮助，请给我们一个Star！</p>
  <p>Made with ❤️ by XLHealth Team</p>
</div>

---

**版本**: v0.0.1-SNAPSHOT  
**最后更新**: 2024-01-01  
**文档版本**: v1.0