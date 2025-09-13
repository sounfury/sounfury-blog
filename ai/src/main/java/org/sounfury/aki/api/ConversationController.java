package org.sounfury.aki.api;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.conversation.dto.*;
import org.sounfury.aki.application.conversation.service.ConversationApplicationService;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

/**
 * 对话控制器
 * 统一处理聊天和Agent模式的对话接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/conversation")
@SaIgnore
public class ConversationController {

    private final ConversationApplicationService conversationApplicationService;

    /**
     * 开始一个新的对话会话
     * @param request 会话开始请求（包含模式选择：conversation/agent）
     * @return 会话开始响应
     */
    @PostMapping("/start")
    public Result<SessionStartResponse> start(@Valid @RequestBody SessionStartRequest request) {
        log.info("开始新会话，模式: {}, 角色: {}, 是否站长: {}", 
                request.getMode(), request.getCharacterId(), request.getIsOwner());
        return Results.success(conversationApplicationService.startSession(request));
    }

    /**
     * 发送消息，继续对话
     * 支持conversation和agent两种模式，根据会话状态自动选择策略
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.debug("处理对话消息，会话ID: {}, 用户: {}", 
                request.getSessionId(), request.getUserName());
        return Results.success(conversationApplicationService.chat(request));
    }

    /**
     * 发送消息，继续对话（流式输出）
     * 支持conversation和agent两种模式，根据会话状态自动选择策略
     * @param request 聊天请求
     * @return 流式聊天响应
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody ChatRequest request) {
        log.debug("处理流式对话消息，会话ID: {}, 用户: {}", 
                request.getSessionId(), request.getUserName());
        return conversationApplicationService.chatStream(request);
    }


    /**
     * 获取会话信息
     * @param sessionId 会话ID
     * @return 会话信息
     */
    @GetMapping("/session/{sessionId}")
    public Result<ConversationApplicationService.SessionInfo> getSessionInfo(@PathVariable String sessionId) {
        return Results.success(conversationApplicationService.getSessionInfo(sessionId));
    }

    
    /**
     * 工具信息DTO
     */
    @lombok.Builder
    @lombok.Data
    public static class ToolInfo {
        private java.util.List<String> toolNames;
        private Integer toolCount;
        private Boolean available;
    }
}
