package org.sounfury.aki.domain.prompt.persona.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 角色激活事件
 * 当角色被激活时触发
 */
@Getter
@Builder
public class CharacterActivated {
    
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
