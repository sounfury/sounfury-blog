package org.sounfury.aki.domain.prompt.persona.event;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;

/**
 * 角色删除事件
 * 当角色被删除时触发
 */
@Getter
public class CharacterDeleted extends DomainEvent {
    
    /**
     * 角色ID
     */
    private final String characterId;
    
    /**
     * 角色名称
     */
    private final String characterName;
    
    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;
    
    @Builder
    public CharacterDeleted(String characterId, String characterName, LocalDateTime timestamp) {
        super("CharacterDeleted");
        this.characterId = characterId;
        this.characterName = characterName;
        this.timestamp = timestamp;
    }
}
