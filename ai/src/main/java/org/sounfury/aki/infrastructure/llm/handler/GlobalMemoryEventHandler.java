package org.sounfury.aki.infrastructure.llm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.domain.conversation.memory.event.GlobalMemoryChangeEvent;
import org.sounfury.aki.domain.conversation.memory.event.GlobalMemoryOperationType;
import org.sounfury.aki.domain.conversation.memory.repository.GlobalMemoryRepository;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.sounfury.aki.infrastructure.llm.advisor.AdvisorCacheManager;
import org.sounfury.aki.infrastructure.llm.advisor.factory.PromptAdvisorFactory;
import org.sounfury.aki.infrastructure.shared.event.DomainEventWrapper;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 全局记忆事件处理器
 * 监听全局记忆变更事件，自动重建全局记忆advisor缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalMemoryEventHandler {

    private final GlobalMemoryRepository globalMemoryRepository;
    private final PromptAdvisorFactory promptAdvisorFactory;
    private final AdvisorCacheManager advisorCacheManager;

    /**
     * 处理全局记忆变更事件
     */
    @EventListener
    @Async
    public void handleGlobalMemoryChange(DomainEventWrapper wrapper) {
        DomainEvent domainEvent = wrapper.getDomainEvent();
        log.info("收到领域事件: {}", domainEvent);
        if (!(domainEvent instanceof GlobalMemoryChangeEvent)) {
            return;
        }
        
        GlobalMemoryChangeEvent event = (GlobalMemoryChangeEvent) domainEvent;
        GlobalMemoryOperationType operationType = event.getOperationType();
        log.info("收到全局记忆变更事件: {}", event);
        
        switch (operationType) {
            case CREATE:
                rebuildGlobalMemoryAdvisor("创建");
                break;
            case UPDATE:
                rebuildGlobalMemoryAdvisor("更新");
                break;
            case DELETE:
                rebuildGlobalMemoryAdvisor("删除");
                break;
            default:
                log.warn("未知的全局记忆操作类型: {}", operationType);
                break;
        }
    }

    /**
     * 重建全局记忆advisor缓存
     * 统一的重建逻辑，适用于所有变更类型
     */
    private void rebuildGlobalMemoryAdvisor(String operation) {
        try {
            log.info("开始重建全局记忆advisor缓存，触发操作: {}", operation);

            // 1. 重新查询所有全局记忆（按时间戳排序）
            List<GlobalMemory> allMemories = globalMemoryRepository.findAllOrderByTimestampDesc();
            log.debug("查询到 {} 条全局记忆", allMemories.size());

            // 2. 重新创建GlobalMemoryAdvisor
            Advisor newGlobalMemoryAdvisor = promptAdvisorFactory.creatGlobalMemoryAdvisor(allMemories);
            
            // 3. 更新缓存
            advisorCacheManager.setGlobalMemoryAdvisor(newGlobalMemoryAdvisor);

            if (newGlobalMemoryAdvisor != null) {
                log.info("全局记忆advisor缓存重建成功，操作: {}，advisor名称: {}", 
                        operation, newGlobalMemoryAdvisor.getName());
            } else {
                log.info("全局记忆advisor缓存清空，操作: {}（无有效全局记忆）", operation);
            }

        } catch (Exception e) {
            log.error("重建全局记忆advisor缓存失败，操作: {}", operation, e);
            // 不抛异常，避免影响业务流程
        }
    }
}
