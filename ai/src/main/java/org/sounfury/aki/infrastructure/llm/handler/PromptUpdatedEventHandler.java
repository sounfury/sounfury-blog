package org.sounfury.aki.infrastructure.llm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.event.PromptUpdated;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.sounfury.aki.infrastructure.llm.advisor.AdvisorCacheManager;
import org.sounfury.aki.infrastructure.shared.event.DomainEventWrapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 提示词更新事件处理器
 * 监听提示词更新事件，根据categoryKey类型精确清除相关缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromptUpdatedEventHandler {

    private final AdvisorCacheManager advisorCacheManager;

    /**
     * 处理提示词更新事件
     */
    @EventListener
    public void handlePromptUpdated(DomainEventWrapper wrapper) {
        DomainEvent domainEvent = wrapper.getDomainEvent();
        if (!(domainEvent instanceof PromptUpdated)) {
            return;
        }
        
        PromptUpdated event = (PromptUpdated) domainEvent;
        try {
            log.info("处理提示词更新事件: categoryKey={}", event.getCategoryKey());
            
            String categoryKey = event.getCategoryKey();
            
            // 根据categoryKey类型决定缓存清理策略
            if (isSystemPromptCategory(categoryKey)) {
                // 系统级提示词更新，清除所有系统advisor缓存
                advisorCacheManager.clearAllSystemAdvisors();
                log.info("系统级提示词更新，已清除所有系统advisor缓存: categoryKey={}", categoryKey);
                
            } else if (isCharacterPromptCategory(categoryKey)) {
                // 角色级提示词更新，清除所有角色advisor缓存
                advisorCacheManager.clearAllCharacterAdvisors();
                log.info("角色级提示词更新，已清除所有角色advisor缓存: categoryKey={}", categoryKey);
                
            } else {
                // 未知类型，保守起见清除所有缓存
                advisorCacheManager.clearAllAdvisors();
                log.warn("未知类型的提示词更新，已清除所有advisor缓存: categoryKey={}", categoryKey);
            }
            
        } catch (Exception e) {
            log.error("处理提示词更新事件失败: categoryKey={}", event.getCategoryKey(), e);
            // 不抛出异常，避免影响其他事件处理
        }
    }

    /**
     * 判断是否为系统级提示词类别
     */
    private boolean isSystemPromptCategory(String categoryKey) {
        if (categoryKey == null) {
            return false;
        }
        
        return categoryKey.startsWith("system.") ||
               categoryKey.startsWith("behavior.") ||
               categoryKey.startsWith("user.") ||
               categoryKey.startsWith("task.");
    }

    /**
     * 判断是否为角色级提示词类别
     */
    private boolean isCharacterPromptCategory(String categoryKey) {
        if (categoryKey == null) {
            return false;
        }
        
        return categoryKey.startsWith("char.");
    }
}
