package org.sounfury.aki.infrastructure.llm.advisor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.infrastructure.llm.advisor.factory.MemoryAdvisorFactory;
import org.sounfury.aki.infrastructure.shared.config.RagSettings;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring AI Advisor组装器
 * 负责根据事件中的提示词信息组装各种Advisor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiAdvisorAssembler {

    private final MemoryAdvisorFactory memoryAdvisorFactory;
    // TODO: 注入世界书向量存储提供者
    // private final WorldBookVectorStoreProvider vectorStoreProvider;
    
    /**
     * 根据提示词信息组装Advisor列表
     * @param baseSystemPrompt 基础系统提示词
     * @param behaviorGuidePrompt 行为指导提示词
     * @param characterPrompt 角色卡提示词
     * @param userAddressPrompt 用户称呼提示词
     * @param enableRag 是否启用RAG
     * @param ragSettings RAG设置
     * @return Spring AI Advisor列表
     */
    public List<Advisor> assembleAdvisorsFromPrompts(String baseSystemPrompt, 
                                                   String behaviorGuidePrompt,
                                                   String characterPrompt, 
                                                   String userAddressPrompt,
                                                   boolean enableRag,
                                                   RagSettings ragSettings) {
        log.debug("开始组装Advisor，基于提示词信息");
        
        List<Advisor> advisors = new ArrayList<>();
        
        // 1. 系统提示词Advisor（最高优先级）
        if (baseSystemPrompt != null && !baseSystemPrompt.trim().isEmpty()) {
            Advisor systemPromptAdvisor = new PromptAdvisor("SystemPromptAdvisor", 100, baseSystemPrompt);
            advisors.add(systemPromptAdvisor);
        }

        // 2. 行为指导Advisor
        if (behaviorGuidePrompt != null && !behaviorGuidePrompt.trim().isEmpty()) {
            Advisor behaviorAdvisor = new PromptAdvisor("BehaviorGuideAdvisor", 200, behaviorGuidePrompt);
            advisors.add(behaviorAdvisor);
        }

        // 2. 角色卡Advisor
        if (characterPrompt != null && !characterPrompt.trim().isEmpty()) {
            Advisor characterAdvisor = new PromptAdvisor("CharacterCardAdvisor", 300, characterPrompt);
            advisors.add(characterAdvisor);
        }

//        // 3. 用户称呼Advisor
//        if (userAddressPrompt != null && !userAddressPrompt.trim().isEmpty()) {
//            Advisor userAddressAdvisor = new PromptAdvisor("UserAddressAdvisor", 300, userAddressPrompt);
//            advisors.add(userAddressAdvisor);
//        }

        // 4. RAG Advisor
        if (enableRag) {
            Advisor ragAdvisor = createRagAdvisor(ragSettings);
            if (ragAdvisor != null) {
                advisors.add(ragAdvisor);
            }
        }
        
        log.debug("Advisor组装完成，数量: {}", advisors.size());
        return advisors;
    }

    /**
     * 创建RAG Advisor
     */
    private Advisor createRagAdvisor(RagSettings ragSettings) {
        try {
            // TODO: 实现RAG Advisor创建逻辑
            log.debug("创建RAG Advisor，设置: {}", ragSettings);
            return null; // 暂时返回null
        } catch (Exception e) {
            log.error("创建RAG Advisor失败", e);
            return null;
        }
    }





}
