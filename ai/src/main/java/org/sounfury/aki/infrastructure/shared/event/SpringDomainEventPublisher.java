package org.sounfury.aki.infrastructure.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.shared.event.DomainEventPublisher;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring事件发布器适配器
 * 将领域事件适配到Spring的事件机制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher springEventPublisher;
    
    @Override
    public void publish(DomainEvent event) {

        System.out.println("Publishing domain event: " + event);
        try {
            System.out.println("Publishing domain event: " + event);
            // 将DomainEvent包装为ApplicationEvent
            DomainEventWrapper wrapper = new DomainEventWrapper(event);
            springEventPublisher.publishEvent(wrapper);
            log.debug("领域事件发布成功: {}", event.getEventType());
        } catch (Exception e) {
            log.error("发布领域事件失败: {}", event, e);
            throw new RuntimeException("事件发布失败", e);
        }
    }
    
    @Override
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        
        log.debug("批量发布 {} 个领域事件", events.size());
        for (DomainEvent event : events) {
            publish(event);
        }
    }
    
    @Override
    @Async
    public void publishAsync(DomainEvent event) {
        try {
            log.debug("异步发布领域事件: {}", event);
            // 将DomainEvent包装为ApplicationEvent
            DomainEventWrapper wrapper = new DomainEventWrapper(event);
            springEventPublisher.publishEvent(wrapper);
            log.debug("异步领域事件发布成功: {}", event.getEventType());
        } catch (Exception e) {
            log.error("异步发布领域事件失败: {}", event, e);
            // 异步发布失败不抛异常，避免影响主流程
        }
    }
}
