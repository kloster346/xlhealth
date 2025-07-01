-- XLHealth AI 心理咨询系统数据库初始化脚本
-- 创建时间: 2024-01-01
-- 版本: 1.0.0

-- 创建数据库
CREATE DATABASE IF NOT EXISTS xlhealth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xlhealth;

-- 1. 用户表 (users)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    birth_date DATE COMMENT '出生日期',
    profile TEXT COMMENT '个人简介',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常, 2-待验证',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) COMMENT '最后登录IP',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 对话表 (conversations)
CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) DEFAULT '新对话' COMMENT '对话标题',
    summary TEXT COMMENT '对话摘要',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已结束, 1-进行中, 2-已暂停',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    total_tokens INT DEFAULT 0 COMMENT '总token数',
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话表';

-- 3. 消息表 (messages)
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('user', 'assistant', 'system') NOT NULL COMMENT '角色类型',
    content TEXT NOT NULL COMMENT '消息内容',
    content_type ENUM('text', 'image', 'audio', 'file') DEFAULT 'text' COMMENT '内容类型',
    metadata JSON COMMENT '元数据(JSON格式)',
    token_count INT DEFAULT 0 COMMENT 'token数量',
    model_name VARCHAR(100) COMMENT '使用的模型名称',
    prompt_tokens INT DEFAULT 0 COMMENT '提示token数',
    completion_tokens INT DEFAULT 0 COMMENT '完成token数',
    total_tokens INT DEFAULT 0 COMMENT '总token数',
    response_time INT DEFAULT 0 COMMENT '响应时间(毫秒)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-失败, 1-成功, 2-处理中',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role (role),
    INDEX idx_created_time (created_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 4. 用户会话表 (user_sessions)
CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_token VARCHAR(255) NOT NULL UNIQUE COMMENT '会话令牌',
    refresh_token VARCHAR(255) COMMENT '刷新令牌',
    device_info VARCHAR(500) COMMENT '设备信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    location VARCHAR(200) COMMENT '地理位置',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已失效, 1-有效',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    last_activity_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后活动时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at),
    INDEX idx_last_activity_time (last_activity_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

-- 5. 系统配置表 (system_configs)
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type ENUM('string', 'number', 'boolean', 'json') DEFAULT 'string' COMMENT '配置类型',
    description VARCHAR(500) COMMENT '配置描述',
    category VARCHAR(50) DEFAULT 'general' COMMENT '配置分类',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开: 0-私有, 1-公开',
    is_editable TINYINT DEFAULT 1 COMMENT '是否可编辑: 0-不可编辑, 1-可编辑',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_config_key (config_key),
    INDEX idx_category (category),
    INDEX idx_is_public (is_public),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 6. 审计日志表 (audit_logs)
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作动作',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id VARCHAR(100) COMMENT '资源ID',
    old_values JSON COMMENT '旧值(JSON格式)',
    new_values JSON COMMENT '新值(JSON格式)',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params JSON COMMENT '请求参数(JSON格式)',
    response_status INT COMMENT '响应状态码',
    execution_time INT COMMENT '执行时间(毫秒)',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_resource_type (resource_type),
    INDEX idx_resource_id (resource_id),
    INDEX idx_created_time (created_time),
    INDEX idx_ip_address (ip_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 插入初始系统配置数据
INSERT INTO system_configs (config_key, config_value, config_type, description, category, is_public) VALUES
('system.name', 'XLHealth AI 心理咨询系统', 'string', '系统名称', 'system', 1),
('system.version', '1.0.0', 'string', '系统版本', 'system', 1),
('system.description', 'AI驱动的心理健康咨询平台', 'string', '系统描述', 'system', 1),
('ai.model.default', 'gpt-3.5-turbo', 'string', '默认AI模型', 'ai', 0),
('ai.max_tokens', '4000', 'number', '最大token数', 'ai', 0),
('ai.temperature', '0.7', 'number', 'AI温度参数', 'ai', 0),
('session.timeout', '86400', 'number', '会话超时时间(秒)', 'security', 0),
('upload.max_file_size', '10485760', 'number', '最大文件上传大小(字节)', 'upload', 0),
('upload.allowed_types', '["jpg","jpeg","png","gif","pdf","doc","docx"]', 'json', '允许上传的文件类型', 'upload', 0),
('notification.email.enabled', 'true', 'boolean', '是否启用邮件通知', 'notification', 0);

-- 创建管理员用户 (密码: admin123)
INSERT INTO users (username, email, password_hash, nickname, status) VALUES
('admin', 'admin@xlhealth.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaYMountjdMSS', '系统管理员', 1);

-- 显示创建结果
SELECT 'XLHealth数据库初始化完成!' as message;
SHOW TABLES;