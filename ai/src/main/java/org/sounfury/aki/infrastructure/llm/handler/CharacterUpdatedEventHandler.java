package org.sounfury.aki.infrastructure.llm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.persona.event.CharacterDeleted;
import org.sounfury.aki.domain.prompt.persona.event.CharacterUpdated;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.sounfury.aki.domain.shared.event.DomainEventHandler;
import org.sounfury.aki.infrastructure.llm.advisor.AdvisorCacheManager;
import org.sounfury.aki.infrastructure.shared.event.DomainEventWrapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 角色更新事件处理器
 * 监听角色更新和删除事件，清除相关的advisor缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CharacterUpdatedEventHandler {

    private final AdvisorCacheManager advisorCacheManager;

    /**
     * 处理角色更新事件
     */
    @EventListener
    public void handleCharacterUpdated(DomainEventWrapper wrapper) {
        DomainEvent domainEvent = wrapper.getDomainEvent();
        if (!(domainEvent instanceof CharacterUpdated)) {
            return;
        }
        
        CharacterUpdated event = (CharacterUpdated) domainEvent;
        try {
            log.info("处理角色更新事件: characterId={}, updateType={}", 
                    event.getCharacterId(), event.getUpdateType());
            
            // 清除角色advisor缓存
            advisorCacheManager.removeCharacterAdvisor(event.getCharacterId());
            
            log.info("角色advisor缓存清除成功: characterId={}", event.getCharacterId());
            
        } catch (Exception e) {
            log.error("处理角色更新事件失败: characterId={}", event.getCharacterId(), e);
            // 不抛出异常，避免影响其他事件处理
        }
    }

    /**
     * 处理角色删除事件
     */
    @EventListener
    public void handleCharacterDeleted(DomainEventWrapper wrapper) {
        DomainEvent domainEvent = wrapper.getDomainEvent();
        if (!(domainEvent instanceof CharacterDeleted)) {
            return;
        }
        
        CharacterDeleted event = (CharacterDeleted) domainEvent;
        try {
            log.info("处理角色删除事件: characterId={}", event.getCharacterId());
            
            // 清除角色advisor缓存
            advisorCacheManager.removeCharacterAdvisor(event.getCharacterId());
            
            log.info("角色advisor缓存清除成功: characterId={}", event.getCharacterId());
            
        } catch (Exception e) {
            log.error("处理角色删除事件失败: characterId={}", event.getCharacterId(), e);
            // 不抛出异常，避免影响其他事件处理
        }
    }
}
