package cn.xlhealth.backend.controller;

import cn.xlhealth.backend.entity.Conversation;
import cn.xlhealth.backend.service.ConversationService;
import cn.xlhealth.backend.ui.controller.ConversationController;
import cn.xlhealth.backend.ui.dto.ConversationCreateRequest;
import cn.xlhealth.backend.ui.dto.ConversationUpdateRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConversationControllerTest {

    @Mock
    private ConversationService conversationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ConversationController conversationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Conversation testConversation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(conversationController).build();
        objectMapper = new ObjectMapper();
        
        // 创建测试数据
        testConversation = new Conversation();
        testConversation.setId(1L);
        testConversation.setUserId(1L);
        testConversation.setTitle("测试对话");
        testConversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        testConversation.setMessageCount(0);
        testConversation.setTotalTokens(0);
        testConversation.setCreatedTime(LocalDateTime.now());
        testConversation.setUpdatedTime(LocalDateTime.now());
        testConversation.setDeleted(false);
        
        // Mock authentication
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void testCreateConversation() throws Exception {
        // 准备测试数据
        ConversationCreateRequest request = new ConversationCreateRequest();
        request.setTitle("新对话");
        
        when(conversationService.createConversation(eq(1L), eq("新对话")))
                .thenReturn(testConversation);
        
        // 执行测试
        mockMvc.perform(post("/api/v1/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("对话创建成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试对话"));
        
        verify(conversationService).createConversation(1L, "新对话");
    }

    @Test
    void testGetUserConversations() throws Exception {
        // 准备测试数据
        Page<Conversation> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testConversation));
        page.setTotal(1);
        
        when(conversationService.getUserConversations(eq(1L), any(Page.class)))
                .thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/conversations")
                        .param("current", "1")
                        .param("size", "10")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(1));
        
        verify(conversationService).getUserConversations(eq(1L), any(Page.class));
    }

    @Test
    void testGetConversationById() throws Exception {
        when(conversationService.getConversationById(1L, 1L))
                .thenReturn(testConversation);
        
        mockMvc.perform(get("/api/v1/conversations/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试对话"));
        
        verify(conversationService).getConversationById(1L, 1L);
    }

    @Test
    void testGetConversationByIdNotFound() throws Exception {
        when(conversationService.getConversationById(1L, 1L))
                .thenReturn(null);
        
        mockMvc.perform(get("/api/v1/conversations/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("对话不存在或无权限访问"));
        
        verify(conversationService).getConversationById(1L, 1L);
    }

    @Test
    void testUpdateConversation() throws Exception {
        ConversationUpdateRequest request = new ConversationUpdateRequest();
        request.setTitle("更新后的标题");
        request.setStatus(Conversation.ConversationStatus.ARCHIVED);
        
        Conversation updatedConversation = new Conversation();
        updatedConversation.setId(1L);
        updatedConversation.setUserId(1L);
        updatedConversation.setTitle("更新后的标题");
        updatedConversation.setStatus(Conversation.ConversationStatus.ARCHIVED);
        
        when(conversationService.updateConversation(eq(1L), eq(1L), eq("更新后的标题"), 
                eq(Conversation.ConversationStatus.ARCHIVED)))
                .thenReturn(updatedConversation);
        
        mockMvc.perform(put("/api/v1/conversations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("对话更新成功"))
                .andExpect(jsonPath("$.data.title").value("更新后的标题"));
        
        verify(conversationService).updateConversation(1L, 1L, "更新后的标题", 
                Conversation.ConversationStatus.ARCHIVED);
    }

    @Test
    void testDeleteConversation() throws Exception {
        when(conversationService.deleteConversation(1L, 1L))
                .thenReturn(true);
        
        mockMvc.perform(delete("/api/v1/conversations/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("对话删除成功"));
        
        verify(conversationService).deleteConversation(1L, 1L);
    }

    @Test
    void testDeleteConversationNotFound() throws Exception {
        when(conversationService.deleteConversation(1L, 1L))
                .thenReturn(false);
        
        mockMvc.perform(delete("/api/v1/conversations/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("对话不存在或无权限访问"));
        
        verify(conversationService).deleteConversation(1L, 1L);
    }

    @Test
    void testArchiveConversation() throws Exception {
        when(conversationService.archiveConversation(1L, 1L))
                .thenReturn(true);
        
        mockMvc.perform(put("/api/v1/conversations/1/archive")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("对话归档成功"));
        
        verify(conversationService).archiveConversation(1L, 1L);
    }

    @Test
    void testActivateConversation() throws Exception {
        when(conversationService.activateConversation(1L, 1L))
                .thenReturn(true);
        
        mockMvc.perform(put("/api/v1/conversations/1/activate")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("对话激活成功"));
        
        verify(conversationService).activateConversation(1L, 1L);
    }

    @Test
    void testGetConversationStats() throws Exception {
        when(conversationService.countUserConversations(1L))
                .thenReturn(5L);
        when(conversationService.getUserConversationsByStatus(1L, Conversation.ConversationStatus.ACTIVE))
                .thenReturn(Arrays.asList(testConversation, testConversation));
        when(conversationService.getUserConversationsByStatus(1L, Conversation.ConversationStatus.ARCHIVED))
                .thenReturn(Arrays.asList(testConversation));
        
        mockMvc.perform(get("/api/v1/conversations/stats")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(5))
                .andExpect(jsonPath("$.data.active").value(2))
                .andExpect(jsonPath("$.data.archived").value(1));
        
        verify(conversationService).countUserConversations(1L);
        verify(conversationService).getUserConversationsByStatus(1L, Conversation.ConversationStatus.ACTIVE);
        verify(conversationService).getUserConversationsByStatus(1L, Conversation.ConversationStatus.ARCHIVED);
    }

    @Test
    void testGetConversationsByStatus() throws Exception {
        List<Conversation> conversations = Arrays.asList(testConversation);
        when(conversationService.getUserConversationsByStatus(1L, Conversation.ConversationStatus.ACTIVE))
                .thenReturn(conversations);
        
        mockMvc.perform(get("/api/v1/conversations/status/ACTIVE")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
        
        verify(conversationService).getUserConversationsByStatus(1L, Conversation.ConversationStatus.ACTIVE);
    }
}