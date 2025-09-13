package org.sounfury.aki.infrastructure.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.service.CallLlmService;
import org.sounfury.aki.contracts.plan.RequestPlan;
import org.sounfury.aki.domain.llm.tools.service.ToolConfigurationService;
import org.sounfury.aki.infrastructure.llm.factory.ChatClientHolder;
import org.sounfury.aki.infrastructure.llm.advisor.SpringAiAdvisorAdapter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallLlmServiceImpl implements CallLlmService {

    private final ChatClientHolder chatClientHolder;
    private final SpringAiAdvisorAdapter advisorAdapter;
    private final ToolConfigurationService toolConfigurationService;



    /**
     * 获取启用的工具回调列表
     * 使用ToolConfigurationService进行动态工具筛选
     * @param requestPlan 请求计划
     * @return 启用的工具回调列表
     */
    private ToolCallback[] getEnabledToolCallbacks(RequestPlan requestPlan) {
        try {
            // 检查是否启用工具
            if (!requestPlan.isEnableTools()) {
                log.debug("工具未启用");
                return new ToolCallback[0];
            }

            // 使用工具配置服务获取启用的工具回调
            ToolCallback[] enabledCallbacks = toolConfigurationService.getEnabledToolCallbacks();
            log.debug("启用工具，获取到{}个启用的工具回调", enabledCallbacks.length);

            return enabledCallbacks;

        } catch (Exception e) {
            log.error("获取启用工具回调失败", e);
            return new ToolCallback[0];
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

    @Override
    public String call(String message, RequestPlan requestPlan) {
        try {
            // 1. 获取统一的对话ChatClient
            ChatClient conversationClient = chatClientHolder.getConversationClient();
            if (conversationClient == null) {
                return "Error: 无法获取对话ChatClient";
            }

            //2. 获取运行时advisor组合
            List<Advisor> runtimeAdvisors = advisorAdapter.buildRequestAdvisors(requestPlan);


            String response = conversationClient
                    .prompt()
                    .advisors(advisorParams -> {
                        // 设置sessionId参数
                        advisorParams.param(ChatMemory.CONVERSATION_ID, requestPlan.getSessionId());

                        // 添加运行时组装的advisor
                        if (!runtimeAdvisors.isEmpty()) {
                            advisorParams.advisors(runtimeAdvisors.toArray(new Advisor[0]));
                        }
                    })
                    .toolCallbacks(getEnabledToolCallbacks(requestPlan))
                    .user(message)
                    .call()
                    .content();

                     return response;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public Flux<String> callStream(String message, RequestPlan requestPlan) {
        try {
            // 1. 获取统一的对话ChatClient
            ChatClient conversationClient = chatClientHolder.getConversationClient();
            if (conversationClient == null) {
                return Flux.error(new IllegalArgumentException("无法获取对话ChatClient"));
            }

            // 2. 获取运行时advisor组合
            List<Advisor> runtimeAdvisors = advisorAdapter.buildRequestAdvisors(requestPlan);

            return conversationClient.prompt()
                    .advisors(advisorParams -> {
                        // 设置sessionId参数
                        advisorParams.param(ChatMemory.CONVERSATION_ID, requestPlan.getSessionId());
                        log.info("开始流式LLM调用: sessionId={}, characterId={}, advisor数量={}",
                                requestPlan.getSessionId(), requestPlan.getCharacterId(), runtimeAdvisors.size());
                        // 添加运行时组装的advisor
                        if (!runtimeAdvisors.isEmpty()) {
                            advisorParams.advisors(runtimeAdvisors.toArray(new Advisor[0]));
                        }
                    }).toolCallbacks(getEnabledToolCallbacks(requestPlan))
                    .user(message)
                    .stream()
                    .content()
                    .doOnComplete(() -> log.debug("流式LLM调用完成: sessionId={}, characterId={}, advisor数量={}",
                            requestPlan.getSessionId(), requestPlan.getCharacterId(), runtimeAdvisors.size()))
                    .doOnError(error -> log.error("流式LLM调用失败: sessionId={}, characterId={}",
                            requestPlan.getSessionId(),requestPlan.getCharacterId(), error));

        } catch (Exception e) {
            log.error("流式LLM调用异常: sessionId={}, characterId={}", 
                    requestPlan.getSessionId(), requestPlan.getCharacterId(), e);
            return Flux.error(e);
        }
    }
}
