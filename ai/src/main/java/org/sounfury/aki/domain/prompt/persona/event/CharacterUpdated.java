package org.sounfury.aki.domain.prompt.persona.event;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;

/**
 * 角色更新事件
 * 当角色信息被更新时触发
 */
@Getter
public class CharacterUpdated extends DomainEvent {
    
    /**
     * 角色ID
     */
    private final String characterId;
    
    /**
     * 角色名称
     */
    private final String characterName;
    
    /**
     * 更新类型（如：card_updated, description_updated）
     */
    private final String updateType;
    
    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;
    
    @Builder
    public CharacterUpdated(String characterId, String characterName, String updateType, LocalDateTime timestamp) {
        super("CharacterUpdated");
        this.characterId = characterId;
        this.characterName = characterName;
        this.updateType = updateType;
        this.timestamp = timestamp;
    }
}
