package org.sounfury.aki.application.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.plan.InitPlan;
import org.sounfury.aki.contracts.plan.RequestPlan;
import org.sounfury.aki.contracts.service.ChatClientFactoryPort;
import org.sounfury.aki.contracts.spec.PromptSpec;
import org.sounfury.aki.contracts.spec.RagSpec;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.domain.conversation.memory.repository.GlobalMemoryRepository;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.repository.LlmConfigurationRepository;
import org.sounfury.aki.domain.prompt.AssembledPrompt;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatClient应用服务
 * 负责ChatClient的业务编排和生命周期管理
 * 整合原ConversationPlanningService的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatClientCreateApplicationService {

    private final LlmConfigurationRepository configurationRepository;
    private final PromptAssemblyService promptAssemblyService;
    private final CharacterRepository characterRepository;
    private final GlobalMemoryRepository globalMemoryRepository;
    private final ChatClientFactoryPort chatClientFactoryPort;

    @Value("${aki.debug.prompt-capture.enabled:false}")
    private boolean promptCaptureEnabled;

    @Value("${aki.llm.default-character-id:aki}")
    private String defaultCharacterId;

    /**
     * 初始化chatClient
     */
    public void initChatClient() {
        initializeTaskClient();
        initBaseConversationClient();
    }

    private void initBaseConversationClient() {

        List<GlobalMemory> globalMemories =  loadGlobalMemories();

        ModelConfiguration config = configurationRepository.findGlobalConfiguration()
                                                           .orElse(ModelConfiguration.createDefault());

        InitPlan initPlan = InitPlan
                .builder()
                .ragSpec(RagSpec.disabled())
                .enableLogging(promptCaptureEnabled)
                .modelConfiguration(config)
                .globalMemories(globalMemories)
                .build();

        chatClientFactoryPort.initializeClientForConversation(initPlan);
    }


    /**
     * 初始化任务ChatClient
     * 供bootstrap层调用
     */
    public void initializeTaskClient() {
        log.info("开始初始化任务ChatClient...");
        try {
            // 构建任务场景的INIT计划
            InitPlan taskInitPlan = planTaskInit();

            // 调用接口创建任务ChatClient
            chatClientFactoryPort.initializeTaskClient(taskInitPlan);

            log.info("任务ChatClient初始化完成");
        } catch (Exception e) {
            log.error("任务ChatClient初始化失败", e);
            throw e;
        }
    }


    public void rebuildAllBySettingsChange(ModelConfiguration newConfiguration) {
        chatClientFactoryPort.rebuildAllBySettingsChange(newConfiguration);
    }

    /**
     * 规划任务场景的INIT配置
     */
    private InitPlan planTaskInit() {
        log.debug("开始规划任务场景INIT阶段配置");

        try {
            // 使用默认角色进行任务场景规划
            Persona defaultPersona = characterRepository.findPersonaById(PersonaId.of(defaultCharacterId))
                                                        .orElseThrow(() -> new IllegalArgumentException("默认角色不存在: " + defaultCharacterId));

            // 组装任务提示词
            AssembledPrompt assembledPrompt = promptAssemblyService.assembleBaseTaskPrompt(defaultPersona);

            // 获取全局记忆
            List<GlobalMemory> globalMemories = loadGlobalMemories();

            // 构建提示词规格
            PromptSpec promptSpec = PromptSpec
                    .builder()
                    .assembledPrompt(assembledPrompt)
                    .globalMemories(globalMemories)
                    .separatedMode(true)
                    .build();
            ModelConfiguration config = configurationRepository.findGlobalConfiguration()
                                                               .orElse(ModelConfiguration.createDefault());


            // 构建任务INIT计划
            InitPlan taskInitPlan = InitPlan
                    .builder()
                    .characterId(defaultCharacterId)
                    .promptSpec(promptSpec)
                    .ragSpec(RagSpec.disabled())
                    .enableLogging(promptCaptureEnabled)
                    .modelConfiguration(config)
                    .build();

            log.debug("任务场景INIT阶段规划完成");
            return taskInitPlan;

        } catch (Exception e) {
            log.error("任务场景INIT阶段规划失败", e);
            return InitPlan.empty(defaultCharacterId);
        }
    }


    /**
     * 加载全局记忆（全局策略提示词）
     */
    private List<GlobalMemory> loadGlobalMemories() {
        try {
            // 从数据库加载最近的全局记忆，作为全局策略提示词
            List<GlobalMemory> globalMemories = globalMemoryRepository.findRecentMemories(10);

            log.debug("成功加载{}条全局记忆", globalMemories.size());
            return globalMemories;

        } catch (Exception e) {
            log.error("加载全局记忆失败", e);
            return List.of();
        }
    }
}
