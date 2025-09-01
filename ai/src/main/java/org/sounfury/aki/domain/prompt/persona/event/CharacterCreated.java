package org.sounfury.aki.domain.prompt.persona.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 角色创建事件
 * 当新角色被创建时触发
 */
@Getter
@Builder
public class CharacterCreated {
    
    /**
     * 角色ID
     */
    private final String characterId;
    
    /**
     * 角色名称
     */
    private final String characterName;
    
    /**
     * 角色描述
     */
    private final String description;
    
    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;
}
