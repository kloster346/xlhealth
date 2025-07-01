package cn.xlhealth.backend.util;

import cn.xlhealth.backend.entity.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实体类转换工具类
 * 提供实体类与DTO之间的转换方法
 */
public class EntityConverter {

    /**
     * 用户实体转换为响应DTO
     */
    public static UserResponseDto toUserResponseDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        BeanUtils.copyProperties(user, dto);
        // 不返回密码哈希
        dto.setPasswordHash(null);
        return dto;
    }

    /**
     * 用户请求DTO转换为实体
     */
    public static User toUserEntity(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    /**
     * 对话实体转换为响应DTO
     */
    public static ConversationResponseDto toConversationResponseDto(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        ConversationResponseDto dto = new ConversationResponseDto();
        BeanUtils.copyProperties(conversation, dto);
        return dto;
    }

    /**
     * 对话请求DTO转换为实体
     */
    public static Conversation toConversationEntity(ConversationRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Conversation conversation = new Conversation();
        BeanUtils.copyProperties(dto, conversation);
        return conversation;
    }

    /**
     * 消息实体转换为响应DTO
     */
    public static MessageResponseDto toMessageResponseDto(Message message) {
        if (message == null) {
            return null;
        }
        MessageResponseDto dto = new MessageResponseDto();
        BeanUtils.copyProperties(message, dto);
        return dto;
    }

    /**
     * 消息请求DTO转换为实体
     */
    public static Message toMessageEntity(MessageRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        return message;
    }

    /**
     * 系统配置实体转换为响应DTO
     */
    public static SystemConfigResponseDto toSystemConfigResponseDto(SystemConfig config) {
        if (config == null) {
            return null;
        }
        SystemConfigResponseDto dto = new SystemConfigResponseDto();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    /**
     * 系统配置请求DTO转换为实体
     */
    public static SystemConfig toSystemConfigEntity(SystemConfigRequestDto dto) {
        if (dto == null) {
            return null;
        }
        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(dto, config);
        return config;
    }

    /**
     * 审计日志实体转换为响应DTO
     */
    public static AuditLogResponseDto toAuditLogResponseDto(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        AuditLogResponseDto dto = new AuditLogResponseDto();
        BeanUtils.copyProperties(auditLog, dto);
        return dto;
    }

    /**
     * 用户会话实体转换为响应DTO
     */
    public static UserSessionResponseDto toUserSessionResponseDto(UserSession session) {
        if (session == null) {
            return null;
        }
        UserSessionResponseDto dto = new UserSessionResponseDto();
        BeanUtils.copyProperties(session, dto);
        // 不返回会话令牌
        dto.setSessionToken(null);
        return dto;
    }

    /**
     * 批量转换用户列表
     */
    public static List<UserResponseDto> toUserResponseDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(EntityConverter::toUserResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换对话列表
     */
    public static List<ConversationResponseDto> toConversationResponseDtoList(List<Conversation> conversations) {
        if (conversations == null) {
            return null;
        }
        return conversations.stream()
                .map(EntityConverter::toConversationResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换消息列表
     */
    public static List<MessageResponseDto> toMessageResponseDtoList(List<Message> messages) {
        if (messages == null) {
            return null;
        }
        return messages.stream()
                .map(EntityConverter::toMessageResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换系统配置列表
     */
    public static List<SystemConfigResponseDto> toSystemConfigResponseDtoList(List<SystemConfig> configs) {
        if (configs == null) {
            return null;
        }
        return configs.stream()
                .map(EntityConverter::toSystemConfigResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换审计日志列表
     */
    public static List<AuditLogResponseDto> toAuditLogResponseDtoList(List<AuditLog> logs) {
        if (logs == null) {
            return null;
        }
        return logs.stream()
                .map(EntityConverter::toAuditLogResponseDto)
                .collect(Collectors.toList());
    }

    // DTO 类定义
    public static class UserResponseDto {
        private Long id;
        private String username;
        private String email;
        private String passwordHash; // 将被设置为null
        private String nickname;
        private String avatarUrl;
        private User.UserStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLoginAt;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public User.UserStatus getStatus() {
            return status;
        }

        public void setStatus(User.UserStatus status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public LocalDateTime getLastLoginAt() {
            return lastLoginAt;
        }

        public void setLastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
        }
    }

    public static class UserRequestDto {
        private String username;
        private String email;
        private String password;
        private String nickname;
        private String avatarUrl;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    public static class ConversationResponseDto {
        private Long id;
        private Long userId;
        private String title;
        private Conversation.ConversationStatus status;
        private String metadata;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastMessageAt;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Conversation.ConversationStatus getStatus() {
            return status;
        }

        public void setStatus(Conversation.ConversationStatus status) {
            this.status = status;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public LocalDateTime getLastMessageAt() {
            return lastMessageAt;
        }

        public void setLastMessageAt(LocalDateTime lastMessageAt) {
            this.lastMessageAt = lastMessageAt;
        }
    }

    public static class ConversationRequestDto {
        private String title;
        private String metadata;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }
    }

    public static class MessageResponseDto {
        private Long id;
        private Long conversationId;
        private Long userId;
        private Message.MessageType messageType;
        private String content;
        private String metadata;
        private LocalDateTime createdAt;
        private Boolean isDeleted;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getConversationId() {
            return conversationId;
        }

        public void setConversationId(Long conversationId) {
            this.conversationId = conversationId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Message.MessageType getMessageType() {
            return messageType;
        }

        public void setMessageType(Message.MessageType messageType) {
            this.messageType = messageType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public Boolean getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
        }
    }

    public static class MessageRequestDto {
        private String content;
        private String metadata;

        // Getters and Setters
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }
    }

    public static class SystemConfigResponseDto {
        private Long id;
        private String configKey;
        private String configValue;
        private SystemConfig.ConfigType configType;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }

        public SystemConfig.ConfigType getConfigType() {
            return configType;
        }

        public void setConfigType(SystemConfig.ConfigType configType) {
            this.configType = configType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class SystemConfigRequestDto {
        private String configKey;
        private String configValue;
        private SystemConfig.ConfigType configType;
        private String description;

        // Getters and Setters
        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }

        public SystemConfig.ConfigType getConfigType() {
            return configType;
        }

        public void setConfigType(SystemConfig.ConfigType configType) {
            this.configType = configType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class AuditLogResponseDto {
        private Long id;
        private Long userId;
        private String action;
        private String resourceType;
        private String resourceId;
        private String details;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime createdAt;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class UserSessionResponseDto {
        private Long id;
        private Long userId;
        private String sessionToken;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private Boolean isActive;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }
}