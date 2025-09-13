package org.sounfury.aki.domain.prompt;

import lombok.Builder;
import lombok.Getter;

/**
 * 角色级提示词值对象
 * 包含角色相关的提示词内容，独立于系统信息
 */
@Getter
@Builder
public class CharacterPrompt {
    
    /**
     * 角色卡提示词
     */
    private final String characterPrompt;
    
    /**
     * 检查是否为空的角色提示词
     */
    public boolean isEmpty() {
        return characterPrompt == null || characterPrompt.trim().isEmpty();
    }
    
    /**
     * 检查是否有角色信息
     */
    public boolean hasCharacterInfo() {
        return characterPrompt != null && !characterPrompt.trim().isEmpty();
    }
    
    /**
     * 获取完整的角色提示词
     */
    public String getFullCharacterPrompt() {
        return hasCharacterInfo() ? characterPrompt : "";
    }
}
