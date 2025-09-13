package org.sounfury.aki.domain.prompt.event;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;

/**
 * 提示词更新事件
 * 当提示词内容被更新时触发
 */
@Getter
public class PromptUpdated extends DomainEvent {
    
    /**
     * 提示词ID
     */
    private final Integer promptId;
    
    /**
     * 分类Key
     */
    private final String categoryKey;
    
    /**
     * 提示词类型
     */
    private final String promptType;
    
    /**
     * 更新类型（如：content_updated, status_updated）
     */
    private final String updateType;
    
    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;
    
    @Builder
    public PromptUpdated(Integer promptId, String categoryKey, String promptType, String updateType, LocalDateTime timestamp) {
        super("PromptUpdated");
        this.promptId = promptId;
        this.categoryKey = categoryKey;
        this.promptType = promptType;
        this.updateType = updateType;
        this.timestamp = timestamp;
    }
}
