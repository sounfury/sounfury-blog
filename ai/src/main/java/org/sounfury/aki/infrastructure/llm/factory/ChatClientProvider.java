package org.sounfury.aki.infrastructure.llm.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import org.sounfury.aki.domain.prompt.AssembledPrompt;
import org.sounfury.aki.infrastructure.llm.advisor.SpringAiAdvisorAssembler;
import org.sounfury.aki.infrastructure.llm.advisor.PromptLoggerAdvisor;
import org.sounfury.aki.domain.llm.repository.LlmConfigurationRepository;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.core.convention.exception.ServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LLM编排服务
 * 集中处理ChatClient的创建、重建和生命周期管理
 * 负责协调提示词组装、Advisor组装和ChatClient构建
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatClientProvider  {

    private final ChatModelFactory chatModelFactory;
    private final ChatClientHolder chatClientHolder;
    private final PromptAssemblyService promptAssemblyService;
    private final CharacterRepository characterRepository;
    private final SpringAiAdvisorAssembler advisorAssembler;
    private final LlmConfigurationRepository configurationRepository;
    private final PromptLoggerAdvisor promptLoggerAdvisor;

    @Value("${aki.llm.default-character-id:bartender}")
    private String defaultCharacterId;

    @Value("${aki.debug.prompt-capture.enabled}")
    private boolean promptCaptureEnabled;

    /**
     * 初始化所有ChatClient
     * 供bootstrap层调用
     */
    public void initializeClients() {
        log.info("开始初始化ChatClient...");

        try {
            // 创建ChatModel
            ModelConfiguration config = configurationRepository.findGlobalConfiguration()
                                                               .orElse(ModelConfiguration.createDefault());
            ChatModel chatModel = chatModelFactory.createChatModel(config);

            // 初始化任务场景的ChatClient
            initializeTaskClient(chatModel);

            log.info("ChatClient初始化完成");

        } catch (Exception e) {
            log.error("ChatClient初始化失败", e);
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * 初始化任务场景的ChatClient
     */
    private void initializeTaskClient(ChatModel chatModel) {
        try {
            List<Advisor> taskAdvisors = assembleTaskAdvisors();

            ChatClient taskClient = ChatClient
                    .builder(chatModel)
                    .defaultAdvisors(taskAdvisors.toArray(new Advisor[0]))
                    .build();

            chatClientHolder.updateTaskClient(taskClient);

            log.info("任务ChatClient初始化完成，advisor数量: {}", taskAdvisors.size());

        } catch (Exception e) {
            log.error("初始化任务ChatClient失败", e);
            throw e;
        }
    }



    /**
     * 全局配置修改时重建所有ChatClient
     * 使用指定的ChatModel重建所有的ChatClient
     * 很明显当api/key/模型等配置变更时需要重建
     * 供事件处理器调用
     */
    public void rebuildChatClientWithModel(ChatModel chatModel) {
        try {
            log.info("使用新ChatModel重建ChatClient");
            
            AssembledPrompt defaultPrompt = assemblePromptForCharacter(defaultCharacterId);

            // 组装默认advisor
            List<Advisor> defaultAdvisors = assembleConversationAdvisors(defaultPrompt);

            // 创建带advisor的ChatClient
            ChatClient chatClientWithAdvisors = ChatClient.builder(chatModel)
                    .defaultAdvisors(defaultAdvisors.toArray(new Advisor[0]))
                    .build();

            // 更新ChatClientHolder
            chatClientHolder.updateAll(chatClientWithAdvisors);

            log.info("ChatClient重建完成，advisor数量: {}", defaultAdvisors.size());

        } catch (Exception e) {
            log.error("重建ChatClient失败", e);
            throw new RuntimeException("重建ChatClient失败", e);
        }
    }

    //=========================角色维度的ChatClient====================================//

    /**
     * 为指定角色创建对话ChatClient
     * 供ChatClientHolder懒加载时调用
     */
    public ChatClient createChatClientForCharacter(String characterId) {
        try {
            log.info("为角色创建对话ChatClient: {}", characterId);

            // 获取ChatModel
            ModelConfiguration config = configurationRepository.findGlobalConfiguration()
                                                               .orElse(ModelConfiguration.createDefault());
            ChatModel chatModel = chatModelFactory.createChatModel(config);

            // 组装角色提示词
            AssembledPrompt prompt = assemblePromptForCharacter(characterId);

            // 组装advisor
            List<Advisor> advisors = assembleConversationAdvisors(prompt);

            // 创建ChatClient
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultAdvisors(advisors.toArray(new Advisor[0]))
                    .build();

            log.info("角色ChatClient创建完成: {}, advisor数量: {}", characterId, advisors.size());
            return chatClient;

        } catch (Exception e) {
            log.error("创建角色ChatClient失败: {}", characterId, e);
            throw new RuntimeException("创建角色ChatClient失败: " + characterId, e);
        }
    }


    //=========================提示词组装创建====================================//
    /**
     * 为指定角色组装提示词
     */
    private AssembledPrompt assemblePromptForCharacter(String characterId) {
        try {
            // 获取角色信息
            Persona persona = characterRepository.findPersonaById(PersonaId.of(characterId))
                                                 .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + characterId));

            // 组装提示词
            return promptAssemblyService.assembleConversationPrompt(
                    persona,
                    PromptAssemblyService.BehaviorKeys.CHAT //默认行为类型为对话
            );

        } catch (Exception e) {
            log.error("组装角色提示词失败: characterId={}", characterId, e);
            return AssembledPrompt.empty();
        }
    }

    //=========================advisor创建====================================//

    /**
     * 组装对话场景的advisor列表（不包含记忆）
     */
    private List<Advisor> assembleConversationAdvisors(AssembledPrompt assembledPrompt) {
        try {
            // 对话场景：基础提示词 + 角色卡 + 行为指导，不包含记忆
            List<Advisor> advisors = advisorAssembler.assembleAdvisorsFromPrompts(
                    assembledPrompt.getBaseSystemPrompt(),
                    assembledPrompt.getBehaviorGuidePrompt(),
                    assembledPrompt.getCharacterPrompt(),
                    null, // 在第一次对话开始时才能通过用户身份区分称呼
                    false, // 暂不启用RAG
                    null   // RAG设置为null
            );

            // 根据配置决定是否添加PromptLoggerAdvisor
            System.out.println("Prompt capture enabled: " + promptCaptureEnabled);
            if (promptCaptureEnabled) {
                advisors.add(promptLoggerAdvisor);
                System.out.println("已添加PromptLoggerAdvisor到任务advisor链");
            }


            log.debug("对话advisor组装完成，数量: {}", advisors.size());
            return advisors;

        } catch (Exception e) {
            log.error("组装对话advisor失败", e);
            return List.of();
        }
    }

    /**
     * 组装任务场景的advisor列表
     */
    private List<Advisor> assembleTaskAdvisors() {
        try {
            Persona defaultPersona = characterRepository.findPersonaById(PersonaId.of(defaultCharacterId))
                                                 .orElseThrow(() -> new IllegalArgumentException("默认角色不存在: " + defaultCharacterId));
            
            // 构建上下文并渲染提示词
            AssembledPrompt assembledPrompt = promptAssemblyService.assembleBaseTaskPrompt(defaultPersona);
            // 任务场景：基础系统提示词 + 角色卡 + 任务行为指导，不包含记忆、RAG、工具
            List<Advisor> advisors = advisorAssembler.assembleAdvisorsFromPrompts(
                    assembledPrompt.getBaseSystemPrompt(), // 基础系统提示词
                    assembledPrompt.getBehaviorGuidePrompt(), // 行为指导提示词
                    assembledPrompt.getCharacterPrompt(), // 角色卡提示词
                    null,                 // 无用户称呼
                    false,                // 无RAG
                    null                  // 无RAG设置
            );
            if (promptCaptureEnabled) {
                advisors.add(promptLoggerAdvisor);
                System.out.println("已添加PromptLoggerAdvisor到任务advisor链");
            }

            log.debug("任务advisor组装完成，数量: {}", advisors.size());
            return advisors;

        } catch (Exception e) {
            log.error("组装任务advisor失败", e);
            return List.of();
        }
    }
}
