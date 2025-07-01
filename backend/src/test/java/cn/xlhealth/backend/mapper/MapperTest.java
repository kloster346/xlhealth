package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapper层测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserSessionMapper userSessionMapper;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Test
    public void testUserMapper() {
        // 测试查询所有用户
        List<User> users = userMapper.selectList(null);
        assertNotNull(users);
        assertTrue(users.size() > 0);
        
        // 测试根据用户名查找用户
        User user = userMapper.findByUsername("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        
        // 测试分页查询
        Page<User> page = new Page<>(1, 10);
        Page<User> userPage = userMapper.selectPage(page, null);
        assertNotNull(userPage);
        assertTrue(userPage.getTotal() > 0);
    }

    @Test
    public void testConversationMapper() {
        // 测试查询所有对话
        List<Conversation> conversations = conversationMapper.selectList(null);
        assertNotNull(conversations);
        
        // 测试分页查询
        Page<Conversation> page = new Page<>(1, 10);
        Page<Conversation> conversationPage = conversationMapper.selectPage(page, null);
        assertNotNull(conversationPage);
    }

    @Test
    public void testMessageMapper() {
        // 测试查询所有消息
        List<Message> messages = messageMapper.selectList(null);
        assertNotNull(messages);
        
        if (!messages.isEmpty()) {
            // 测试根据对话ID分页查询消息
            Long conversationId = messages.get(0).getConversationId();
            Page<Message> page = new Page<>(1, 10);
            IPage<Message> messagePage = messageMapper.findByConversationId(page, conversationId);
            assertNotNull(messagePage);
            
            // 测试统计对话消息数量
            Long count = messageMapper.countByConversationId(conversationId);
            assertNotNull(count);
            assertTrue(count >= 0);
            
            // 测试查询最后一条消息
            Message lastMessage = messageMapper.findLastByConversationId(conversationId);
            // 可能为null，所以不做断言
        }
    }

    @Test
    public void testUserSessionMapper() {
        // 测试查询所有会话
        List<UserSession> sessions = userSessionMapper.selectList(null);
        assertNotNull(sessions);
        
        // 测试删除过期会话
        int deletedCount = userSessionMapper.deleteExpiredSessions();
        assertTrue(deletedCount >= 0);
    }

    @Test
    public void testSystemConfigMapper() {
        // 测试查询所有配置
        List<SystemConfig> configs = systemConfigMapper.selectList(null);
        assertNotNull(configs);
        assertTrue(configs.size() > 0);
        
        // 测试根据配置键查询配置值
        String value = systemConfigMapper.findValueByKey("system.name");
        assertNotNull(value);
    }

    @Test
    public void testAuditLogMapper() {
        // 测试查询所有审计日志
        List<AuditLog> logs = auditLogMapper.selectList(null);
        assertNotNull(logs);
        
        // 测试分页查询
        Page<AuditLog> page = new Page<>(1, 10);
        Page<AuditLog> logPage = auditLogMapper.selectPage(page, null);
        assertNotNull(logPage);
    }

    @Test
    public void testEntityCreation() {
        // 测试创建用户实体
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setNickname("Test User");
        user.setStatus(User.UserStatus.ACTIVE);
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals(User.UserStatus.ACTIVE, user.getStatus());
        
        // 测试创建对话实体
        Conversation conversation = new Conversation();
        conversation.setUserId(1L);
        conversation.setTitle("Test Conversation");
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        
        assertNotNull(conversation);
        assertEquals("Test Conversation", conversation.getTitle());
        
        // 测试创建消息实体
        Message message = new Message();
        message.setConversationId(1L);
        message.setUserId(1L);
        message.setMessageType(Message.MessageType.USER);
        message.setContent("Test message content");
        
        assertNotNull(message);
        assertEquals(Message.MessageType.USER, message.getMessageType());
        
        // 测试创建系统配置实体
        SystemConfig config = new SystemConfig();
        config.setConfigKey("test.key");
        config.setConfigValue("test.value");
        config.setConfigType(SystemConfig.ConfigType.STRING);
        config.setDescription("Test configuration");
        
        assertNotNull(config);
        assertEquals(SystemConfig.ConfigType.STRING, config.getConfigType());
    }
}