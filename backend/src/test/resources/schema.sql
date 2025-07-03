-- 测试环境数据库初始化脚本

-- 清理已存在的表
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS user_sessions;
DROP TABLE IF EXISTS email_verification_tokens;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS system_configs;
DROP TABLE IF EXISTS users;

-- 创建 users 表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    phone VARCHAR(20),
    gender VARCHAR(10),
    birth_date DATE,
    profile TEXT,
    last_login_ip VARCHAR(45),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 创建 user_sessions 表
CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_info VARCHAR(255),
    location VARCHAR(255),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1
);

-- 为 user_sessions 创建索引
CREATE INDEX IF NOT EXISTS idx_session_token ON user_sessions (session_token);
CREATE INDEX IF NOT EXISTS idx_user_id ON user_sessions (user_id);
CREATE INDEX IF NOT EXISTS idx_expires_at ON user_sessions (expires_at);
CREATE INDEX IF NOT EXISTS idx_status ON user_sessions (status);

-- 用户资料表
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bio TEXT,
    birth_date DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    location VARCHAR(100),
    website VARCHAR(255),
    preferences JSON,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 密码重置令牌表
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 为 password_reset_tokens 表创建索引
CREATE INDEX IF NOT EXISTS idx_token ON password_reset_tokens (token);
CREATE INDEX IF NOT EXISTS idx_expires_at_reset ON password_reset_tokens (expires_at);

-- 邮箱验证令牌表
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 为 email_verification_tokens 表创建索引
CREATE INDEX IF NOT EXISTS idx_token_email ON email_verification_tokens (token);
CREATE INDEX IF NOT EXISTS idx_expires_at_email ON email_verification_tokens (expires_at);

-- 审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50),
    resource_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    request_method VARCHAR(10),
    request_url VARCHAR(255),
    request_params TEXT,
    response_status INT,
    execution_time BIGINT,
    error_message TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 为 audit_logs 表创建索引
CREATE INDEX IF NOT EXISTS idx_user_id ON audit_logs (user_id);
CREATE INDEX IF NOT EXISTS idx_action ON audit_logs (action);
CREATE INDEX IF NOT EXISTS idx_resource ON audit_logs (resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_created_time_audit ON audit_logs (created_time);

-- 创建 system_configs 表
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    category VARCHAR(50),
    description VARCHAR(255),
    config_type VARCHAR(20) DEFAULT 'STRING',
    is_public BOOLEAN DEFAULT FALSE,
    is_editable BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 创建 conversations 表
CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    metadata TEXT,
    summary TEXT,
    message_count INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    last_message_at TIMESTAMP
);

-- 创建 messages 表
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    user_id BIGINT,
    role VARCHAR(20) NOT NULL,
    content TEXT,
    content_type VARCHAR(20) DEFAULT 'TEXT',
    token_count INT,
    model_name VARCHAR(100),
    prompt_tokens INT,
    completion_tokens INT,
    total_tokens INT,
    response_time BIGINT,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    error_message TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 创建 system_configs 表的索引
CREATE INDEX idx_config_key ON system_configs (config_key);
CREATE INDEX idx_category ON system_configs (category);

-- 创建 conversations 表的索引
CREATE INDEX idx_conversations_user_id ON conversations (user_id);
CREATE INDEX idx_conversations_status ON conversations (status);

-- 创建 messages 表的索引
CREATE INDEX idx_messages_conversation_id ON messages (conversation_id);
CREATE INDEX idx_messages_user_id ON messages (user_id);
CREATE INDEX idx_messages_role ON messages (role);