-- Flyway Migration Script V2
-- Description: Insert initial system configuration data and admin user
-- Author: System
-- Date: 2024-01-01

-- 插入系统配置数据
INSERT INTO system_configs (config_key, config_value, config_type, description, category, is_public, is_editable, sort_order) VALUES
('system.name', 'XLHealth AI心理咨询系统', 'string', '系统名称', 'system', 1, 1, 1),
('system.version', '1.0.0', 'string', '系统版本', 'system', 1, 0, 2),
('system.description', '基于AI的智能心理健康咨询平台', 'string', '系统描述', 'system', 1, 1, 3),
('ai.model.default', 'gpt-3.5-turbo', 'string', '默认AI模型', 'ai', 0, 1, 10),
('ai.model.max_tokens', '4096', 'number', '最大token数', 'ai', 0, 1, 11),
('ai.model.temperature', '0.7', 'number', '模型温度参数', 'ai', 0, 1, 12),
('chat.max_history', '20', 'number', '最大聊天历史记录数', 'chat', 0, 1, 20),
('chat.session_timeout', '3600', 'number', '会话超时时间(秒)', 'chat', 0, 1, 21),
('security.jwt_secret', 'xlhealth_jwt_secret_key_2024', 'string', 'JWT密钥', 'security', 0, 1, 30),
('security.jwt_expiration', '86400', 'number', 'JWT过期时间(秒)', 'security', 0, 1, 31)
ON DUPLICATE KEY UPDATE
config_value = VALUES(config_value),
description = VALUES(description),
updated_time = CURRENT_TIMESTAMP;

-- 插入管理员用户
INSERT INTO users (username, email, password_hash, nickname, status, created_time, updated_time) VALUES
('admin', 'admin@xlhealth.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzfKOCgdXwO', '系统管理员', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
nickname = VALUES(nickname),
status = VALUES(status),
updated_time = NOW();