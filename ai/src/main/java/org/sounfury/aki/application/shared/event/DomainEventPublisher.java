package org.sounfury.aki.application.shared.event;

import org.sounfury.aki.domain.shared.event.DomainEvent;

/**
 * 领域事件发布器接口
 * 应用层用于发布领域事件的抽象接口，由基础设施层实现
 */
public interface DomainEventPublisher {
    
    /**
     * 发布单个领域事件
     * @param event 要发布的领域事件
     */
    void publish(DomainEvent event);
    
    /**
     * 批量发布领域事件
     * @param events 要发布的领域事件列表
     */
    void publishAll(java.util.List<DomainEvent> events);
    
    /**
     * 异步发布领域事件
     * @param event 要发布的领域事件
     */
    void publishAsync(DomainEvent event);
}
