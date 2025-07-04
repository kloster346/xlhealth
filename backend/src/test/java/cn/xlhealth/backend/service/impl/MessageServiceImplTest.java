package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.entity.Message;
import cn.xlhealth.backend.mapper.MessageMapper;
import cn.xlhealth.backend.service.ConversationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageServiceImpl 测试类
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

        @Mock
        private MessageMapper messageMapper;

        @Mock
        private ConversationService conversationService;

        @InjectMocks
        private MessageServiceImpl messageService;

        private Message testMessage;
        private Long testUserId = 1L;
        private Long testConversationId = 1L;
        private Long testMessageId = 1L;

        @BeforeEach
        void setUp() {
                testMessage = new Message();
                testMessage.setId(testMessageId);
                testMessage.setConversationId(testConversationId);
                testMessage.setUserId(testUserId);
                testMessage.setRole(Message.MessageRole.USER);
                testMessage.setContent("测试消息内容");
                testMessage.setContentType(Message.ContentType.TEXT);
                testMessage.setStatus(Message.MessageStatus.SUCCESS);
                testMessage.setCreatedTime(LocalDateTime.now());
                testMessage.setUpdatedTime(LocalDateTime.now());
                testMessage.setDeleted(false);
        }

        @Test
        void testSendMessage() {
                // Mock ConversationService 验证权限
                when(conversationService.getConversationById(testConversationId, testUserId))
                                .thenReturn(null); // 简化测试，实际应返回Conversation对象

                // Mock MessageMapper insert
                when(messageMapper.insert(any(Message.class))).thenReturn(1);

                // 执行测试
                Message result = messageService.sendMessage(
                                testConversationId,
                                testUserId,
                                "测试消息",
                                Message.MessageRole.USER,
                                Message.ContentType.TEXT,
                                null);

                // 验证结果
                assertNotNull(result);
                assertEquals(testConversationId, result.getConversationId());
                assertEquals(testUserId, result.getUserId());
                assertEquals(Message.MessageRole.USER, result.getRole());
                assertEquals("测试消息", result.getContent());
                assertEquals(Message.ContentType.TEXT, result.getContentType());
                assertEquals(Message.MessageStatus.SUCCESS, result.getStatus());
                assertFalse(result.getDeleted());

                // 验证方法调用
                verify(messageMapper, times(1)).insert(any(Message.class));
        }

        @Test
        void testGetMessageById() {
                // Mock MessageMapper selectById
                when(messageMapper.selectById(testMessageId)).thenReturn(testMessage);

                // Mock ConversationService 验证权限
                when(conversationService.getConversationById(testConversationId, testUserId))
                                .thenReturn(null); // 简化测试

                // 执行测试
                Message result = messageService.getMessageById(testMessageId, testUserId);

                // 验证结果
                assertNotNull(result);
                assertEquals(testMessageId, result.getId());
                assertEquals(testConversationId, result.getConversationId());
                assertEquals(testUserId, result.getUserId());

                // 验证方法调用
                verify(messageMapper, times(1)).selectById(testMessageId);
        }

        @Test
        void testDeleteMessage() {
                // Mock MessageMapper selectById
                when(messageMapper.selectById(testMessageId)).thenReturn(testMessage);

                // Mock ConversationService 验证权限
                when(conversationService.getConversationById(testConversationId, testUserId))
                                .thenReturn(null); // 简化测试

                // Mock MessageMapper update
                when(messageMapper.update(eq(null), any())).thenReturn(1);

                // 执行测试
                boolean result = messageService.deleteMessage(testMessageId, testUserId);

                // 验证结果
                assertTrue(result);

                // 验证方法调用
                verify(messageMapper, times(1)).update(eq(null), any());
        }

        @Test
        void testBatchDeleteMessages() {
                List<Long> messageIds = Arrays.asList(1L, 2L, 3L);

                // Mock MessageMapper selectById for each message
                when(messageMapper.selectById(anyLong())).thenReturn(testMessage);

                // Mock ConversationService 验证权限
                when(conversationService.getConversationById(testConversationId, testUserId))
                                .thenReturn(null); // 简化测试

                // Mock MessageMapper update
                when(messageMapper.update(eq(null), any())).thenReturn(3);

                // Mock MessageMapper selectList for deleted messages
                when(messageMapper.selectList(any(QueryWrapper.class)))
                                .thenReturn(Arrays.asList(testMessage, testMessage, testMessage));

                // 执行测试
                Integer result = messageService.batchDeleteMessages(messageIds, testUserId);

                // 验证结果
                assertEquals(3, result);

                // 验证方法调用
                verify(messageMapper, times(1)).update(eq(null), any());
        }

        @Test
        void testCountConversationMessages() {
                // Mock ConversationService 验证权限
                when(conversationService.getConversationById(testConversationId, testUserId))
                                .thenReturn(null); // 简化测试

                // Mock MessageMapper selectCount
                when(messageMapper.selectCount(any(QueryWrapper.class))).thenReturn(5L);

                // 执行测试
                Long result = messageService.countConversationMessages(testConversationId, testUserId);

                // 验证结果
                assertEquals(5L, result);

                // 验证方法调用
                verify(messageMapper, times(1)).selectCount(any(QueryWrapper.class));
        }

        @Test
        void testGenerateAIReply() {
                // Mock ConversationService 验证权限
                when(conversationService.getConversationById(testConversationId, testUserId))
                                .thenReturn(null); // 简化测试

                // Mock MessageMapper insert
                when(messageMapper.insert(any(Message.class))).thenReturn(1);

                // 执行测试
                Message result = messageService.generateAIReply(
                                testConversationId,
                                testUserId,
                                "用户消息");

                // 验证结果
                assertNotNull(result);
                assertEquals(testConversationId, result.getConversationId());
                assertNull(result.getUserId()); // AI消息没有用户ID
                assertEquals(Message.MessageRole.ASSISTANT, result.getRole());
                assertTrue(result.getContent().contains("用户消息"));
                assertEquals(Message.ContentType.TEXT, result.getContentType());
                assertEquals(Message.MessageStatus.SUCCESS, result.getStatus());
                assertEquals("gpt-3.5-turbo", result.getModelName());
                assertFalse(result.getDeleted());

                // 验证方法调用
                verify(messageMapper, times(1)).insert(any(Message.class));
        }

        @Test
        void testGetUserMessageStatistics() {
                // Mock MessageMapper selectCount for different queries
                when(messageMapper.selectCount(any(QueryWrapper.class)))
                                .thenReturn(10L) // total messages
                                .thenReturn(6L) // user messages
                                .thenReturn(4L); // assistant messages

                // Mock MessageMapper selectList for token count
                Message tokenMessage = new Message();
                tokenMessage.setTokenCount(1000);
                when(messageMapper.selectList(any(QueryWrapper.class)))
                                .thenReturn(Arrays.asList(tokenMessage));

                // 执行测试
                MessageServiceImpl.MessageStatistics result = (MessageServiceImpl.MessageStatistics) messageService
                                .getUserMessageStatistics(testUserId, null);

                // 验证结果
                assertNotNull(result);
                assertEquals(10L, result.getTotalMessages());
                assertEquals(6L, result.getUserMessages());
                assertEquals(4L, result.getAiMessages());
                assertEquals(1000, result.getTotalTokens());

                // 验证方法调用次数
                verify(messageMapper, times(3)).selectCount(any(QueryWrapper.class));
                verify(messageMapper, times(1)).selectList(any(QueryWrapper.class));
        }
}