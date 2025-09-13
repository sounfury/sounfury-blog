package org.sounfury.aki.infrastructure.llm.advisor.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.spec.PromptSpec;
import org.sounfury.aki.domain.prompt.AssembledPrompt;
import org.sounfury.aki.domain.prompt.SystemPrompt;
import org.sounfury.aki.domain.prompt.CharacterPrompt;

import org.sounfury.aki.contracts.constant.AdvisorOrder;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.infrastructure.llm.advisor.base.PromptAdvisor;
import org.sounfury.aki.infrastructure.llm.advisor.base.GlobalMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromptAdvisorFactory {

    /**
     * 创建系统级Advisor列表
     * @param systemPrompt 系统提示词
     * @return 系统advisor列表
     */
    public List<Advisor> createSystemAdvisors(SystemPrompt systemPrompt) {
        try {
            if (systemPrompt == null || systemPrompt.isEmpty()) {
                log.warn("系统提示词为空，返回空列表");
                return List.of();
            }

            List<Advisor> advisors = new ArrayList<>();

            // 1. 系统提示词Advisor（最高优先级）
            if (systemPrompt.hasSystemPrompt()) {
                Advisor systemPromptAdvisor = new PromptAdvisor("SystemPromptAdvisor", AdvisorOrder.SYSTEM_PROMPT, systemPrompt.getBaseSystemPrompt());
                advisors.add(systemPromptAdvisor);
            }

            // 2. 行为指导Advisor
            if (systemPrompt.hasBehaviorGuide()) {
                Advisor behaviorAdvisor = new PromptAdvisor("BehaviorGuideAdvisor", AdvisorOrder.BEHAVIOR_GUIDE, systemPrompt.getBehaviorGuidePrompt());
                advisors.add(behaviorAdvisor);
            }

            // 3. 用户称呼Advisor（如果有）
            if (systemPrompt.hasUserAddress()) {
                Advisor userAddressAdvisor = new PromptAdvisor("UserAddressAdvisor", AdvisorOrder.USER_ADDRESS, systemPrompt.getUserAddressPrompt());
                advisors.add(userAddressAdvisor);
            }

            log.debug("系统advisor组装完成，数量: {}", advisors.size());
            return advisors;

        } catch (Exception e) {
            log.error("创建系统advisor列表失败", e);
            return List.of();
        }
    }

    /**
     * 创建角色级Advisor列表
     * @param characterPrompt 角色提示词
     * @return 角色advisor列表
     */
    public List<Advisor> createCharacterAdvisors(CharacterPrompt characterPrompt) {
        try {
            if (characterPrompt == null || characterPrompt.isEmpty()) {
                log.warn("角色提示词为空，返回空列表");
                return List.of();
            }

            List<Advisor> advisors = new ArrayList<>();

            // 角色卡Advisor
            if (characterPrompt.hasCharacterInfo()) {
                Advisor characterAdvisor = new PromptAdvisor("CharacterCardAdvisor", AdvisorOrder.CHARACTER_CARD, characterPrompt.getCharacterPrompt());
                advisors.add(characterAdvisor);
            }

            log.debug("角色advisor组装完成，数量: {}", advisors.size());
            return advisors;

        } catch (Exception e) {
            log.error("创建角色advisor列表失败", e);
            return List.of();
        }
    }

    /**
     * 创建单个合并的提示词Advisor
     * @param promptSpec 提示词规格
     * @return 合并的advisor
     */
    public Advisor createAdvisor(PromptSpec promptSpec) {
        return createFullPromptAdvisor(promptSpec);
    }

    /**
     * 创建多个分离的提示词Advisor列表
     * @param promptSpec 提示词规格
     * @return advisor列表
     */
    public List<Advisor> createAdvisors(PromptSpec promptSpec) {
        return createPromptAdvisors(promptSpec);
    }

    //=========================advisor创建====================================//

    /**
     * 创建单个提示词Advisor
     * 将PromptSpec合并为一个Advisor
     */
    private Advisor createFullPromptAdvisor(PromptSpec promptSpec) {
        try {
            if (promptSpec == null || !promptSpec.isValid()) {
                log.warn("提示词规格无效，返回null");
                return null;
            }

            StringBuilder fullPrompt = new StringBuilder();

            // 1. 组装AssembledPrompt部分
            if (promptSpec.getAssembledPrompt() != null && !promptSpec.getAssembledPrompt().isEmpty()) {
                fullPrompt.append(promptSpec.getAssembledPrompt().getFullSystemPrompt());
            }

            if (fullPrompt.length() == 0) {
                log.warn("合并后的提示词为空，返回null");
                return null;
            }

            return new PromptAdvisor("CompositePromptAdvisor", AdvisorOrder.SYSTEM_PROMPT, fullPrompt.toString());

        } catch (Exception e) {
            log.error("创建提示词Advisor失败", e);
            return null;
        }
    }

    /**
     * 创建多个提示词Advisor列表（分离模式）
     */
    private List<Advisor> createPromptAdvisors(PromptSpec promptSpec) {
        try {
            if (promptSpec == null || !promptSpec.isValid()) {
                log.warn("提示词规格无效，返回空列表");
                return List.of();
            }

            List<Advisor> advisors = new ArrayList<>();

            // 处理AssembledPrompt部分
            if (promptSpec.getAssembledPrompt() != null && !promptSpec.getAssembledPrompt().isEmpty()) {
                addAssembledPromptAdvisors(promptSpec.getAssembledPrompt(), advisors);
            }

            log.debug("提示词advisor组装完成，数量: {}", advisors.size());
            return advisors;

        } catch (Exception e) {
            log.error("创建提示词advisor列表失败", e);
            return List.of();
        }
    }

    /**
     * 添加AssembledPrompt相关的advisor
     */
    private void addAssembledPromptAdvisors(AssembledPrompt assembledPrompt, List<Advisor> advisors) {
        // 1. 系统提示词Advisor（最高优先级）
        if (assembledPrompt.hasSystemPrompt()) {
            Advisor systemPromptAdvisor = new PromptAdvisor("SystemPromptAdvisor", AdvisorOrder.SYSTEM_PROMPT, assembledPrompt.getSystemPrompt().getBaseSystemPrompt());
            advisors.add(systemPromptAdvisor);
        }

        // 2. 行为指导Advisor
        if (assembledPrompt.hasBehaviorGuide()) {
            Advisor behaviorAdvisor = new PromptAdvisor("BehaviorGuideAdvisor", AdvisorOrder.BEHAVIOR_GUIDE, assembledPrompt.getSystemPrompt().getBehaviorGuidePrompt());
            advisors.add(behaviorAdvisor);
        }

        // 3. 角色卡Advisor
        if (assembledPrompt.hasCharacterInfo()) {
            Advisor characterAdvisor = new PromptAdvisor("CharacterCardAdvisor", AdvisorOrder.CHARACTER_CARD, assembledPrompt.getCharacterPrompt().getCharacterPrompt());
            advisors.add(characterAdvisor);
        }

        // 4. 用户称呼Advisor（如果有）
        if (assembledPrompt.hasUserAddress()) {
            Advisor userAddressAdvisor = new PromptAdvisor("UserAddressAdvisor", AdvisorOrder.USER_ADDRESS, assembledPrompt.getSystemPrompt().getUserAddressPrompt());
            advisors.add(userAddressAdvisor);
        }
    }

    /**
     * 创建GlobalMemory相关的advisor
     * 使用专门的GlobalMemoryAdvisor，类型为SYSTEM
     */
    public Advisor creatGlobalMemoryAdvisor(List<GlobalMemory> globalMemories) {
        if (globalMemories == null || globalMemories.isEmpty()) {
            log.warn("全局记忆列表为空，返回null");
            return null;
        }

        // 合并多个全局记忆内容
        StringBuilder combinedContent = new StringBuilder();
        for (int i = 0; i < globalMemories.size(); i++) {
            GlobalMemory globalMemory = globalMemories.get(i);
            if (globalMemory.getContent() != null && !globalMemory.getContent().trim().isEmpty()) {
                if (combinedContent.length() > 0) {
                    combinedContent.append("\n\n");
                }
                combinedContent.append(globalMemory.getContent());
            }
        }

        if (combinedContent.length() == 0) {
            log.warn("所有全局记忆内容均为空，返回null");
            return null;
        }

        return new GlobalMemoryAdvisor("GlobalMemoryAdvisor", combinedContent.toString());
    }


}
