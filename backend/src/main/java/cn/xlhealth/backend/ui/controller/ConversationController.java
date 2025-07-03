package cn.xlhealth.backend.ui.controller;

import cn.xlhealth.backend.entity.Conversation;
import cn.xlhealth.backend.service.ConversationService;
import cn.xlhealth.backend.ui.dto.ApiResponse;
import cn.xlhealth.backend.ui.dto.ConversationCreateRequest;
import cn.xlhealth.backend.ui.dto.ConversationResponse;
import cn.xlhealth.backend.ui.dto.ConversationUpdateRequest;
import cn.xlhealth.backend.ui.dto.PageResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话管理控制器
 */
@RestController
@RequestMapping("/api/v1/conversations")
@Tag(name = "对话管理", description = "对话会话相关接口")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 创建新对话
     */
    @PostMapping
    @Operation(summary = "创建对话", description = "创建一个新的对话会话")
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(
            @Valid @RequestBody ConversationCreateRequest request,
            Authentication authentication) {

        // 从认证信息中获取用户ID（现在authentication.getName()直接返回用户ID字符串）
        Long userId = Long.valueOf(authentication.getName());
        Conversation conversation = conversationService.createConversation(userId, request.getTitle());
        ConversationResponse response = new ConversationResponse(conversation);

        return ResponseEntity.ok(ApiResponse.success("对话创建成功", response));
    }

    /**
     * 获取用户对话列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取对话列表", description = "分页获取当前用户的对话列表")
    public ResponseEntity<ApiResponse<PageResponse<ConversationResponse>>> getUserConversations(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        Page<Conversation> page = new Page<>(current, size);
        IPage<Conversation> conversationPage = conversationService.getUserConversations(userId, page);

        List<ConversationResponse> responseList = conversationPage.getRecords().stream()
                .map(ConversationResponse::new)
                .collect(Collectors.toList());

        PageResponse<ConversationResponse> pageResponse = PageResponse.<ConversationResponse>builder()
                .records(responseList)
                .total(conversationPage.getTotal())
                .current(conversationPage.getCurrent())
                .size(conversationPage.getSize())
                .build();
        pageResponse.calculateAll();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * 获取对话详情
     */
    @GetMapping("/{conversationId}")
    @Operation(summary = "获取对话详情", description = "根据ID获取对话的详细信息")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversationById(
            @Parameter(description = "对话ID", example = "1") @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        Conversation conversation = conversationService.getConversationById(conversationId, userId);

        if (conversation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("对话不存在或无权限访问"));
        }

        ConversationResponse response = new ConversationResponse(conversation);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新对话信息
     */
    @PutMapping("/{conversationId}")
    @Operation(summary = "更新对话", description = "更新对话的标题或状态")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @Parameter(description = "对话ID", example = "1") @PathVariable Long conversationId,
            @Valid @RequestBody ConversationUpdateRequest request,
            Authentication authentication) {

        try {
            Long userId = Long.valueOf(authentication.getName());
            Conversation conversation = conversationService.updateConversation(
                    conversationId, userId, request.getTitle(), request.getStatus());

            ConversationResponse response = new ConversationResponse(conversation);
            return ResponseEntity.ok(ApiResponse.success("对话更新成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/{conversationId}")
    @Operation(summary = "删除对话", description = "软删除指定的对话")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @Parameter(description = "对话ID", example = "1") @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        boolean deleted = conversationService.deleteConversation(conversationId, userId);

        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("对话删除成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("对话不存在或无权限访问"));
        }
    }

    /**
     * 归档对话
     */
    @PutMapping("/{conversationId}/archive")
    @Operation(summary = "归档对话", description = "将对话状态设置为已归档")
    public ResponseEntity<ApiResponse<Void>> archiveConversation(
            @Parameter(description = "对话ID", example = "1") @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        boolean archived = conversationService.archiveConversation(conversationId, userId);

        if (archived) {
            return ResponseEntity.ok(ApiResponse.success("对话归档成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("对话不存在或无权限访问"));
        }
    }

    /**
     * 激活对话
     */
    @PutMapping("/{conversationId}/activate")
    @Operation(summary = "激活对话", description = "将对话状态设置为活跃")
    public ResponseEntity<ApiResponse<Void>> activateConversation(
            @Parameter(description = "对话ID", example = "1") @PathVariable Long conversationId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        boolean activated = conversationService.activateConversation(conversationId, userId);

        if (activated) {
            return ResponseEntity.ok(ApiResponse.success("对话激活成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("对话不存在或无权限访问"));
        }
    }

    /**
     * 获取用户对话统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取对话统计", description = "获取当前用户的对话统计信息")
    public ResponseEntity<ApiResponse<Object>> getConversationStats(Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        Long totalCount = conversationService.countUserConversations(userId);

        List<Conversation> activeConversations = conversationService
                .getUserConversationsByStatus(userId, Conversation.ConversationStatus.ACTIVE);
        List<Conversation> archivedConversations = conversationService
                .getUserConversationsByStatus(userId, Conversation.ConversationStatus.ARCHIVED);

        Object stats = new Object() {
            public final Long total = totalCount;
            public final Integer active = activeConversations.size();
            public final Integer archived = archivedConversations.size();
        };

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 根据状态获取对话列表
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取对话", description = "根据状态获取对话列表")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversationsByStatus(
            @Parameter(description = "对话状态", example = "ACTIVE") @PathVariable Conversation.ConversationStatus status,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        List<Conversation> conversations = conversationService.getUserConversationsByStatus(userId, status);

        List<ConversationResponse> responseList = conversations.stream()
                .map(ConversationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }
}