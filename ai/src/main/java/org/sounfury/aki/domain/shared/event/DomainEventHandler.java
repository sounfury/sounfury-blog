package org.sounfury.aki.domain.shared.event;

/**
 * 领域事件处理器接口
 * 定义处理特定类型领域事件的契约
 */
public interface DomainEventHandler<T extends DomainEvent> {
    
    /**
     * 处理领域事件
     * @param event 要处理的领域事件
     */
    void handle(T event);
    
    /**
     * 获取处理器能够处理的事件类型
     * @return 事件类型的Class对象
     */
    Class<T> getEventType();
    
    /**
     * 获取处理器名称
     * @return 处理器名称
     */
    default String getHandlerName() {
        return this.getClass().getSimpleName();
    }
}
