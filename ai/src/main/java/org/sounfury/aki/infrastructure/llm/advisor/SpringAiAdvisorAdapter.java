package org.sounfury.aki.infrastructure.llm.advisor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.constant.MemoryType;
import org.sounfury.aki.contracts.service.AdvisorFactoryPort;
import org.sounfury.aki.contracts.spec.PromptSpec;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.domain.conversation.session.SessionMemoryPolicy;
import org.sounfury.aki.contracts.plan.InitPlan;
import org.sounfury.aki.contracts.plan.RequestPlan;
import org.sounfury.aki.infrastructure.llm.advisor.base.PromptAdvisor;
import org.sounfury.aki.infrastructure.llm.advisor.base.PromptLoggerAdvisor;
import org.sounfury.aki.infrastructure.llm.advisor.factory.PromptAdvisorFactory;
import org.sounfury.aki.infrastructure.llm.advisor.factory.MemoryAdvisorFactory;
import org.sounfury.aki.domain.prompt.SystemPrompt;
import org.sounfury.aki.domain.prompt.CharacterPrompt;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Spring AI Advisor适配器
 * 负责将无技术语义的Plan/Spec对象转换为Spring AI的Advisor列表
 * 重构后支持advisor分类缓存和热重载
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiAdvisorAdapter implements AdvisorFactoryPort {

    private final PromptAdvisorFactory promptAdvisorFactory;
    private final MemoryAdvisorFactory memoryAdvisorFactory;
    private final PromptLoggerAdvisor promptLoggerAdvisor;
    private final AdvisorCacheManager advisorCacheManager;
    private final PromptAssemblyService promptAssemblyService;

    /**
     * 构建INIT阶段的advisor列表,只用于系统初始化时
     * @param initPlan INIT计划对象
     * @return advisor列表
     */
    public List<Advisor> buildInitAdvisors(InitPlan initPlan) {
        if (initPlan == null || !initPlan.isValid()) {
            log.warn("InitPlan无效，返回空advisor列表");
            return List.of();
        }
        
        List<Advisor> advisors = new ArrayList<>();
        //1. 日志advisor
        advisors.add(promptLoggerAdvisor);

        //2. 全局记忆advisor
        List<GlobalMemory> globalMemories = initPlan.getGlobalMemories();
        if (globalMemories != null && !globalMemories.isEmpty()) {
            Advisor globalMemoryAdvisorToCache = promptAdvisorFactory.creatGlobalMemoryAdvisor(globalMemories);
            if (globalMemoryAdvisorToCache != null) {
                advisors.add(globalMemoryAdvisorToCache);
                advisorCacheManager.setGlobalMemoryAdvisor(globalMemoryAdvisorToCache);
                log.debug("添加了全局记忆advisor并缓存");
            }
        }

        ///3 若有角色，设置角色相关的advisor
        if (initPlan.getCharacterId() != null && !initPlan.getCharacterId().isEmpty()) {
            createAndCacheAdvisors(initPlan.getPromptSpec(), initPlan.getCharacterId());
            List<Advisor> characterAdvisors = advisorCacheManager.getCharacterAdvisor(initPlan.getCharacterId());
            if (characterAdvisors != null && !characterAdvisors.isEmpty()) {
                advisors.addAll(characterAdvisors);
                log.debug("添加了角色相关advisor，角色ID: {}", initPlan.getCharacterId());
            }
        }


        log.debug("INIT阶段advisor构建完成，数量: {}", advisors.size());
        return advisors;
    }

    /**
     * 构建PER_REQUEST阶段的advisor列表
     * 合并系统级advisor和运行时advisor，并按order排序
     * @param requestPlan 请求计划对象
     * @return 完整的advisor列表
     */
    public List<Advisor> buildRequestAdvisors(RequestPlan requestPlan) {
        return buildRequestAdvisors(requestPlan, "chat"); // 默认使用chat行为类型
    }

    /**
     * 构建PER_REQUEST阶段的advisor列表，支持指定行为类型
     * @param requestPlan 请求计划对象
     * @param behaviorType 行为类型
     * @return 完整的advisor列表
     */
    public List<Advisor> buildRequestAdvisors(RequestPlan requestPlan, String behaviorType) {
        if (requestPlan == null || !requestPlan.isValid()) {
            log.warn("RequestPlan无效，返回空advisor列表");
            return List.of();
        }

        log.debug("开始构建完整advisor列表，会话: {}, behaviorType: {}", requestPlan.getSessionId(), behaviorType);

        List<Advisor> advisors = new ArrayList<>();

        // 1. 添加基础advisor（日志）
        advisors.add(promptLoggerAdvisor);

        // 2. 添加全局记忆advisor
        Advisor globalMemoryAdvisor = advisorCacheManager.getGlobalMemoryAdvisor();
        if (globalMemoryAdvisor != null) {
            advisors.add(globalMemoryAdvisor);
        }

        // 3. 确保并添加系统级advisor
        ensureSystemAdvisorCache(behaviorType);
        List<Advisor> systemAdvisors = advisorCacheManager.getSystemAdvisors(behaviorType);
        if (systemAdvisors != null && !systemAdvisors.isEmpty()) {
            advisors.addAll(systemAdvisors);
            log.debug("添加系统advisor数量: {}", systemAdvisors.size());
        }

        // 4. 添加角色相关advisor
        List<Advisor> characterAdvisors = advisorCacheManager.getCharacterAdvisor(requestPlan.getCharacterId());
        if (characterAdvisors == null || characterAdvisors.isEmpty()) {
            throw new IllegalArgumentException("角色advisor不存在，无法构建请求advisor，请先调用ensureCharacterAdvisor方法");
        }
        advisors.addAll(characterAdvisors);
        log.debug("添加角色advisor数量: {}", characterAdvisors.size());

        // 5. 添加运行时动态advisor（会话记忆等）
        buildDynamicMemoryAdvisor(requestPlan, advisors);

        // 6. 按advisor order排序，确保执行顺序正确
        advisors.sort(Comparator.comparingInt(Advisor::getOrder));

        log.debug("完整advisor列表构建完成，总数量: {}", advisors.size());
        return advisors;
    }

    /**
     * 构建动态记忆advisor（PER_REQUEST阶段）
     */
    private void buildDynamicMemoryAdvisor(RequestPlan requestPlan, List<Advisor> advisors) {
        log.info(requestPlan.toString());
        try {
            Advisor memoryAdvisor = memoryAdvisorFactory.createMemoryAdvisor(
                requestPlan.getMemorySpec().getType() == MemoryType.PERSISTENT
                    ? SessionMemoryPolicy.forOwner()
                    : SessionMemoryPolicy.forGuest()
            );
            if (memoryAdvisor != null) {
                advisors.add(memoryAdvisor);
                log.info("添加了动态记忆advisor");
            }
        } catch (Exception e) {
            log.error("创建动态记忆advisor失败", e);
        }
    }



    @Override
    public void clearAll() {
    }

    @Override
    public void removeForCharacter(String characterId) {
        advisorCacheManager.removeCharacterAdvisor(characterId);
        log.debug("移除角色advisor缓存: {}", characterId);
    }

    @Override
    public boolean containsCharacter(String characterId) {
        return false;
    }

    @Override
    public void createAndCacheAdvisors(PromptSpec promptSpec, String characterId) {
        if (promptSpec == null || promptSpec.getAssembledPrompt() == null) {
            log.warn("PromptSpec或AssembledPrompt为空，无法创建advisor");
            return;
        }

        // 从PromptSpec中获取角色信息，构建角色advisor
        CharacterPrompt characterPrompt = promptSpec.getAssembledPrompt().getCharacterPrompt();

        List<Advisor> characterAdvisors = promptAdvisorFactory.createCharacterAdvisors(characterPrompt);
        if (characterAdvisors != null && !characterAdvisors.isEmpty()) {
            advisorCacheManager.setCharacterAdvisor(characterId, characterAdvisors);
            log.debug("创建并缓存角色advisor: {}, 数量: {}", characterId, characterAdvisors.size());
        }
    }

    /**
     * 确保系统级advisor缓存存在，如果不存在则创建
     * @param behaviorType 行为类型
     */
    public void ensureSystemAdvisorCache(String behaviorType) {
        if (!advisorCacheManager.hasSystemAdvisors(behaviorType)) {
            // 组装系统提示词
            SystemPrompt systemPrompt = promptAssemblyService.assembleSystemPrompt(behaviorType);
            
            // 创建系统advisor
            List<Advisor> systemAdvisors = promptAdvisorFactory.createSystemAdvisors(systemPrompt);
            
            // 缓存系统advisor
            if (systemAdvisors != null && !systemAdvisors.isEmpty()) {
                advisorCacheManager.setSystemAdvisors(behaviorType, systemAdvisors);
                log.debug("创建并缓存系统advisor: behaviorType={}, 数量: {}", behaviorType, systemAdvisors.size());
            }
        }
    }
}
