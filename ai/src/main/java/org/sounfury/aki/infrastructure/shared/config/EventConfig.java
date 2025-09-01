package org.sounfury.aki.infrastructure.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 事件配置
 * 配置领域事件的处理机制
 */
@Slf4j
@Component
public class EventConfig {
    
    /**
     * 全局领域事件监听器
     * 用于日志记录和监控
     */
    @EventListener
    @Async
    public void handleDomainEvent(DomainEvent event) {
        log.info("处理领域事件: {} [{}] 发生时间: {}", 
                event.getEventType(), 
                event.getEventId(), 
                event.getOccurredOn());
    }
}
