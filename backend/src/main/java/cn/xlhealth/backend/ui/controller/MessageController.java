package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.entity.Message;
import cn.xlhealth.backend.service.MessageService;
import cn.xlhealth.backend.ui.dto.*;
import cn.xlhealth.backend.ui.dto.ApiResponse;
import cn.xlhealth.backend.dto.AIReplyRequest;
import cn.xlhealth.backend.ui.dto.request.BatchDeleteMessageRequest;
import cn.xlhealth.backend.ui.dto.request.MessageStatusUpdateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import cn.xlhealth.backend.ui.advice.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 消息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
@Tag(name = "消息管理", description = "消息发送、获取、删除等操作")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    @Operation(summary = "发送消息", description = "发送新消息到指定对话")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @Valid @RequestBody MessageSendRequest request) {
        try {
            Long userId = getCurrentUserId();

            Message message = messageService.sendMessage(
                    conversationId,
                    userId,
                    request.getContent(),
                    request.getRole(),
                    request.getContentType(),
                    request.getMetadata());

            MessageResponse response = convertToMessageResponse(message);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取对话消息列表
     */
    @GetMapping
    @Operation(summary = "获取对话消息列表", description = "分页获取指定对话的消息列表")
    public ResponseEntity<ApiResponse<IPage<MessageResponse>>> getConversationMessages(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "created_time") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Long userId = getCurrentUserId();

            Page<Message> pageParam = new Page<>(page, size);
            IPage<Message> messagePage = messageService.getConversationMessages(
                    conversationId, userId, pageParam, sortBy, sortOrder);

            // 转换为响应DTO
            IPage<MessageResponse> responsePage = messagePage.convert(this::convertToMessageResponse);

            return ResponseEntity.ok(ApiResponse.success(responsePage));
        } catch (Exception e) {
            log.error("获取对话消息列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    @Operation(summary = "获取消息详情", description = "根据消息ID获取消息详细信息")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            Long userId = getCurrentUserId();

            Message message = messageService.getMessageById(messageId, userId);
            MessageResponse response = convertToMessageResponse(message);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("获取消息详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息", description = "软删除指定消息")
    public ResponseEntity<ApiResponse<Boolean>> deleteMessage(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            Long userId = getCurrentUserId();

            boolean success = messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok(ApiResponse.success(success));
        } catch (Exception e) {
            log.error("删除消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除消息", description = "批量软删除多条消息")
    public ResponseEntity<ApiResponse<Boolean>> batchDeleteMessages(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @Parameter(description = "批量删除请求") @RequestBody @Valid BatchDeleteMessageRequest request) {
        try {
            Long userId = getCurrentUserId();

            Integer deletedCount = messageService.batchDeleteMessages(request.getMessageIds(), userId);
            boolean success = deletedCount != null && deletedCount > 0;
            return ResponseEntity.ok(ApiResponse.success(success));
        } catch (Exception e) {
            log.error("批量删除消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 统计对话消息数量
     */
    @GetMapping("/count")
    @Operation(summary = "统计对话消息数量", description = "获取指定对话的消息总数")
    public ResponseEntity<ApiResponse<Long>> countConversationMessages(
            @Parameter(description = "对话ID") @PathVariable Long conversationId) {
        try {
            Long userId = getCurrentUserId();

            Long count = messageService.countConversationMessages(conversationId, userId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            log.error("统计对话消息数量失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取对话最后一条消息
     */
    @GetMapping("/last")
    @Operation(summary = "获取对话最后一条消息", description = "获取指定对话的最新消息")
    public ResponseEntity<ApiResponse<MessageResponse>> getLastConversationMessage(
            @Parameter(description = "对话ID") @PathVariable Long conversationId) {
        try {
            Long userId = getCurrentUserId();

            Message message = messageService.getLastConversationMessage(conversationId, userId);
            if (message == null) {
                return ResponseEntity.ok(ApiResponse.success(null));
            }

            MessageResponse response = convertToMessageResponse(message);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("获取对话最后一条消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 清空对话所有消息
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空对话所有消息", description = "软删除指定对话的所有消息")
    public ResponseEntity<ApiResponse<Boolean>> clearConversationMessages(
            @Parameter(description = "对话ID") @PathVariable Long conversationId) {
        try {
            Long userId = getCurrentUserId();

            boolean success = messageService.clearConversationMessages(conversationId, userId);
            return ResponseEntity.ok(ApiResponse.success(success));
        } catch (Exception e) {
            log.error("清空对话所有消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 生成AI回复
     */
    @PostMapping("/ai-reply")
    @Operation(summary = "生成AI回复", description = "根据用户输入生成AI回复")
    public ApiResponse<Message> generateAIReply(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @RequestBody @Valid AIReplyRequest request) {
        try {
            Long userId = getCurrentUserId();
            Message aiReply = messageService.generateAIReply(conversationId, userId, request);
            return ApiResponse.success(aiReply);
        } catch (Exception e) {
            log.error("生成AI回复失败", e);
            return ApiResponse.error("生成AI回复失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户消息统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取用户消息统计", description = "获取当前用户的消息统计信息")
    public ResponseEntity<ApiResponse<MessageStatisticsResponse>> getUserMessageStatistics(
            @Parameter(description = "对话ID") @PathVariable Long conversationId) {
        try {
            Long userId = getCurrentUserId();

            MessageService.MessageStatistics statistics = messageService.getUserMessageStatistics(userId,
                    conversationId);

            MessageStatisticsResponse response = convertToStatisticsResponse(statistics);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("获取用户消息统计失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新消息状态
     */
    @PutMapping("/{messageId}/status")
    @Operation(summary = "更新消息状态", description = "更新指定消息的状态")
    public ResponseEntity<ApiResponse<Boolean>> updateMessageStatus(
            @Parameter(description = "对话ID") @PathVariable Long conversationId,
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "状态更新请求") @RequestBody @Valid MessageStatusUpdateRequest request) {
        try {
            Long userId = getCurrentUserId();
            boolean success = messageService.updateMessageStatus(messageId, request.getStatus(), userId);
            return ResponseEntity.ok(ApiResponse.success(success));
        } catch (BusinessException e) {
            log.error("更新消息状态失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新消息状态失败", e);
            throw BusinessException.badRequest("更新消息状态失败: " + e.getMessage());
        }
    }

    /**
     * 转换Message实体为MessageResponse DTO
     */
    private MessageResponse convertToMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        BeanUtils.copyProperties(message, response);
        return response;
    }

    /**
     * 转换MessageStatistics为MessageStatisticsResponse DTO
     */
    private MessageStatisticsResponse convertToStatisticsResponse(MessageService.MessageStatistics statistics) {
        MessageStatisticsResponse response = new MessageStatisticsResponse();
        response.setTotalMessages(statistics.getTotalMessages());
        response.setUserMessages(statistics.getUserMessages());
        response.setAssistantMessages(statistics.getAiMessages());
        // 将Long类型的totalTokens转换为Integer类型
        if (statistics.getTotalTokens() != null) {
            response.setTotalTokens(statistics.getTotalTokens().intValue());
        }
        return response;
    }

    /**
     * 获取当前登录用户ID
     * 注意：JWT验证后，authentication.getName()直接返回用户ID字符串
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.unauthorized("用户未登录");
        }

        String userIdStr = authentication.getName();
        if ("anonymousUser".equals(userIdStr)) {
            throw BusinessException.unauthorized("用户未登录");
        }

        try {
            // 直接将用户ID字符串转换为Long类型
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.error("无效的用户ID格式: {}", userIdStr, e);
            throw BusinessException.unauthorized("用户认证信息异常");
        }
    }
}