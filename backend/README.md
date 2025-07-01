# XLHealth AI 心理咨询系统 - 后端服务

## 项目简介

XLHealth AI 心理咨询系统后端服务，基于 Spring Boot 3.5.0 构建，提供用户认证、对话管理、消息处理、AI 集成等核心 API 服务。

## 技术架构

### 核心框架
- **Spring Boot 3.5.0** - 主框架
- **Java 17** - 开发语言
- **Maven** - 项目构建工具
- **MyBatis Plus 3.5.8** - ORM 框架

### 数据库
- **MySQL 8.0** - 主数据库
- **HikariCP** - 数据库连接池（Spring Boot 默认）

### 核心依赖
- **JWT (jjwt 0.9.1)** - 身份认证
- **FastJSON 1.2.35** - JSON 处理
- **MySQL Connector/J** - 数据库驱动
- **JAXB API 2.3.1** - XML 绑定支持

## 项目结构

```
backend/
├── src/main/java/cn/xlhealth/backend/
│   ├── BackendApplication.java          # 主启动类
│   ├── config/                          # 配置类
│   ├── entity/                          # 实体类
│   ├── mapper/                          # 数据访问层
│   ├── service/                         # 业务逻辑层
│   │   └── impl/                        # 服务实现类
│   └── ui/                              # 控制层
│       ├── advice/                      # 全局异常处理
│       ├── controller/                  # 控制器
│       ├── dto/                         # 数据传输对象
│       └── interceptor/                 # 拦截器
├── src/main/resources/
│   ├── application.yml                  # 主配置文件
│   └── static/                          # 静态资源
├── src/test/java/                       # 测试代码
├── pom.xml                              # Maven 配置
└── README.md                            # 项目说明
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

## API 接口规范

### 请求格式
- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **认证方式**: JWT Bearer Token

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2024-01-01T12:00:00Z"
}
```

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

### Docker 部署
```dockerfile
# Dockerfile
FROM openjdk:17-jre-slim
VOLUME /tmp
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]
```

构建和运行：
```bash
# 构建镜像
docker build -t xlhealth-backend .

# 运行容器
docker run -p 8081:8081 xlhealth-backend
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

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详情请查看 [LICENSE](LICENSE) 文件

## 联系方式

- **项目维护者**: XLHealth 开发团队
- **邮箱**: dev@xlhealth.cn
- **项目地址**: https://github.com/xlhealth/xlhealth

---

**版本**: v0.0.1-SNAPSHOT  
**最后更新**: 2024-01-01  
**文档版本**: v1.0