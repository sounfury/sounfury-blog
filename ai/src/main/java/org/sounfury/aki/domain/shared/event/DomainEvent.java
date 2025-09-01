package org.sounfury.aki.domain.shared.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 * 所有领域事件的抽象基类，定义事件的基本属性
 */
public abstract class DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String eventType;
    
    protected DomainEvent(String eventType) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.eventType = eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DomainEvent that = (DomainEvent) obj;
        return eventId.equals(that.eventId);
    }
    
    @Override
    public int hashCode() {
        return eventId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s{eventId='%s', occurredOn=%s}", 
                getClass().getSimpleName(), eventId, occurredOn);
    }
}
