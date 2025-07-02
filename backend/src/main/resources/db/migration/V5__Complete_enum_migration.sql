-- Flyway Migration Script V5
-- Description: Complete the enum field migration to ensure consistency
-- Author: System
-- Date: 2024-01-01

-- 1. 修改 messages 表的 role 字段为大写枚举，并统一 AI 概念
ALTER TABLE messages MODIFY COLUMN role ENUM('USER', 'ASSISTANT', 'SYSTEM') NOT NULL COMMENT '角色类型';

-- 2. 更新现有的 messages 数据，将小写角色改为大写
UPDATE messages SET role = 'USER' WHERE role = 'user';
UPDATE messages SET role = 'ASSISTANT' WHERE role = 'assistant';
UPDATE messages SET role = 'SYSTEM' WHERE role = 'system';

-- 3. 修改 conversations 表的 status 字段为字符串枚举
ALTER TABLE conversations MODIFY COLUMN status ENUM('ACTIVE', 'ARCHIVED', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-进行中, ARCHIVED-已归档, DELETED-已删除';

-- 4. 更新现有的 conversations 数据，将数字状态改为字符串枚举
UPDATE conversations SET status = 'DELETED' WHERE status = 0;
UPDATE conversations SET status = 'ACTIVE' WHERE status = 1;
UPDATE conversations SET status = 'ARCHIVED' WHERE status = 2;

-- 5. 修改 messages 表的 content_type 字段为大写枚举
ALTER TABLE messages MODIFY COLUMN content_type ENUM('TEXT', 'IMAGE', 'AUDIO', 'VIDEO', 'FILE') NOT NULL DEFAULT 'TEXT' COMMENT '内容类型';

-- 6. 更新现有的 messages 数据，将小写内容类型改为大写
UPDATE messages SET content_type = 'TEXT' WHERE content_type = 'text';
UPDATE messages SET content_type = 'IMAGE' WHERE content_type = 'image';
UPDATE messages SET content_type = 'AUDIO' WHERE content_type = 'audio';
UPDATE messages SET content_type = 'VIDEO' WHERE content_type = 'video';
UPDATE messages SET content_type = 'FILE' WHERE content_type = 'file';

-- 7. 修改 user_sessions 表的 status 字段为字符串枚举
ALTER TABLE user_sessions MODIFY COLUMN status ENUM('INVALID', 'ACTIVE', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: INVALID-已失效, ACTIVE-活跃, EXPIRED-已过期';

-- 8. 更新现有的 user_sessions 数据
UPDATE user_sessions SET status = 'INVALID' WHERE status = 0;
UPDATE user_sessions SET status = 'ACTIVE' WHERE status = 1;
UPDATE user_sessions SET status = 'EXPIRED' WHERE status = 2;

-- 9. 修改 messages 表的 status 字段为字符串枚举
ALTER TABLE messages MODIFY COLUMN status ENUM('FAILED', 'SUCCESS', 'PROCESSING') NOT NULL DEFAULT 'SUCCESS' COMMENT '状态: FAILED-失败, SUCCESS-成功, PROCESSING-处理中';

-- 10. 更新现有的 messages 数据
UPDATE messages SET status = 'FAILED' WHERE status = 0;
UPDATE messages SET status = 'SUCCESS' WHERE status = 1;
UPDATE messages SET status = 'PROCESSING' WHERE status = 2;

-- 11. 为了保持一致性，删除可能重复的 message_type 字段（因为 role 字段已经表示了消息角色）
-- 如果需要保留 message_type，请注释掉下面这行
-- ALTER TABLE messages DROP COLUMN message_type;

-- 12. 添加注释说明字段用途
ALTER TABLE messages MODIFY COLUMN role ENUM('USER', 'ASSISTANT', 'SYSTEM') NOT NULL COMMENT '消息角色: USER-用户, ASSISTANT-AI助手, SYSTEM-系统';
ALTER TABLE messages MODIFY COLUMN message_type ENUM('USER', 'ASSISTANT', 'SYSTEM') NOT NULL DEFAULT 'USER' COMMENT '消息类型: USER-用户消息, ASSISTANT-AI回复, SYSTEM-系统消息';