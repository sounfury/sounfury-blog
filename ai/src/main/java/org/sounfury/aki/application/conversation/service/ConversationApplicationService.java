package org.sounfury.aki.application.conversation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.conversation.dto.*;
import org.sounfury.aki.contracts.plan.RequestPlan;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.conversation.session.ConversationMode;
import org.sounfury.aki.domain.llm.service.CallLlmService;
import org.sounfury.aki.domain.conversation.session.*;
import org.sounfury.aki.domain.conversation.session.repository.SessionRepository;

import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 对话应用服务
 * 统一处理聊天和Agent模式的对话功能，被api层直接调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationApplicationService {

    private final CharacterRepository characterRepository;
    private final CallLlmService callLlmService;
    private final SessionRepository sessionRepository;
    private final RequestPlanCreatService requestPlanCreatService;

    /**
     * 开始新的对话会话
     *
     * @param request 会话开始请求
     * @return 会话开始响应
     */
    public SessionStartResponse startSession(SessionStartRequest request) {
        try {
            log.info("开始新的对话会话，角色ID: {}, 模式: {}, 是否站长: {}", request.getCharacterId(), request.getMode(),
                     request.getIsOwner());

            // 解析聊天模式
            ConversationMode mode = parseConversationMode(request.getMode());

            //获取角色卡并转换
            if (request.getCharacterId() == null || request
                    .getCharacterId()
                    .trim()
                    .isEmpty()) {
                return SessionStartResponse.failure("角色ID不能为空");
            }
            if (!characterRepository.existsById(PersonaId.of(request.getCharacterId()))) {
                return SessionStartResponse.failure("角色不存在");
            }

            // 直接创建会话
            boolean isOwnerSession = !UserContextHolder
                    .getContext()
                    .isGuest();

            // 创建会话配置
            SessionMeta sessionConfig = SessionMeta
                    .builder()
                    .personaId(request.getCharacterId())
                    .mode(mode)
                    .isOwnerSession(isOwnerSession)
                    .enableMemory(shouldEnableMemory(mode))
                    .enableRag(shouldEnableRag(request.getCharacterId()))
                    .memoryPolicy(isOwnerSession
                            ? SessionMemoryPolicy.forOwner()
                            : SessionMemoryPolicy.forGuest())
                    .build();

            // 创建会话
            Session session = Session.create(sessionConfig, isOwnerSession);

            // 保存会话
            sessionRepository.save(session);

            // 获取角色开场白
            String greeting = getCharacterGreeting(request.getCharacterId());

            log.info("对话会话创建成功，会话ID: {}, 角色: {}, 模式: {}", session.getSessionId().getValue(), request.getCharacterId(),
                     mode.getName());

            return SessionStartResponse.success(greeting, session.getSessionId().getValue(), request.getCharacterId(),
                                                mode.getName());

        } catch (Exception e) {
            log.error("创建对话会话失败，角色ID: {}", request.getCharacterId(), e);
            return SessionStartResponse.failure("创建会话失败: " + e.getMessage());
        }
    }

    /**
     * 进行对话
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    public ChatResponse chat(ChatRequest request) {
        try {
            log.info("处理对话消息，会话ID: {}, 用户: {}, 角色: {}, Agent启用: {}", request.getSessionId(), request.getUserName(),
                     request.getCharacterId(), request.getEnableAgent());
            
            // 确保角色advisor存在
            requestPlanCreatService.ensure(request.getCharacterId());
            
            // 规划请求 - 传递enableAgent参数
            RequestPlan requestPlan = requestPlanCreatService.planForRequest(
                    request.getSessionId(), 
                    request.getCharacterId(), 
                    Boolean.TRUE.equals(request.getEnableAgent()));
            
            // 调用LLM
            String aiResponse = callLlmService.call(request.getMessage(), requestPlan);

            log.info("对话处理成功，会话ID: {}", request.getSessionId());

            return ChatResponse.success(aiResponse, request.getSessionId(), request.getCharacterId(), "conversation",
                                        "conversation-service");

        } catch (Exception e) {
            log.error("对话处理异常，会话ID: {}", request.getSessionId(), e);
            return ChatResponse.failure("服务异常: " + e.getMessage(), request.getSessionId());
        }
    }

    /**
     * 进行流式对话
     * @param request 聊天请求
     * @return 流式聊天响应
     */
    public Flux<String> chatStream(ChatRequest request) {
        try {
            log.info("处理流式对话消息，会话ID: {}, 用户: {}, 角色: {}, Agent启用: {}", request.getSessionId(), request.getUserName(),
                     request.getCharacterId(), request.getEnableAgent());

            // 确保角色advisor存在
            requestPlanCreatService.ensure(request.getCharacterId());
            
            // 规划请求 - 传递enableAgent参数
            RequestPlan requestPlan = requestPlanCreatService.planForRequest(
                    request.getSessionId(), 
                    request.getCharacterId(), 
                    Boolean.TRUE.equals(request.getEnableAgent()));
            
            // 调用LLM流式响应
            return callLlmService.callStream(request.getMessage(), requestPlan);

        } catch (Exception e) {
            log.error("流式对话处理异常，会话ID: {}", request.getSessionId(), e);
            return Flux.error(new RuntimeException("服务异常: " + e.getMessage()));
        }
    }


    /**
     * 获取会话信息
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    public SessionInfo getSessionInfo(String sessionId) {
        try {
            // 直接查询会话信息
            Session session = sessionRepository.findById(SessionId.of(sessionId))
                    .orElseThrow(() -> new IllegalArgumentException("会话不存在: " + sessionId));

            return SessionInfo
                    .builder()
                    .sessionId(sessionId)
                    .exists(!session.isArchived())
                    .mode(session.getConfiguration().getMode().getName())
                    .characterId(session.getConfiguration().getPersonaId())
                    .isOwnerSession(session.getConfiguration().isOwnerSession())
                    .messageCount(0) // 消息数量由Spring AI Memory管理，这里不再统计
                    .conversationRounds(0) // 轮次计算同样不再需要
                    .toolsEnabled(session.getConfiguration().supportsToolCalling())
                    .memoryEnabled(session.getConfiguration().needsMemory())
                    .configuration("对话配置")
                    .build();

        } catch (Exception e) {
            log.error("获取会话信息失败，会话ID: {}", sessionId, e);
            return SessionInfo
                    .builder()
                    .sessionId(sessionId)
                    .exists(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * 解析对话模式
     */
    private ConversationMode parseConversationMode(String mode) {
        if (mode == null || mode
                .trim()
                .isEmpty()) {
            return ConversationMode.CHAT;
        }

        return switch (mode.toLowerCase()) {
            case "agent" -> ConversationMode.AGENT;
            case "conversation", "chat" -> ConversationMode.CHAT;
            default -> ConversationMode.CHAT;
        };
    }


    /**
     * 获取角色开场白
     */
    private String getCharacterGreeting(String characterId) {
        if (characterId == null || characterId
                .trim()
                .isEmpty()) {
            return "你好！我是你的AI助手，有什么可以帮助你的吗？";
        }
        return characterRepository
                .findPersonaById(PersonaId.of(characterId))
                .map(character -> character
                        .getCard()
                        .getCharGreeting())
                .orElse("你好！我是你的AI助手，有什么可以帮助你的吗？");
    }




    /**
     * 会话信息DTO
     */
    @lombok.Builder
    @lombok.Data
    public static class SessionInfo {
        private String sessionId;
        private Boolean exists;
        private String mode;
        private String characterId;
        private Boolean isOwnerSession;
        private Integer messageCount;
        private Integer conversationRounds;
        private Boolean toolsEnabled;
        private Boolean memoryEnabled;
        private String configuration;
        private String errorMessage;
    }

    /**
     * 判断是否启用记忆
     */
    private boolean shouldEnableMemory(ConversationMode mode) {
        return mode == ConversationMode.AGENT || mode == ConversationMode.CHAT;
    }

    /**
     * 判断是否启用RAG
     */
    private boolean shouldEnableRag(String characterId) {
        return characterId != null && !characterId.trim().isEmpty();
    }


}
