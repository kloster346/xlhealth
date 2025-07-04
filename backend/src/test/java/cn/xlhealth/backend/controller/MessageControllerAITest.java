package cn.xlhealth.backend.controller;

import cn.xlhealth.backend.ui.dto.ApiResponse;
import cn.xlhealth.backend.ui.dto.ConversationCreateRequest;
import cn.xlhealth.backend.ui.dto.MessageSendRequest;
import cn.xlhealth.backend.ui.dto.LoginRequest;
import cn.xlhealth.backend.ui.dto.RegisterRequest;
import cn.xlhealth.backend.ui.dto.ConversationResponse;
import cn.xlhealth.backend.ui.dto.MessageResponse;
import cn.xlhealth.backend.ui.dto.AuthResponse;
import cn.xlhealth.backend.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI功能接口测试类
 * 测试通过消息接口触发AI回复的完整流程
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
                "ai.service.mock-mode=true",
                "ai.service.provider=MOCK",
                "ai.service.context.enabled=true",
                "ai.service.quality.enabled=true",
                "ai.service.monitoring.enabled=true"
})
class MessageControllerAITest {

        @Autowired
        private TestRestTemplate restTemplate;

        private String authToken;
        private Long testUserId;

        @BeforeEach
        void setUp() {
                // 创建测试用户并获取认证token
                authToken = createTestUserAndLogin();
        }

        /**
         * 测试发送消息触发AI回复的完整流程
         */
        @Test
        void testSendMessageWithAIReply() throws InterruptedException {
                // 1. 创建测试对话
                Long conversationId = createTestConversation();
                assertNotNull(conversationId);

                // 2. 发送情感支持类型的消息
                MessageSendRequest request = new MessageSendRequest();
                request.setContent("我感到很焦虑，不知道该怎么办");
                request.setRole(Message.MessageRole.USER);
                request.setContentType(Message.ContentType.TEXT);
                request.setMetadata("{\"emotionalState\":\"焦虑\"}");

                HttpHeaders headers = createAuthHeaders();
                HttpEntity<MessageSendRequest> entity = new HttpEntity<>(request, headers);

                // 3. 发送消息
                ResponseEntity<ApiResponse<MessageResponse>> response = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.POST,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<MessageResponse>>() {
                                });

                // 4. 验证用户消息发送成功
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());

                MessageResponse userMessage = response.getBody().getData();
                assertNotNull(userMessage);
                assertEquals(request.getContent(), userMessage.getContent());
                assertEquals(Message.MessageRole.USER, userMessage.getRole());

                // 5. 等待AI异步回复处理
                Thread.sleep(3000);

                // 6. 查询对话消息，验证AI回复
                ResponseEntity<ApiResponse<List<MessageResponse>>> messagesResponse = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                new ParameterizedTypeReference<ApiResponse<List<MessageResponse>>>() {
                                });

                assertEquals(HttpStatus.OK, messagesResponse.getStatusCode());
                List<MessageResponse> messages = messagesResponse.getBody().getData();

                // 7. 验证消息数量（用户消息 + AI回复）
                assertTrue(messages.size() >= 2, "应该包含用户消息和AI回复");

                // 8. 验证AI回复
                MessageResponse aiReply = messages.stream()
                                .filter(msg -> Message.MessageRole.ASSISTANT.equals(msg.getRole()))
                                .findFirst()
                                .orElse(null);

                assertNotNull(aiReply, "应该存在AI回复消息");
                assertNotNull(aiReply.getContent(), "AI回复内容不能为空");
                assertFalse(aiReply.getContent().trim().isEmpty(), "AI回复内容不能为空字符串");

                // 验证AI回复的特征
                String aiContent = aiReply.getContent().toLowerCase();
                assertTrue(
                                aiContent.contains("理解") ||
                                                aiContent.contains("焦虑") ||
                                                aiContent.contains("帮助") ||
                                                aiContent.contains("支持"),
                                "AI回复应该包含情感支持相关内容");

                System.out.println("用户消息: " + userMessage.getContent());
                System.out.println("AI回复: " + aiReply.getContent());
        }

        /**
         * 测试不同情感状态的AI回复
         */
        @Test
        void testDifferentEmotionalStatesAIReply() throws InterruptedException {
                Long conversationId = createTestConversation();

                // 测试不同情感状态
                String[] emotionalStates = { "焦虑", "抑郁", "愤怒", "恐惧", "平静", "快乐" };
                String[] messages = {
                                "我感到很焦虑",
                                "我心情很低落",
                                "我很生气",
                                "我很害怕",
                                "我感觉很平静",
                                "我今天很开心"
                };

                for (int i = 0; i < emotionalStates.length; i++) {
                        // 发送消息
                        MessageSendRequest request = new MessageSendRequest();
                        request.setContent(messages[i]);
                        request.setRole(Message.MessageRole.USER);
                        request.setContentType(Message.ContentType.TEXT);
                        request.setMetadata("{\"emotionalState\":\"" + emotionalStates[i] + "\"}");

                        HttpEntity<MessageSendRequest> entity = new HttpEntity<>(request, createAuthHeaders());

                        ResponseEntity<ApiResponse<MessageResponse>> response = restTemplate.exchange(
                                        "/api/v1/conversations/" + conversationId + "/messages",
                                        HttpMethod.POST,
                                        entity,
                                        new ParameterizedTypeReference<ApiResponse<MessageResponse>>() {
                                        });

                        assertEquals(HttpStatus.OK, response.getStatusCode());
                        assertTrue(response.getBody().isSuccess());

                        // 等待AI回复
                        Thread.sleep(2000);
                }

                // 验证所有消息和AI回复
                ResponseEntity<ApiResponse<List<MessageResponse>>> messagesResponse = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.GET,
                                new HttpEntity<>(createAuthHeaders()),
                                new ParameterizedTypeReference<ApiResponse<List<MessageResponse>>>() {
                                });

                List<MessageResponse> allMessages = messagesResponse.getBody().getData();

                // 应该有用户消息和AI回复
                long userMessages = allMessages.stream().filter(msg -> Message.MessageRole.USER.equals(msg.getRole()))
                                .count();
                long aiMessages = allMessages.stream()
                                .filter(msg -> Message.MessageRole.ASSISTANT.equals(msg.getRole())).count();

                assertEquals(emotionalStates.length, userMessages, "用户消息数量应该匹配");
                assertTrue(aiMessages > 0, "应该有AI回复消息");

                System.out.println("测试了 " + emotionalStates.length + " 种情感状态");
                System.out.println("生成了 " + aiMessages + " 条AI回复");
        }

        /**
         * 测试上下文保持功能
         */
        @Test
        void testContextPreservation() throws InterruptedException {
                Long conversationId = createTestConversation();

                // 第一条消息：建立上下文
                sendMessage(conversationId, "我叫张三，今年25岁", "平静");
                Thread.sleep(2000);

                // 第二条消息：引用之前的信息
                sendMessage(conversationId, "你还记得我的名字吗？", "期待");
                Thread.sleep(2000);

                // 获取所有消息
                ResponseEntity<ApiResponse<List<MessageResponse>>> messagesResponse = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.GET,
                                new HttpEntity<>(createAuthHeaders()),
                                new ParameterizedTypeReference<ApiResponse<List<MessageResponse>>>() {
                                });

                List<MessageResponse> messages = messagesResponse.getBody().getData();

                // 查找最后一条AI回复
                MessageResponse lastAIReply = messages.stream()
                                .filter(msg -> Message.MessageRole.ASSISTANT.equals(msg.getRole()))
                                .reduce((first, second) -> second)
                                .orElse(null);

                assertNotNull(lastAIReply, "应该存在AI回复");

                // AI回复应该包含用户名字或相关信息
                String aiContent = lastAIReply.getContent();
                assertTrue(
                                aiContent.contains("张三") ||
                                                aiContent.contains("记得") ||
                                                aiContent.contains("名字"),
                                "AI应该能够记住用户的名字或相关上下文信息");

                System.out.println("上下文测试 - AI回复: " + aiContent);
        }

        /**
         * 测试健康建议类型的AI回复
         */
        @Test
        void testHealthAdviceAIReply() throws InterruptedException {
                Long conversationId = createTestConversation();

                // 发送健康相关问题
                MessageSendRequest request = new MessageSendRequest();
                request.setContent("我应该如何保持健康？有什么运动建议吗？");
                request.setRole(Message.MessageRole.USER);
                request.setContentType(Message.ContentType.TEXT);
                request.setMetadata("{\"emotionalState\":\"期待\"}");

                HttpEntity<MessageSendRequest> entity = new HttpEntity<>(request, createAuthHeaders());

                ResponseEntity<ApiResponse<MessageResponse>> response = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.POST,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<MessageResponse>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                Thread.sleep(2000);

                // 获取AI回复
                ResponseEntity<ApiResponse<List<MessageResponse>>> messagesResponse = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.GET,
                                new HttpEntity<>(createAuthHeaders()),
                                new ParameterizedTypeReference<ApiResponse<List<MessageResponse>>>() {
                                });

                List<MessageResponse> messages = messagesResponse.getBody().getData();
                MessageResponse aiReply = messages.stream()
                                .filter(msg -> Message.MessageRole.ASSISTANT.equals(msg.getRole()))
                                .findFirst()
                                .orElse(null);

                assertNotNull(aiReply);
                String aiContent = aiReply.getContent().toLowerCase();

                // 验证健康建议相关内容
                assertTrue(
                                aiContent.contains("运动") ||
                                                aiContent.contains("健康") ||
                                                aiContent.contains("锻炼") ||
                                                aiContent.contains("饮食") ||
                                                aiContent.contains("睡眠"),
                                "AI回复应该包含健康建议相关内容");

                System.out.println("健康建议测试 - AI回复: " + aiReply.getContent());
        }

        // ==================== 辅助方法 ====================

        /**
         * 创建测试用户并登录获取token
         */
        private String createTestUserAndLogin() {
                String username = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
                String password = "Password123";
                String email = username + "@test.com";

                // 注册用户
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername(username);
                registerRequest.setPassword(password);
                registerRequest.setConfirmPassword(password);
                registerRequest.setEmail(email);
                registerRequest.setNickname("测试用户");

                ResponseEntity<ApiResponse<Void>> registerResponse = restTemplate.exchange(
                                "/api/v1/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(registerRequest),
                                new ParameterizedTypeReference<ApiResponse<Void>>() {
                                });

                if (registerResponse.getStatusCode() != HttpStatus.OK) {
                        System.out.println("Register failed with status: " + registerResponse.getStatusCode());
                        System.out.println("Response body: " + registerResponse.getBody());
                }
                assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

                // 登录获取token
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsernameOrEmail(username);
                loginRequest.setPassword(password);

                ResponseEntity<ApiResponse<AuthResponse>> loginResponse = restTemplate.exchange(
                                "/api/v1/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(loginRequest),
                                new ParameterizedTypeReference<ApiResponse<AuthResponse>>() {
                                });

                assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
                assertTrue(loginResponse.getBody().isSuccess());

                AuthResponse loginData = loginResponse.getBody().getData();
                this.testUserId = loginData.getUserInfo().getId();

                return loginData.getAccessToken();
        }

        /**
         * 创建测试对话
         */
        private Long createTestConversation() {
                ConversationCreateRequest request = new ConversationCreateRequest();
                request.setTitle("AI功能测试对话");

                HttpEntity<ConversationCreateRequest> entity = new HttpEntity<>(request, createAuthHeaders());

                ResponseEntity<ApiResponse<ConversationResponse>> response = restTemplate.exchange(
                                "/api/v1/conversations",
                                HttpMethod.POST,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<ConversationResponse>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertTrue(response.getBody().isSuccess());

                return response.getBody().getData().getId();
        }

        /**
         * 发送消息
         */
        private void sendMessage(Long conversationId, String content, String emotionalState) {
                MessageSendRequest request = new MessageSendRequest();
                request.setContent(content);
                request.setRole(Message.MessageRole.USER);
                request.setContentType(Message.ContentType.TEXT);
                request.setMetadata("{\"emotionalState\":\"" + emotionalState + "\"}");

                HttpEntity<MessageSendRequest> entity = new HttpEntity<>(request, createAuthHeaders());

                ResponseEntity<ApiResponse<MessageResponse>> response = restTemplate.exchange(
                                "/api/v1/conversations/" + conversationId + "/messages",
                                HttpMethod.POST,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<MessageResponse>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertTrue(response.getBody().isSuccess());
        }

        /**
         * 创建认证请求头
         */
        private HttpHeaders createAuthHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(authToken);
                return headers;
        }
}