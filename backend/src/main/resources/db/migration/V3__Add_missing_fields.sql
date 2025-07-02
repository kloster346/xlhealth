-- Flyway Migration Script V3
-- Description: Add missing fields to match Java entity definitions
-- Author: System
-- Date: 2024-01-01

-- 1. 为 conversations 表添加 metadata 字段
ALTER TABLE conversations ADD COLUMN metadata JSON COMMENT '元数据(JSON格式)';

-- 2. 为 conversations 表添加 last_message_at 字段
ALTER TABLE conversations ADD COLUMN last_message_at DATETIME COMMENT '最后消息时间';

-- 3. 为 messages 表添加 message_type 字段
ALTER TABLE messages ADD COLUMN message_type ENUM('USER', 'ASSISTANT', 'SYSTEM') NOT NULL DEFAULT 'USER' COMMENT '消息类型';

-- 4. 更新现有的 system_configs 数据，将小写枚举值改为大写
UPDATE system_configs SET config_type = 'STRING' WHERE config_type = 'string';
UPDATE system_configs SET config_type = 'NUMBER' WHERE config_type = 'number';
UPDATE system_configs SET config_type = 'BOOLEAN' WHERE config_type = 'boolean';
UPDATE system_configs SET config_type = 'JSON' WHERE config_type = 'json';

-- 5. 为新字段添加索引
ALTER TABLE conversations ADD INDEX idx_last_message_at (last_message_at);
ALTER TABLE messages ADD INDEX idx_message_type (message_type);