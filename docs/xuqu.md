### AI心理咨询网站产品需求文档（简化版）

------

#### **1. 项目概述**

**目标**：提供基础AI心理咨询服务，支持用户与AI机器人对话，管理咨询历史记录。
**核心功能**：

- 游客浏览（无需登录）
- 用户注册/登录
- 三大基础页面：首页、AI心理助手、个人中心
  **开发原则**：快速实现核心功能，简化非必要特性

------

#### **2. 页面设计与功能说明**

##### **2.1 全局元素**

- **顶部导航栏**（Element Plus组件）：
  - Logo + 网站名称
  - 导航菜单：`首页`、`AI心理助手`（登录后可见）、`我的`（登录后可见）
  - 用户状态区：
    - 未登录：`登录`/`注册`按钮
    - 已登录：用户昵称 + 下拉菜单（个人中心、退出）
- **页脚**：版权信息 + 基础联系方式

------

##### **2.2 首页**

**核心元素**（使用Element Plus卡片/栅格布局）：

1. **欢迎横幅**：
   - 主文案："AI心理助手，随时倾听您的感受"
   - 引导按钮：`开始对话`（未登录点击弹出登录提示）
2. **动态消息区**：
   - 标题："最新动态"
   - 简易列表：纯文本公告（标题+发布日期）
3. **服务简介**：
   - 三个特性标签（图标+单行说明）：
     - 24小时在线
     - 隐私保护
     - 对话存档

------

##### **2.3 AI心理助手**（**仅登录用户**）

**功能布局**（左右分栏结构）：

1. **左侧历史记录面板**：
   - 标题："历史对话" + `新建`按钮
   - 会话列表：日期 + 对话首句（最多显示20条）
   - 操作：点击加载内容，每条记录带`删除`图标
2. **右侧聊天区**：
   - 消息气泡：
     - 用户消息（右侧/蓝色）
     - AI回复（左侧/灰色 + 机器人图标）
   - 输入区：
     - 多行文本框（Element Plus Input）
     - `发送`按钮（支持回车提交）
     - 底部固定提示："遇到危机请拨打心理援助热线"

------

##### **2.4 个人中心**（**仅登录用户**）

**模块设计**（Element Plus表单组件）：

1. **用户信息卡**：
   - 头像占位图（默认系统图标）
   - 昵称 + 邮箱（不可编辑）
2. **功能区块**：
   - **历史管理**：同AI心理助手中的会话列表
   - **账号设置**：
     - 修改密码（旧密码+新密码二次确认）
   - **数据清除**：
     - `清空所有历史`按钮（带确认弹窗）
   - **退出登录**按钮（底部）

------

#### **3. 技术栈实现方案**

##### **前端**（Vue 3生态）

- **框架**：Vue 3 Composition API
- **路由**：Vue Router 4（路由守卫控制页面权限）
- **状态管理**：Vuex 4（集中管理用户状态/对话数据）
- **UI组件库**：Element Plus（快速搭建界面）
- **HTTP请求**：Axios（封装API调用）

##### **后端**（Spring Boot生态）

- **框架**：Spring Boot 3.x（RESTful API开发）

- **数据库**：MySQL 8.0

  - 用户表（id, 用户名, 密码, 邮箱）
  - 对话表（id, 用户id, 对话内容, 时间戳）

- **认证**：JWT（登录态校验）

- **API设计**：

  ```
  POST /api/login        # 用户登录
  POST /api/register     # 用户注册
  GET /api/history       # 获取对话历史
  POST /api/chat         # 提交对话请求
  DELETE /api/history/{id} # 删除单条记录
  ```

##### **AI集成**

- 初期方案：直接调用第三方AI服务API（如OpenAI）

------

#### **4. 简化设计说明**

1. **安全简化**：
   - 密码存储：Spring Security BCrypt加密
   - JWT有效期：默认24小时
2. **功能简化**：
   - 无头像上传功能
   - 无实时通信（HTTP轮询替代WebSocket）
   - 历史记录仅存储文本（不包含对话状态）
3. **扩展性预留**：
   - 数据库设计预留`settings`字段（未来扩展配置项）
   - API响应结构包含`error_code`（未来扩展错误类型）

------

#### **5. 开发优先级**

1. **第一阶段**：
   - 用户系统（注册/登录/JWT）
   - AI对话基础功能（发送/接收/保存）
2. **第二阶段**：
   - 历史记录管理（查看/删除）
   - 个人中心页面
3. **第三阶段**：
   - 首页动态展示
   - 密码修改功能

------

**文档版本**：V1.1（简化版）
**最后更新**：2025年6月15日
**注**：此版本聚焦最小可行产品（MVP），后续迭代逐步增强功能