package org.sounfury.aki.infrastructure.llm.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.plan.InitPlan;
import org.sounfury.aki.contracts.service.ChatClientFactoryPort;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.infrastructure.llm.advisor.SpringAiAdvisorAdapter;
import org.sounfury.core.convention.exception.ServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatClient提供者
 * 实现ChatClientCreateService接口，负责ChatClient的创建和生命周期管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatClientProvider implements ChatClientFactoryPort {

    private final ChatModelFactory chatModelFactory;
    private final ChatClientHolder chatClientHolder;
    private final SpringAiAdvisorAdapter advisorAdapter;

    @Override
    public void initializeTaskClient(InitPlan taskInitPlan) {
        log.info("开始初始化任务ChatClient...");
        
        try {
            // 创建ChatModel
            ChatModel chatModel = chatModelFactory.createChatModel(taskInitPlan.getModelConfiguration());

            // 通过Adapter转换为Advisor列表
            List<Advisor> taskAdvisors = advisorAdapter.buildInitAdvisors(taskInitPlan);
            
            // 创建ChatClient
            ChatClient taskClient = ChatClient
                    .builder(chatModel)
                    .defaultAdvisors(taskAdvisors.toArray(new Advisor[0]))
                    .build();

            chatClientHolder.updateTaskClient(taskClient);

            log.info("任务ChatClient初始化完成，advisor数量: {}", taskAdvisors.size());

        } catch (Exception e) {
            log.error("初始化任务ChatClient失败", e);
            throw new ServiceException(e.getMessage());
        }
    }



    @Override
    public void initializeClientForConversation(InitPlan initPlan) {
        try {
            // 创建ChatModel
            ChatModel chatModel = chatModelFactory.createChatModel(initPlan.getModelConfiguration());

            // 对话Client不设置defaultAdvisors，运行时通过buildRequestAdvisors获取完整advisor列表
            ChatClient conversationClient = ChatClient
                    .builder(chatModel)
                    .build();
            chatClientHolder.updateConversationClient(conversationClient);
            
            log.info("对话ChatClient初始化完成（无defaultAdvisors），运行时将通过缓存获取advisor");
        } catch (Exception e) {
            log.error("初始化对话ChatClient失败", e);
            throw new ServiceException(e.getMessage());
        }
    }


    @Override
    public void rebuildAllBySettingsChange(ModelConfiguration newConfiguration) {
        OpenAiChatOptions openAiChatOptions = chatModelFactory.buildChatOptions(newConfiguration.getProvider(),
                                                                                newConfiguration.getSettings());
        //1.取出任务Client
        ChatClient taskClient = chatClientHolder.getTaskClient();
        if (taskClient != null) {
            ChatClient build = taskClient
                    .mutate()
                    .defaultOptions(openAiChatOptions)
                    .build();
            chatClientHolder.updateTaskClient(build);
        } else {
            log.warn("任务ChatClient不存在，无法应用新配置");
        }
        //2.取出对话Client
        ChatClient conversationClient = chatClientHolder.getConversationClient();
        if (conversationClient != null) {
            ChatClient build = conversationClient
                    .mutate()
                    .defaultOptions(openAiChatOptions)
                    .build();
            chatClientHolder.updateConversationClient(build);
        } else {
            log.warn("对话ChatClient不存在，无法应用新配置");
        }
    }



}
