-- Flyway Migration Script V4
-- Description: Fix enum fields to match Java entity definitions
-- Author: System
-- Date: 2024-01-01

-- 1. 修改 system_configs 表的 config_type 字段为 ENUM 类型
ALTER TABLE system_configs MODIFY COLUMN config_type ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') NOT NULL DEFAULT 'STRING';

-- 2. 更新现有的 system_configs 数据，将小写枚举值改为大写
UPDATE system_configs SET config_type = 'STRING' WHERE config_type = 'string';
UPDATE system_configs SET config_type = 'NUMBER' WHERE config_type = 'number';
UPDATE system_configs SET config_type = 'BOOLEAN' WHERE config_type = 'boolean';
UPDATE system_configs SET config_type = 'JSON' WHERE config_type = 'json';

-- 3. 确保 users 表的 status 字段为 ENUM 类型
ALTER TABLE users MODIFY COLUMN status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'ACTIVE';

-- 4. 确保 users 表的 gender 字段为 ENUM 类型
ALTER TABLE users MODIFY COLUMN gender ENUM('MALE', 'FEMALE', 'OTHER') DEFAULT 'OTHER';

-- 5. 更新空的 gender 值
UPDATE users SET gender = 'OTHER' WHERE gender IS NULL OR gender = '';