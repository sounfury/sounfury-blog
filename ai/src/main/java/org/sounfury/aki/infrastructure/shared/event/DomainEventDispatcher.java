package org.sounfury.aki.infrastructure.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.sounfury.aki.domain.shared.event.DomainEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 领域事件分发器
 * 统一处理DomainEventHandler接口的调用，实现DomainEvent到对应处理器的分发
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventDispatcher {
    
    private final List<DomainEventHandler<?>> domainEventHandlers;
    private final Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlerCache = new ConcurrentHashMap<>();
    
    /**
     * 监听包装的领域事件并分发给对应的DomainEventHandler
     */
    @EventListener
    @Async
    public void handleDomainEventWrapper(DomainEventWrapper wrapper) {
        DomainEvent domainEvent = wrapper.getDomainEvent();
        
        try {
            DomainEventHandler<DomainEvent> handler = findHandler(domainEvent);
            if (handler != null) {
                log.debug("分发领域事件到处理器: event={}, handler={}", 
                         domainEvent.getEventType(), handler.getHandlerName());
                handler.handle(domainEvent);
                log.debug("领域事件处理完成: event={}, handler={}", 
                         domainEvent.getEventType(), handler.getHandlerName());
            } else {
                log.debug("未找到对应的DomainEventHandler: eventType={}", domainEvent.getEventType());
            }
        } catch (Exception e) {
            log.error("分发领域事件失败: event={}", domainEvent, e);
            // 不抛出异常，避免影响其他事件处理
        }
    }
    
    /**
     * 查找对应的事件处理器
     */
    @SuppressWarnings("unchecked")
    private DomainEventHandler<DomainEvent> findHandler(DomainEvent event) {
        Class<? extends DomainEvent> eventClass = event.getClass();
        
        // 先从缓存中查找
        DomainEventHandler<? extends DomainEvent> cachedHandler = handlerCache.get(eventClass);
        if (cachedHandler != null) {
            return (DomainEventHandler<DomainEvent>) cachedHandler;
        }
        
        // 遍历所有处理器，找到匹配的
        for (DomainEventHandler<?> handler : domainEventHandlers) {
            if (handler.getEventType().equals(eventClass)) {
                handlerCache.put(eventClass, handler);
                return (DomainEventHandler<DomainEvent>) handler;
            }
        }
        
        return null;
    }
}
