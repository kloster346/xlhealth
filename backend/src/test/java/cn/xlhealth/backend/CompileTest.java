package cn.xlhealth.backend;

import cn.xlhealth.backend.entity.*;
import cn.xlhealth.backend.mapper.*;
import cn.xlhealth.backend.util.EntityConverter;
import cn.xlhealth.backend.ui.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 编译测试类 - 验证TASK003代码的编译正确性
 * 不依赖数据库连接，仅验证代码结构和类型安全
 */
@SpringBootTest
public class CompileTest {

    @Test
    public void testEntityClassesCompilation() {
        // 测试User实体类
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setNickname("Test User");
        user.setAvatarUrl("http://example.com/avatar.jpg");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals(User.UserStatus.ACTIVE, user.getStatus());
        
        // 测试Conversation实体类
        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setUserId(1L);
        conversation.setTitle("Test Conversation");
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        conversation.setMetadata("{\"key\": \"value\"}");
        conversation.setCreatedTime(LocalDateTime.now());
        conversation.setUpdatedTime(LocalDateTime.now());
        conversation.setLastMessageTime(LocalDateTime.now());
        
        assertNotNull(conversation);
        assertEquals("Test Conversation", conversation.getTitle());
        assertEquals(Conversation.ConversationStatus.ACTIVE, conversation.getStatus());
        
        // 测试Message实体类
        Message message = new Message();
        message.setId(1L);
        message.setConversationId(1L);
        message.setUserId(1L);
        message.setMessageType(Message.MessageType.USER);
        message.setContent("Test message content");
        // message.setMetadata("{\"key\": \"value\"}"); // Message实体没有metadata字段
        message.setCreatedTime(LocalDateTime.now());
        message.setDeleted(false);
        
        assertNotNull(message);
        assertEquals("Test message content", message.getContent());
        assertEquals(Message.MessageType.USER, message.getMessageType());
        
        // 测试SystemConfig实体类
        SystemConfig config = new SystemConfig();
        config.setConfigKey("test.key");
        config.setConfigValue("test.value");
        config.setConfigType(SystemConfig.ConfigType.NUMBER);
        config.setDescription("Test configuration");
        config.setCreatedTime(LocalDateTime.now());
        config.setUpdatedTime(LocalDateTime.now());
        
        assertEquals("test.key", config.getConfigKey());
        assertEquals("test.value", config.getConfigValue());
        assertEquals(SystemConfig.ConfigType.NUMBER, config.getConfigType());
        assertEquals("Test configuration", config.getDescription());
        assertNotNull(config.getCreatedTime());
        assertNotNull(config.getUpdatedTime());
        
        // 测试AuditLog实体类
        AuditLog auditLog = new AuditLog();
        auditLog.setId(1L);
        auditLog.setUserId(1L);
        auditLog.setAction("CREATE");
        auditLog.setResourceType("USER");
        auditLog.setResourceId(1L);
        auditLog.setOldValues("{}");
        auditLog.setNewValues("{\"name\":\"test\"}");
        auditLog.setIpAddress("192.168.1.1");
        auditLog.setCreatedTime(LocalDateTime.now());
        
        assertNotNull(auditLog.getId());
        assertEquals(Long.valueOf(1L), auditLog.getUserId());
        assertEquals("CREATE", auditLog.getAction());
        assertEquals("USER", auditLog.getResourceType());
        assertEquals(Long.valueOf(1L), auditLog.getResourceId());
        assertEquals("{}", auditLog.getOldValues());
        assertEquals("{\"name\":\"test\"}", auditLog.getNewValues());
        assertEquals("192.168.1.1", auditLog.getIpAddress());
        assertNotNull(auditLog.getCreatedTime());
        
        // 测试UserSession实体类
        UserSession session = new UserSession();
        session.setSessionToken("session123");
        session.setUserId(1L);
        session.setIpAddress("192.168.1.1");
        session.setUserAgent("Test Agent");
        session.setCreatedTime(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusHours(24));
        session.setLastActivityTime(LocalDateTime.now());
        
        assertEquals("session123", session.getSessionToken());
        assertEquals(Long.valueOf(1L), session.getUserId());
        assertEquals("192.168.1.1", session.getIpAddress());
        assertEquals("Test Agent", session.getUserAgent());
        assertNotNull(session.getCreatedTime());
        assertNotNull(session.getExpiresAt());
        assertNotNull(session.getLastActivityTime());
    }
    
    @Test
    public void testMapperInterfacesCompilation() {
        // 验证Mapper接口可以正确编译
        // 这里只是验证类型定义，不执行实际的数据库操作
        
        Class<?> userMapperClass = UserMapper.class;
        Class<?> conversationMapperClass = ConversationMapper.class;
        Class<?> messageMapperClass = MessageMapper.class;
        Class<?> systemConfigMapperClass = SystemConfigMapper.class;
        Class<?> auditLogMapperClass = AuditLogMapper.class;
        Class<?> userSessionMapperClass = UserSessionMapper.class;
        
        assertNotNull(userMapperClass);
        assertNotNull(conversationMapperClass);
        assertNotNull(messageMapperClass);
        assertNotNull(systemConfigMapperClass);
        assertNotNull(auditLogMapperClass);
        assertNotNull(userSessionMapperClass);
        
        // 验证Mapper接口继承了BaseMapper
        assertTrue(com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(userMapperClass));
        assertTrue(com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(conversationMapperClass));
        assertTrue(com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(messageMapperClass));
        assertTrue(com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(systemConfigMapperClass));
        assertTrue(com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(auditLogMapperClass));
        assertTrue(com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(userSessionMapperClass));
    }
    
    @Test
    public void testEntityConverterCompilation() {
        // 测试实体转换工具类
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setNickname("Test User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedTime(LocalDateTime.now());
        
        // 测试用户实体转换
        EntityConverter.UserResponseDto userDto = EntityConverter.toUserResponseDto(user);
        assertNotNull(userDto);
        assertEquals("testuser", userDto.getUsername());
        assertEquals("test@example.com", userDto.getEmail());
        assertNull(userDto.getPasswordHash()); // 密码应该被清除
        
        // 测试用户请求DTO转换
        EntityConverter.UserRequestDto requestDto = new EntityConverter.UserRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setEmail("new@example.com");
        requestDto.setPassword("password");
        
        User convertedUser = EntityConverter.toUserEntity(requestDto);
        assertNotNull(convertedUser);
        assertEquals("newuser", convertedUser.getUsername());
        assertEquals("new@example.com", convertedUser.getEmail());
        
        // 测试对话实体转换
        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setUserId(1L);
        conversation.setTitle("Test Conversation");
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        
        EntityConverter.ConversationResponseDto conversationDto = EntityConverter.toConversationResponseDto(conversation);
        assertNotNull(conversationDto);
        assertEquals("Test Conversation", conversationDto.getTitle());
        assertEquals(Conversation.ConversationStatus.ACTIVE, conversationDto.getStatus());
        
        // 测试消息实体转换
        Message message = new Message();
        message.setId(1L);
        message.setConversationId(1L);
        message.setUserId(1L);
        message.setMessageType(Message.MessageType.USER);
        message.setContent("Test message");
        
        EntityConverter.MessageResponseDto messageDto = EntityConverter.toMessageResponseDto(message);
        assertNotNull(messageDto);
        assertEquals("Test message", messageDto.getContent());
        assertEquals(Message.MessageType.USER, messageDto.getMessageType());
        
        // 测试批量转换
        List<User> users = Arrays.asList(user);
        List<EntityConverter.UserResponseDto> userDtos = EntityConverter.toUserResponseDtoList(users);
        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
        assertEquals("testuser", userDtos.get(0).getUsername());
    }
    
    @Test
    public void testApiResponseCompilation() {
        // 测试ApiResponse类
        ApiResponse<String> successResponse = ApiResponse.success("Hello World");
        assertNotNull(successResponse);
        assertTrue(successResponse.isSuccess());
        assertEquals("SUCCESS", successResponse.getCode());
        assertEquals("Hello World", successResponse.getData());
        
        ApiResponse<Void> errorResponse = ApiResponse.error("BAD_REQUEST", "Bad request");
        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertEquals("BAD_REQUEST", errorResponse.getCode());
        assertEquals("Bad request", errorResponse.getMessage());
        
        ApiResponse<Void> badRequestResponse = ApiResponse.badRequest("Operation failed");
        assertNotNull(badRequestResponse);
        assertFalse(badRequestResponse.isSuccess());
        assertEquals("BAD_REQUEST", badRequestResponse.getCode());
        assertEquals("Operation failed", badRequestResponse.getMessage());
    }
    
    @Test
    public void testEnumCompilation() {
        // 测试枚举类型
        User.UserStatus[] userStatuses = User.UserStatus.values();
        assertTrue(userStatuses.length > 0);
        assertNotNull(User.UserStatus.ACTIVE);
        assertNotNull(User.UserStatus.INACTIVE);
        assertNotNull(User.UserStatus.SUSPENDED);
        
        Conversation.ConversationStatus[] conversationStatuses = Conversation.ConversationStatus.values();
        assertTrue(conversationStatuses.length > 0);
        assertNotNull(Conversation.ConversationStatus.ACTIVE);
        assertNotNull(Conversation.ConversationStatus.ARCHIVED);
        assertNotNull(Conversation.ConversationStatus.DELETED);
        
        Message.MessageType[] messageTypes = Message.MessageType.values();
        assertTrue(messageTypes.length > 0);
        assertNotNull(Message.MessageType.USER);
        assertNotNull(Message.MessageType.ASSISTANT);
        assertNotNull(Message.MessageType.SYSTEM);
        
        SystemConfig.ConfigType[] configTypes = SystemConfig.ConfigType.values();
        assertTrue(configTypes.length > 0);
        assertNotNull(SystemConfig.ConfigType.STRING);
        assertNotNull(SystemConfig.ConfigType.NUMBER);
        assertNotNull(SystemConfig.ConfigType.BOOLEAN);
        assertNotNull(SystemConfig.ConfigType.JSON);
    }
}