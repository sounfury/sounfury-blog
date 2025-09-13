package org.sounfury.aki.domain.prompt;

import lombok.Builder;
import lombok.Getter;

/**
 * 装配后的提示词
 * 内聚SystemPrompt和CharacterPrompt，体现组合关系和职责分离
 */
@Getter
@Builder
public class AssembledPrompt {
    
    /**
     * 系统级提示词
     */
    private final SystemPrompt systemPrompt;
    
    /**
     * 角色级提示词
     */
    private final CharacterPrompt characterPrompt;
    
    /**
     * 检查是否为空的装配结果
     */
    public boolean isEmpty() {
        return (systemPrompt == null || systemPrompt.isEmpty()) &&
               (characterPrompt == null || characterPrompt.isEmpty());
    }
    
    /**
     * 检查是否有系统提示词
     */
    public boolean hasSystemPrompt() {
        return systemPrompt != null && !systemPrompt.isEmpty();
    }
    
    /**
     * 检查是否有角色信息
     */
    public boolean hasCharacterInfo() {
        return characterPrompt != null && !characterPrompt.isEmpty();
    }
    
    /**
     * 获取完整的系统提示词
     */
    public String getFullSystemPrompt() {
        return hasSystemPrompt() ? systemPrompt.getFullSystemPrompt() : "";
    }
    
    /**
     * 获取完整的角色提示词
     */
    public String getFullCharacterPrompt() {
        return hasCharacterInfo() ? characterPrompt.getFullCharacterPrompt() : "";
    }

    /**
     * 检查是否有行为指导
     */
    public boolean hasBehaviorGuide() {
        return hasSystemPrompt() && systemPrompt.hasBehaviorGuide();
    }
    
    /**
     * 检查是否有用户称呼
     */
    public boolean hasUserAddress() {
        return hasSystemPrompt() && systemPrompt.hasUserAddress();
    }
    
    /**
     * 创建空的装配结果
     */
    public static AssembledPrompt empty() {
        return AssembledPrompt.builder().build();
    }
}
