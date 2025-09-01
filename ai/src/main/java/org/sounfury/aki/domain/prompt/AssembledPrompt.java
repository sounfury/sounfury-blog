package org.sounfury.aki.domain.prompt;

import lombok.Builder;
import lombok.Getter;

/**
 * 装配后的提示词
 * 包含所有组装完成的提示词内容，是Template + Global + Character的最终装配结果
 */
@Getter
@Builder
public class AssembledPrompt {
    
    /**
     * 基础系统提示词
     */
    private final String baseSystemPrompt;
    
    /**
     * 行为指导提示词
     */
    private final String behaviorGuidePrompt;
    
    /**
     * 角色卡提示词
     */
    private final String characterPrompt;
    
    /**
     * 用户称呼提示词
     */
    private final String userAddressPrompt;
    
    /**
     * 检查是否为空的装配结果
     */
    public boolean isEmpty() {
        return (baseSystemPrompt == null || baseSystemPrompt.trim().isEmpty()) &&
               (behaviorGuidePrompt == null || behaviorGuidePrompt.trim().isEmpty()) &&
               (characterPrompt == null || characterPrompt.trim().isEmpty()) &&
               (userAddressPrompt == null || userAddressPrompt.trim().isEmpty());
    }
    
    /**
     * 检查是否有系统提示词
     */
    public boolean hasSystemPrompt() {
        return baseSystemPrompt != null && !baseSystemPrompt.trim().isEmpty();
    }
    
    /**
     * 检查是否有行为指导
     */
    public boolean hasBehaviorGuide() {
        return behaviorGuidePrompt != null && !behaviorGuidePrompt.trim().isEmpty();
    }
    
    /**
     * 检查是否有角色信息
     */
    public boolean hasCharacterInfo() {
        return characterPrompt != null && !characterPrompt.trim().isEmpty();
    }
    
    /**
     * 检查是否有用户称呼
     */
    public boolean hasUserAddress() {
        return userAddressPrompt != null && !userAddressPrompt.trim().isEmpty();
    }
    
    /**
     * 获取完整的系统提示词（组合所有非空部分）
     */
    public String getFullSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        
        if (hasSystemPrompt()) {
            sb.append(baseSystemPrompt);
        }
        
        if (hasBehaviorGuide()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append(behaviorGuidePrompt);
        }
        
        if (hasCharacterInfo()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append(characterPrompt);
        }
        
        if (hasUserAddress()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append(userAddressPrompt);
        }
        
        return sb.toString();
    }
    
    /**
     * 创建空的装配结果
     */
    public static AssembledPrompt empty() {
        return AssembledPrompt.builder().build();
    }
}
