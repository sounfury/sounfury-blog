package org.sounfury.aki.domain.prompt.persona.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 角色更新事件
 * 当角色信息被更新时触发
 */
@Getter
@Builder
public class CharacterUpdated {
    
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
}
