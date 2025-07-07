# API密钥配置说明

## 概述

为了保护敏感信息（如API密钥）不被提交到代码仓库，本项目采用环境变量和本地配置文件的方式管理敏感配置。

## 配置方式

### 方式一：使用本地配置文件（推荐）

1. 复制配置模板文件：
   ```bash
   cp src/main/resources/application-ai.yml.template src/main/resources/application-ai-local.yml
   ```

2. 编辑 `application-ai-local.yml` 文件，将 `your-deepseek-api-key-here` 替换为你的真实API密钥：
   ```yaml
   ai:
     service:
       deepseek:
         api-key: your-actual-deepseek-api-key
   ```

3. 启动应用，Spring Boot会自动加载本地配置文件。

### 方式二：使用环境变量

1. 设置环境变量：
   
   **Windows (PowerShell):**
   ```powershell
   $env:DEEPSEEK_API_KEY="your-actual-deepseek-api-key"
   ```
   
   **Windows (CMD):**
   ```cmd
   set DEEPSEEK_API_KEY=your-actual-deepseek-api-key
   ```
   
   **Linux/macOS:**
   ```bash
   export DEEPSEEK_API_KEY="your-actual-deepseek-api-key"
   ```

2. 启动应用：
   ```bash
   mvn spring-boot:run
   ```

### 方式三：IDE环境变量配置

在你的IDE中配置运行环境变量：

**IntelliJ IDEA:**
1. 打开 Run/Debug Configurations
2. 在 Environment variables 中添加：`DEEPSEEK_API_KEY=your-actual-deepseek-api-key`

**VS Code:**
1. 在 `.vscode/launch.json` 中添加环境变量（注意：此文件已在.gitignore中）

## 获取DeepSeek API密钥

1. 访问 [DeepSeek官网](https://platform.deepseek.com/)
2. 注册账号并登录
3. 进入API管理页面
4. 创建新的API密钥
5. 复制密钥并按照上述方式配置

## 安全注意事项

1. **永远不要**将真实的API密钥提交到代码仓库
2. **永远不要**在代码中硬编码API密钥
3. 定期轮换API密钥
4. 如果意外提交了API密钥，立即撤销并生成新密钥
5. 本地配置文件 `application-ai-local.yml` 已被添加到 `.gitignore`，不会被提交

## 文件说明

- `application-ai.yml` - 主配置文件，使用环境变量占位符
- `application-ai.yml.template` - 配置模板文件，供参考使用
- `application-ai-local.yml` - 本地配置文件，包含真实密钥（不会被提交）
- `.gitignore` - 已配置忽略所有敏感配置文件

## 故障排除

如果遇到API密钥相关错误：

1. 检查环境变量是否正确设置
2. 检查本地配置文件是否存在且格式正确
3. 确认API密钥是否有效且未过期
4. 查看应用启动日志中的配置加载信息

## 团队协作

每个开发者都需要：
1. 获取自己的DeepSeek API密钥
2. 按照上述方式配置本地环境
3. 不要共享API密钥
4. 不要修改 `application-ai.yml` 中的环境变量占位符