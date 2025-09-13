package org.sounfury.aki.domain.prompt;

import lombok.Builder;
import lombok.Getter;

/**
 * 系统级提示词值对象
 * 包含系统相关的提示词内容，独立于角色信息
 */
@Getter
@Builder
public class SystemPrompt {
    
    /**
     * 基础系统提示词
     */
    private final String baseSystemPrompt;
    
    /**
     * 行为指导提示词
     */
    private final String behaviorGuidePrompt;
    
    /**
     * 用户称呼提示词
     */
    private final String userAddressPrompt;
    
    /**
     * 检查是否为空的系统提示词
     */
    public boolean isEmpty() {
        return (baseSystemPrompt == null || baseSystemPrompt.trim().isEmpty()) &&
               (behaviorGuidePrompt == null || behaviorGuidePrompt.trim().isEmpty()) &&
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
     * 检查是否有用户称呼
     */
    public boolean hasUserAddress() {
        return userAddressPrompt != null && !userAddressPrompt.trim().isEmpty();
    }
    
    /**
     * 获取完整的系统提示词（组合所有非空部分）
     */
    public String getFullSystemPrompt() {
        StringBuilder fullPrompt = new StringBuilder();
        
        if (hasSystemPrompt()) {
            fullPrompt.append(baseSystemPrompt);
        }
        
        if (hasBehaviorGuide()) {
            if (fullPrompt.length() > 0) {
                fullPrompt.append("\n\n");
            }
            fullPrompt.append(behaviorGuidePrompt);
        }
        
        if (hasUserAddress()) {
            if (fullPrompt.length() > 0) {
                fullPrompt.append("\n\n");
            }
            fullPrompt.append(userAddressPrompt);
        }
        
        return fullPrompt.toString();
    }
}
