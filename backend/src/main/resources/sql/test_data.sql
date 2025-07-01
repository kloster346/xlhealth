-- 测试数据脚本
-- Description: Insert test data for development and testing
-- Author: System
-- Date: 2024-01-01
-- Note: 此脚本仅用于开发和测试环境，请勿在生产环境中执行

-- 清理现有测试数据（保留管理员用户）
DELETE FROM audit_logs WHERE user_id > 1;
DELETE FROM user_sessions WHERE user_id > 1;
DELETE FROM messages WHERE user_id > 1;
DELETE FROM conversations WHERE user_id > 1;
DELETE FROM users WHERE id > 1;

-- 重置自增ID
ALTER TABLE users AUTO_INCREMENT = 2;
ALTER TABLE conversations AUTO_INCREMENT = 1;
ALTER TABLE messages AUTO_INCREMENT = 1;
ALTER TABLE user_sessions AUTO_INCREMENT = 1;
ALTER TABLE audit_logs AUTO_INCREMENT = 1;

-- 插入测试用户
INSERT INTO users (username, email, password_hash, phone, nickname, avatar_url, gender, birth_date, profile, status, created_time, updated_time) VALUES
('testuser1', 'test1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzfKOCgdXwO', '13800138001', '测试用户1', 'https://example.com/avatar1.jpg', 1, '1990-01-01', '这是测试用户1的个人简介', 1, NOW(), NOW()),
('testuser2', 'test2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzfKOCgdXwO', '13800138002', '测试用户2', 'https://example.com/avatar2.jpg', 2, '1995-05-15', '这是测试用户2的个人简介', 1, NOW(), NOW()),
('testuser3', 'test3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzfKOCgdXwO', '13800138003', '测试用户3', 'https://example.com/avatar3.jpg', 0, '1988-12-25', '这是测试用户3的个人简介', 1, NOW(), NOW()),
('inactiveuser', 'inactive@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzfKOCgdXwO', '13800138004', '禁用用户', NULL, 0, NULL, '这是一个被禁用的测试用户', 0, NOW(), NOW());

-- 插入测试对话
INSERT INTO conversations (user_id, title, summary, status, message_count, total_tokens, start_time, created_time, updated_time) VALUES
(2, '心理健康咨询', '关于焦虑和压力管理的对话', 1, 6, 1250, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NOW()),
(2, '情感支持对话', '关于人际关系问题的讨论', 0, 8, 1800, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 2 HOUR),
(3, '睡眠问题咨询', '关于失眠和睡眠质量的咨询', 1, 4, 950, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, NOW()),
(3, '工作压力管理', '关于职场压力和工作生活平衡的对话', 2, 10, 2200, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 HOUR),
(4, '抑郁情绪咨询', '关于情绪低落和抑郁症状的咨询', 0, 5, 1100, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 3 DAY);

-- 插入测试消息
INSERT INTO messages (conversation_id, user_id, role, content, content_type, token_count, model_name, prompt_tokens, completion_tokens, total_tokens, response_time, status, created_time, updated_time) VALUES
-- 对话1的消息
(1, 2, 'user', '你好，我最近感到很焦虑，不知道该怎么办。', 'text', 25, NULL, 0, 0, 25, 0, 1, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR),
(1, 2, 'assistant', '你好！感谢你愿意分享你的感受。焦虑是很常见的情绪反应，我们可以一起探讨一些应对方法。能告诉我具体是什么让你感到焦虑吗？', 'text', 85, 'gpt-3.5-turbo', 25, 85, 110, 1200, 1, NOW() - INTERVAL 2 HOUR + INTERVAL 1 MINUTE, NOW() - INTERVAL 2 HOUR + INTERVAL 1 MINUTE),
(1, 2, 'user', '主要是工作上的压力，总是担心完不成任务，晚上也睡不好。', 'text', 32, NULL, 0, 0, 32, 0, 1, NOW() - INTERVAL 2 HOUR + INTERVAL 5 MINUTE, NOW() - INTERVAL 2 HOUR + INTERVAL 5 MINUTE),
(1, 2, 'assistant', '工作压力确实会影响睡眠质量，形成恶性循环。我建议你可以尝试以下几个方法：1. 制定合理的工作计划，将大任务分解为小目标；2. 学习放松技巧，如深呼吸或冥想；3. 建立良好的睡前习惯。你觉得哪个方法比较适合你先尝试？', 'text', 145, 'gpt-3.5-turbo', 32, 145, 177, 1500, 1, NOW() - INTERVAL 2 HOUR + INTERVAL 6 MINUTE, NOW() - INTERVAL 2 HOUR + INTERVAL 6 MINUTE),
(1, 2, 'user', '我想先试试制定工作计划，但不知道具体怎么做。', 'text', 28, NULL, 0, 0, 28, 0, 1, NOW() - INTERVAL 2 HOUR + INTERVAL 10 MINUTE, NOW() - INTERVAL 2 HOUR + INTERVAL 10 MINUTE),
(1, 2, 'assistant', '很好的选择！制定工作计划的具体步骤：1. 列出所有待完成的任务；2. 按重要性和紧急性排序；3. 估算每个任务所需时间；4. 安排到具体的时间段；5. 预留缓冲时间应对突发情况。建议使用番茄工作法，每25分钟专注工作，然后休息5分钟。你可以从明天开始尝试，有什么问题随时可以来咨询。', 'text', 168, 'gpt-3.5-turbo', 28, 168, 196, 1800, 1, NOW() - INTERVAL 2 HOUR + INTERVAL 11 MINUTE, NOW() - INTERVAL 2 HOUR + INTERVAL 11 MINUTE),

-- 对话2的消息
(2, 2, 'user', '我和朋友之间出现了一些矛盾，感觉很困扰。', 'text', 24, NULL, 0, 0, 24, 0, 1, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
(2, 2, 'assistant', '人际关系中出现矛盾是很正常的，重要的是如何处理。能具体说说发生了什么吗？这样我能更好地帮助你分析情况。', 'text', 78, 'gpt-3.5-turbo', 24, 78, 102, 1100, 1, NOW() - INTERVAL 1 DAY + INTERVAL 2 MINUTE, NOW() - INTERVAL 1 DAY + INTERVAL 2 MINUTE),

-- 对话3的消息
(3, 3, 'user', '我最近总是失眠，已经持续一个多星期了。', 'text', 22, NULL, 0, 0, 22, 0, 1, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
(3, 3, 'assistant', '失眠确实很困扰，持续一周以上需要重视。让我们先了解一下你的睡眠习惯：你通常几点上床？睡前会做什么？卧室环境如何？', 'text', 88, 'gpt-3.5-turbo', 22, 88, 110, 1300, 1, NOW() - INTERVAL 3 HOUR + INTERVAL 1 MINUTE, NOW() - INTERVAL 3 HOUR + INTERVAL 1 MINUTE),

-- 对话4的消息
(4, 3, 'user', '工作压力太大了，感觉快要崩溃了。', 'text', 19, NULL, 0, 0, 19, 0, 1, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
(4, 3, 'assistant', '我能理解你现在的感受，工作压力过大确实会让人感到不堪重负。首先，请记住你并不孤单，这种感受是可以改善的。我们可以一起探讨一些减压的方法。', 'text', 95, 'gpt-3.5-turbo', 19, 95, 114, 1400, 1, NOW() - INTERVAL 2 DAY + INTERVAL 1 MINUTE, NOW() - INTERVAL 2 DAY + INTERVAL 1 MINUTE);

-- 插入测试用户会话
INSERT INTO user_sessions (user_id, session_token, refresh_token, device_info, ip_address, user_agent, location, status, expires_at, last_activity_time, created_time, updated_time) VALUES
(2, 'session_token_user2_001', 'refresh_token_user2_001', 'Windows 10, Chrome 120.0', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '北京市', 1, NOW() + INTERVAL 1 DAY, NOW(), NOW() - INTERVAL 2 HOUR, NOW()),
(3, 'session_token_user3_001', 'refresh_token_user3_001', 'macOS 14.0, Safari 17.0', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15', '上海市', 1, NOW() + INTERVAL 1 DAY, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 30 MINUTE),
(4, 'session_token_user4_001', 'refresh_token_user4_001', 'Android 13, Chrome Mobile', '192.168.1.102', 'Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36', '广州市', 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 HOUR);

-- 插入测试审计日志
INSERT INTO audit_logs (user_id, action, resource_type, resource_id, old_values, new_values, ip_address, user_agent, request_method, request_url, response_status, execution_time, created_time) VALUES
(2, 'LOGIN', 'user', '2', NULL, '{"login_time": "2024-01-01 10:00:00"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'POST', '/api/auth/login', 200, 150, NOW() - INTERVAL 2 HOUR),
(2, 'CREATE_CONVERSATION', 'conversation', '1', NULL, '{"title": "心理健康咨询", "user_id": 2}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'POST', '/api/conversations', 201, 80, NOW() - INTERVAL 2 HOUR + INTERVAL 5 MINUTE),
(3, 'LOGIN', 'user', '3', NULL, '{"login_time": "2024-01-01 08:00:00"}', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15', 'POST', '/api/auth/login', 200, 120, NOW() - INTERVAL 3 HOUR),
(3, 'CREATE_CONVERSATION', 'conversation', '3', NULL, '{"title": "睡眠问题咨询", "user_id": 3}', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15', 'POST', '/api/conversations', 201, 90, NOW() - INTERVAL 3 HOUR + INTERVAL 10 MINUTE),
(4, 'LOGIN', 'user', '4', NULL, '{"login_time": "2023-12-30 15:00:00"}', '192.168.1.102', 'Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36', 'POST', '/api/auth/login', 200, 200, NOW() - INTERVAL 1 DAY),
(1, 'UPDATE_CONFIG', 'system_config', 'ai.model.default', '{"value": "gpt-3.5-turbo"}', '{"value": "gpt-4"}', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'PUT', '/api/admin/configs/ai.model.default', 200, 50, NOW() - INTERVAL 6 HOUR);

-- 显示插入的测试数据统计
SELECT 
    'users' as table_name, 
    COUNT(*) as record_count 
FROM users 
WHERE id > 1
UNION ALL
SELECT 
    'conversations' as table_name, 
    COUNT(*) as record_count 
FROM conversations
UNION ALL
SELECT 
    'messages' as table_name, 
    COUNT(*) as record_count 
FROM messages
UNION ALL
SELECT 
    'user_sessions' as table_name, 
    COUNT(*) as record_count 
FROM user_sessions
UNION ALL
SELECT 
    'audit_logs' as table_name, 
    COUNT(*) as record_count 
FROM audit_logs;

-- 显示测试用户信息
SELECT 
    id,
    username,
    email,
    nickname,
    status,
    created_time
FROM users 
WHERE id > 1
ORDER BY id;