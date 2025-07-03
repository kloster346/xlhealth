-- 测试环境初始数据插入脚本

-- 插入系统配置数据
INSERT INTO system_configs (config_key, config_value, config_type, description, category, is_public, is_editable, sort_order, created_time, updated_time, deleted) VALUES
('system.name', 'XLHealth AI心理咨询系统', 'STRING', '系统名称', 'system', 1, 1, 1, NOW(), NOW(), 0),
('system.version', '1.0.0', 'STRING', '系统版本', 'system', 1, 0, 2, NOW(), NOW(), 0),
('system.description', '基于AI的智能心理健康咨询平台', 'STRING', '系统描述', 'system', 1, 1, 3, NOW(), NOW(), 0),
('ai.model.default', 'gpt-3.5-turbo', 'STRING', '默认AI模型', 'ai', 0, 1, 10, NOW(), NOW(), 0),
('ai.model.max_tokens', '4096', 'NUMBER', '最大token数', 'ai', 0, 1, 11, NOW(), NOW(), 0),
('ai.model.temperature', '0.7', 'NUMBER', '模型温度参数', 'ai', 0, 1, 12, NOW(), NOW(), 0),
('chat.max_history', '20', 'NUMBER', '最大聊天历史记录数', 'chat', 0, 1, 20, NOW(), NOW(), 0),
('chat.session_timeout', '3600', 'NUMBER', '会话超时时间(秒)', 'chat', 0, 1, 21, NOW(), NOW(), 0),
('security.jwt_secret', 'xlhealth_jwt_secret_key_2024', 'STRING', 'JWT密钥', 'security', 0, 1, 30, NOW(), NOW(), 0),
('security.jwt_expiration', '86400', 'NUMBER', 'JWT过期时间(秒)', 'security', 0, 1, 31, NOW(), NOW(), 0);

-- 插入管理员用户
INSERT INTO users (username, email, password_hash, nickname, status, created_time, updated_time, deleted) VALUES
('admin', 'admin@xlhealth.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzfKOCgdXwO', '系统管理员', 'ACTIVE', NOW(), NOW(), 0);