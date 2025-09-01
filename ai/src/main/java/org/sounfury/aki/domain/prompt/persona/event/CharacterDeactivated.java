package org.sounfury.aki.domain.prompt.persona.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 角色停用事件
 * 当角色被停用时触发
 */
@Getter
@Builder
public class CharacterDeactivated {
    
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
}
