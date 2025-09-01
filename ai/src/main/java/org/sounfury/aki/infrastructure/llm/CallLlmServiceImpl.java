package org.sounfury.aki.infrastructure.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.service.CallLlmService;
import org.sounfury.aki.domain.conversation.session.Session;
import org.sounfury.aki.domain.conversation.session.SessionId;
import org.sounfury.aki.domain.conversation.session.repository.SessionRepository;
import org.sounfury.aki.domain.conversation.session.ConversationMode;
import org.sounfury.aki.infrastructure.llm.factory.ChatClientHolder;
import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.sounfury.aki.infrastructure.llm.advisor.factory.MemoryAdvisorFactory;
import org.sounfury.aki.domain.conversation.session.SessionMemoryPolicy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallLlmServiceImpl implements CallLlmService {

    private final ChatClientHolder chatClientHolder;
    private final SessionRepository sessionRepository;
    private final MemoryAdvisorFactory memoryAdvisorFactory;

    @Override
    public String sendMessage(String sessionId, String characterId, String message) {
        try {
            // 1. 获取指定角色的对话ChatClient（懒加载）
            ChatClient conversationClient = chatClientHolder.getChatClient(characterId);
            if (conversationClient == null) {
                return "Error: 无法获取角色ChatClient: " + characterId;
            }

            // 2. 查询会话配置
            Session session = sessionRepository.findById(SessionId.of(sessionId))
                    .orElseThrow(() -> new IllegalArgumentException("会话不存在: " + sessionId));

            // 3. 获取当前用户身份
            UserContextHolder.UserContext userContext = UserContextHolder.getContext();

            // 4. 动态添加advisor并调用
            String response = conversationClient
                    .prompt()
                    .advisors(advisorParams -> {
                        // 设置sessionId参数
                        advisorParams.param(ChatMemory.CONVERSATION_ID, sessionId);

                        // 根据用户身份动态添加记忆advisor
                        addMemoryAdvisor(advisorParams, userContext.isOwner());

                        // 根据对话模式添加工具advisor（仅Agent模式）
                        if (session.getConfiguration().getMode() == ConversationMode.AGENT) {
                            addToolAdvisor(advisorParams, userContext.isOwner());
                        }
                    })
                    .user(message)
                    .call()
                    .content();

            log.debug("LLM调用成功: sessionId={}, characterId={}, userRole={}",
                    sessionId, characterId, userContext.getRole());
            return response;

        } catch (Exception e) {
            log.error("LLM调用失败: sessionId={}, characterId={}", sessionId, characterId, e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public Flux<String> sendMessageStream(String sessionId, String characterId, String message) {
        try {
            // 1. 获取指定角色的对话ChatClient（懒加载）
            ChatClient conversationClient = chatClientHolder.getChatClient(characterId);
            if (conversationClient == null) {
                return Flux.error(new IllegalArgumentException("无法获取角色ChatClient: " + characterId));
            }

            // 2. 查询会话配置
            Session session = sessionRepository.findById(SessionId.of(sessionId))
                    .orElseThrow(() -> new IllegalArgumentException("会话不存在: " + sessionId));


            // 3. 获取当前用户身份
            UserContextHolder.UserContext userContext = UserContextHolder.getContext();

            // 4. 动态添加advisor并调用
            return conversationClient.prompt()
                    .advisors(advisorParams -> {
                        // 设置sessionId参数
                        advisorParams.param(ChatMemory.CONVERSATION_ID, sessionId);

                        // 根据用户身份动态添加记忆advisor
                        addMemoryAdvisor(advisorParams, userContext.isOwner());

                        // 根据对话模式添加工具advisor（仅Agent模式）
                        if (session.getConfiguration().getMode() == ConversationMode.AGENT) {
                            addToolAdvisor(advisorParams, userContext.isOwner());
                        }
                    })
                    .user(message)
                    .stream()
                    .content()
                    .doOnComplete(() -> log.debug("流式LLM调用完成: sessionId={}, characterId={}",
                            sessionId, characterId))
                    .doOnError(error -> log.error("流式LLM调用失败: sessionId={}, characterId={}",
                            sessionId, characterId, error));

        } catch (Exception e) {
            log.error("流式LLM调用异常: sessionId={}, characterId={}", sessionId, characterId, e);
            return Flux.error(e);
        }
    }

    /**
     * 根据用户身份添加记忆advisor
     * 这块advisorParams必须是新实例，否则会导致多次加入advisor
     */
    private void addMemoryAdvisor(ChatClient.AdvisorSpec advisorParams, boolean isOwner) {
        try {
            Advisor memoryAdvisor;
            if (isOwner) {
                // 站长使用持久化记忆
                memoryAdvisor = memoryAdvisorFactory.createMemoryAdvisor(SessionMemoryPolicy.forOwner());
                log.debug("添加站长持久化记忆advisor");
            } else {
                // 游客使用内存记忆
                memoryAdvisor = memoryAdvisorFactory.createMemoryAdvisor(SessionMemoryPolicy.forGuest());
                log.debug("添加游客内存记忆advisor");
            }
            if (memoryAdvisor != null) {
                advisorParams.advisors(memoryAdvisor);
            }
        } catch (Exception e) {
            log.error("添加记忆advisor失败", e);
        }
    }

    /**
     * 添加工具调用advisor（仅Agent模式且站长身份）
     */
    private void addToolAdvisor(ChatClient.AdvisorSpec advisorParams, boolean isOwner) {
        if (isOwner) {
            // TODO: 实现工具调用advisor
            log.debug("Agent模式且站长身份，应添加工具调用advisor（待实现）");
        }
    }



    @Override
    public String sendTaskMessage(String taskSpecificPrompt, String userMessage) {
        try {
            log.debug("执行任务LLM调用: taskSpecificPromptLength={}, userMessageLength={}",
                    taskSpecificPrompt != null ? taskSpecificPrompt.length() : 0,
                    userMessage != null ? userMessage.length() : 0);

            // 获取任务专用的ChatClient（已包含基础Advisor）
            ChatClient taskClient = chatClientHolder.getTaskClient();
            if (taskClient == null) {
                return "Error: 无法获取任务ChatClient";
            }

            // 构建用户消息（包含任务特定提示词）
            String fullUserMessage = buildFullUserMessage(taskSpecificPrompt, userMessage);

            // 调用任务ChatClient
            String response = taskClient.prompt()
                    .user(fullUserMessage)
                    .call()
                    .content();

            log.debug("任务LLM调用成功");
            return response;

        } catch (Exception e) {
            log.error("任务LLM调用失败", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public Flux<String> sendTaskMessageStream(String taskSpecificPrompt, String userMessage) {
        try {
            log.debug("执行流式任务LLM调用: taskSpecificPromptLength={}, userMessageLength={}",
                    taskSpecificPrompt != null ? taskSpecificPrompt.length() : 0,
                    userMessage != null ? userMessage.length() : 0);

            // 获取任务专用的ChatClient（已包含基础Advisor）
            ChatClient taskClient = chatClientHolder.getTaskClient();
            if (taskClient == null) {
                return Flux.error(new IllegalStateException("无法获取任务ChatClient"));
            }

            // 构建用户消息（包含任务特定提示词）
            String fullUserMessage = buildFullUserMessage(taskSpecificPrompt, userMessage);

            // 流式调用任务ChatClient
            return taskClient.prompt()
                    .user(fullUserMessage)
                    .stream()
                    .content()
                    .doOnComplete(() -> log.debug("流式任务LLM调用完成"))
                    .doOnError(error -> log.error("流式任务LLM调用失败", error));

        } catch (Exception e) {
            log.error("流式任务LLM调用异常", e);
            return Flux.error(e);
        }
    }

    /**
     * 构建完整的用户消息
     */
    private String buildFullUserMessage(String taskSpecificPrompt, String userMessage) {
        if (taskSpecificPrompt == null || taskSpecificPrompt.trim().isEmpty()) {
            return userMessage;
        }

        return taskSpecificPrompt + "\n\n" + userMessage;
    }
}
