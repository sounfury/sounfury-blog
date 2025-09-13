package org.sounfury.aki.infrastructure.shared.event;

import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 领域事件包装器
 * 将DomainEvent包装为Spring的ApplicationEvent，使其能被Spring事件机制正确处理
 */
public class DomainEventWrapper extends ApplicationEvent {
    
    private final DomainEvent domainEvent;
    
    /**
     * 构造函数
     * @param domainEvent 要包装的领域事件
     */
    public DomainEventWrapper(DomainEvent domainEvent) {
        super(domainEvent);
        this.domainEvent = domainEvent;
    }
    
    /**
     * 获取包装的领域事件
     * @return 领域事件
     */
    public DomainEvent getDomainEvent() {
        return domainEvent;
    }
    
    /**
     * 获取事件类型
     * @return 事件类型
     */
    public String getEventType() {
        return domainEvent.getEventType();
    }
    
    /**
     * 获取事件ID
     * @return 事件ID
     */
    public String getEventId() {
        return domainEvent.getEventId();
    }
    
    @Override
    public String toString() {
        return String.format("DomainEventWrapper{domainEvent=%s}", domainEvent);
    }
}
