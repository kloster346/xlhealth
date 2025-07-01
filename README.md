# XLHealth - AI 青少年心理健康咨询干预平台

## 项目简介

XLHealth 是一个全栈心理咨询干预平台，采用前后端分离架构，为用户提供全面的心理咨询服务。

## 技术架构

### 后端技术栈

- **框架**: Spring Boot 2.x
- **数据库**: MySQL 8.0
- **端口**: 8081
- **构建工具**: Maven
- **Java 版本**: JDK 8+

### 前端技术栈

- **框架**: Vue.js 3.x
- **路由**: Vue Router 4.x
- **状态管理**: Vuex 4.x
- **构建工具**: Vue CLI 5.x
- **代码规范**: ESLint

## 项目结构

```
xlhealth/
├── backend/                 # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/        # Java源代码
│   │   │   └── resources/   # 配置文件
│   │   └── test/            # 测试代码
│   ├── pom.xml             # Maven依赖配置
│   └── .gitignore          # 后端Git忽略文件
├── frontend/               # 前端Vue.js项目
│   ├── src/
│   │   ├── components/     # Vue组件
│   │   ├── views/          # 页面视图
│   │   ├── router/         # 路由配置
│   │   ├── store/          # Vuex状态管理
│   │   └── assets/         # 静态资源
│   ├── public/             # 公共文件
│   ├── package.json        # npm依赖配置
│   └── .gitignore          # 前端Git忽略文件
├── .gitignore              # 项目根目录Git忽略文件
└── README.md               # 项目说明文档
```

## 环境要求

### 后端环境

- JDK 8 或更高版本
- Maven 3.6+
- MySQL 8.0+

### 前端环境

- Node.js 14.0+
- npm 6.0+ 或 yarn 1.22+

## 快速开始

### 1. 数据库配置

1. 安装并启动 MySQL 服务
2. 创建数据库：
   ```sql
   CREATE DATABASE xlhealth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 修改后端配置文件 `backend/src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/xlhealth?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
       username: your_username
       password: your_password
   ```

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 安装依赖并启动
./mvnw spring-boot:run

# 或者使用Maven
mvn spring-boot:run
```

后端服务将在 `http://localhost:8081` 启动

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

前端服务将在 `http://localhost:8080` 启动

## 开发指南

### 后端开发

#### 项目结构说明

- `src/main/java/cn/xlhealth/backend/` - 主要业务代码
- `src/main/resources/application.yml` - 应用配置文件
- `src/test/java/` - 单元测试代码

#### 常用命令

```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 打包项目
mvn package

# 清理项目
mvn clean
```

### 前端开发

#### 项目结构说明

- `src/components/` - 可复用组件
- `src/views/` - 页面组件
- `src/router/` - 路由配置
- `src/store/` - 状态管理
- `src/assets/` - 静态资源文件

#### 常用命令

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build

# 代码检查
npm run lint

# 代码格式化
npm run lint -- --fix
```

## API 接口

后端 API 基础地址：`http://localhost:8081`

### 接口规范

- 所有接口返回 JSON 格式数据
- 使用 RESTful API 设计规范
- 统一的响应格式：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {}
  }
  ```

## 部署说明

### 后端部署

1. 打包项目：`mvn clean package`
2. 上传 jar 包到服务器
3. 配置生产环境数据库
4. 启动服务：`java -jar backend-0.0.1-SNAPSHOT.jar`

### 前端部署

1. 构建项目：`npm run build`
2. 将`dist`目录内容部署到 Web 服务器
3. 配置 Nginx 反向代理（可选）

## 常见问题

### 1. 数据库连接失败

- 检查 MySQL 服务是否启动
- 确认数据库用户名和密码正确
- 检查数据库是否存在

### 2. 前端启动失败

- 检查 Node.js 版本是否符合要求
- 删除`node_modules`文件夹，重新执行`npm install`
- 检查端口 8080 是否被占用

### 3. 跨域问题

- 后端已配置 CORS 支持
- 如有问题，检查`application.yml`中的跨域配置

## 贡献指南

1. Fork 项目
2. 创建特性分支：`git checkout -b feature/new-feature`
3. 提交更改：`git commit -am 'Add new feature'`
4. 推送分支：`git push origin feature/new-feature`
5. 提交 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目 Issues：[GitHub Issues](https://github.com/your-username/xlhealth/issues)
- 邮箱：your-email@example.com

---

**注意**: 这是一个开发中的项目，功能可能会有变动。建议在生产环境使用前进行充分测试。
