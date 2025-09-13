package org.sounfury.aki.domain.conversation.memory.event;

import lombok.Getter;
import org.sounfury.aki.domain.shared.event.DomainEvent;

/**
 * 全局记忆变更事件
 * 统一处理全局记忆的创建、更新、删除操作
 */
@Getter
public class GlobalMemoryChangeEvent extends DomainEvent {
    
    private final GlobalMemoryOperationType operationType;
    
    public GlobalMemoryChangeEvent(GlobalMemoryOperationType operationType) {
        super("GlobalMemoryChange");
        this.operationType = operationType;
    }
    
    @Override
    public String toString() {
        return String.format("GlobalMemoryChangeEvent{operationType=%s, occurredOn=%s}", 
                operationType.getDescription(), 
                getOccurredOn());
    }
}
