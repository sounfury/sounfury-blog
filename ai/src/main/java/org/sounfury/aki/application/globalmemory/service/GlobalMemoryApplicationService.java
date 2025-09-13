package org.sounfury.aki.application.globalmemory.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.globalmemory.dto.GlobalMemoryAddReq;
import org.sounfury.aki.application.globalmemory.dto.GlobalMemoryResponse;
import org.sounfury.aki.application.globalmemory.dto.GlobalMemoryUpdateReq;
import org.sounfury.aki.application.shared.event.DomainEventPublisher;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.domain.conversation.memory.event.GlobalMemoryChangeEvent;
import org.sounfury.aki.domain.conversation.memory.event.GlobalMemoryOperationType;
import org.sounfury.aki.domain.conversation.memory.repository.GlobalMemoryRepository;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.sounfury.utils.CacheUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局记忆应用服务
 * 负责处理全局记忆相关的业务流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalMemoryApplicationService {

    private final GlobalMemoryRepository globalMemoryRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * 全局记忆最大数量限制
     */
    private static final int MAX_GLOBAL_MEMORIES = 10;

    /**
     * 缓存名称
     */
    private static final String CACHE_NAME = GlobalMemoryRepository.CACHE_NAME;

    /**
     * 应用启动时预热缓存
     */
    @PostConstruct
    public void warmUpCache() {
        try {
            log.info("开始预热全局记忆缓存");
            
            // 预热所有查询缓存
            globalMemoryRepository.findGMemoryAll();
            globalMemoryRepository.findAllOrderByTimestampDesc();
            
            log.info("全局记忆缓存预热完成");
        } catch (Exception e) {
            log.error("全局记忆缓存预热失败", e);
        }
    }

    /**
     * 查询所有全局记忆
     */
    public List<GlobalMemoryResponse> getAllGlobalMemories() {
        try {
            List<GlobalMemory> globalMemories = globalMemoryRepository.findAllOrderByTimestampDesc();
            
            List<GlobalMemoryResponse> responses = globalMemories.stream()
                    .map(GlobalMemoryResponse::from)
                    .collect(Collectors.toList());
            
            log.debug("查询到{}条全局记忆", responses.size());
            return responses;
            
        } catch (Exception e) {
            log.error("查询全局记忆失败", e);
            throw new RuntimeException("查询全局记忆失败", e);
        }
    }

    /**
     * 根据ID查询全局记忆
     */
    public GlobalMemoryResponse getGlobalMemoryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("记忆ID不能为空");
        }

        try {
            GlobalMemory globalMemory = globalMemoryRepository.findGMemoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("记忆不存在: " + id));
            
            return GlobalMemoryResponse.from(globalMemory);
            
        } catch (Exception e) {
            log.error("根据ID查询全局记忆失败: {}", id, e);
            throw new RuntimeException("查询全局记忆失败", e);
        }
    }

    /**
     * 新增全局记忆
     */
    @Transactional
    public GlobalMemoryResponse addGlobalMemory(GlobalMemoryAddReq request) {
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("记忆内容不能为空");
        }

        try {
            // 检查数量限制
            long currentCount = globalMemoryRepository.count();
            if (currentCount >= MAX_GLOBAL_MEMORIES) {
                throw new IllegalStateException("全局记忆数量不能超过" + MAX_GLOBAL_MEMORIES + "条，当前已有" + currentCount + "条");
            }

            // 创建新的全局记忆（领域事件在create方法中产生）
            GlobalMemory globalMemory = GlobalMemory.create(request.getContent().trim());
            GlobalMemory savedMemory = globalMemoryRepository.save(globalMemory);
            
            // 发布领域实体中产生的事件
            publishDomainEvents(savedMemory);
            
            log.info("新增全局记忆成功: {}", savedMemory.getId());
            return GlobalMemoryResponse.from(savedMemory);
            
        } catch (Exception e) {
            log.error("新增全局记忆失败: {}", request.getContent(), e);
            throw new RuntimeException("新增全局记忆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新全局记忆
     */
    @Transactional
    public GlobalMemoryResponse updateGlobalMemory(GlobalMemoryUpdateReq request) {
        if (request == null || request.getId() == null) {
            throw new IllegalArgumentException("记忆ID不能为空");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("记忆内容不能为空");
        }

        try {
            // 查找现有记忆
            GlobalMemory existingMemory = globalMemoryRepository.findGMemoryById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("记忆不存在: " + request.getId()));


            existingMemory.updateContent(request.getContent().trim());
            globalMemoryRepository.save(existingMemory);
            
            // 发布领域实体中产生的事件
            publishDomainEvents(existingMemory);
            
            log.info("更新全局记忆成功: {}", existingMemory.getId());
            return GlobalMemoryResponse.from(existingMemory);
            
        } catch (Exception e) {
            log.error("更新全局记忆失败: {}", request.getId(), e);
            throw new RuntimeException("更新全局记忆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除全局记忆
     */
    @Transactional
    public void deleteGlobalMemory(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("记忆ID不能为空");
        }

        try {
            // 检查记忆是否存在并获取内容用于事件
            GlobalMemory existingMemory = globalMemoryRepository.findGMemoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("记忆不存在: " + id));
            
            String deletedContent = existingMemory.getContent();

            globalMemoryRepository.deleteById(id);
            
            // 删除操作需要在应用层发布事件（因为实体已删除）
            domainEventPublisher.publish(new GlobalMemoryChangeEvent(GlobalMemoryOperationType.DELETE));
            
            log.info("删除全局记忆成功: {}", id);
            
        } catch (Exception e) {
            log.error("删除全局记忆失败: {}", id, e);
            throw new RuntimeException("删除全局记忆失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取全局记忆统计信息
     */
    public GlobalMemoryStats getGlobalMemoryStats() {
        try {
            long totalCount = globalMemoryRepository.count();
            return new GlobalMemoryStats(totalCount, MAX_GLOBAL_MEMORIES, MAX_GLOBAL_MEMORIES - totalCount);
            
        } catch (Exception e) {
            log.error("获取全局记忆统计信息失败", e);
            return new GlobalMemoryStats(0, MAX_GLOBAL_MEMORIES, MAX_GLOBAL_MEMORIES);
        }
    }

    /**
     * 清除所有缓存
     */
    public void clearCache() {
        try {
            CacheUtils.clear(CACHE_NAME);
            log.info("全局记忆缓存已清除");
        } catch (Exception e) {
            log.error("清除全局记忆缓存失败", e);
        }
    }

    /**
     * 重新加载缓存
     */
    public void reloadCache() {
        try {
            clearCache();
            warmUpCache();
            log.info("全局记忆缓存已重新加载");
        } catch (Exception e) {
            log.error("重新加载全局记忆缓存失败", e);
        }
    }

    /**
     * 全局记忆统计信息
     */
    public static class GlobalMemoryStats {
        private final long totalCount;
        private final int maxCount;
        private final long remainingCount;

        public GlobalMemoryStats(long totalCount, int maxCount, long remainingCount) {
            this.totalCount = totalCount;
            this.maxCount = maxCount;
            this.remainingCount = remainingCount;
        }

        public long getTotalCount() { return totalCount; }
        public int getMaxCount() { return maxCount; }
        public long getRemainingCount() { return remainingCount; }
    }

    /**
     * 发布聚合中的领域事件
     */
    private void publishDomainEvents(GlobalMemory globalMemory) {
        try {
            for (DomainEvent event : globalMemory.getDomainEvents()) {
                domainEventPublisher.publish(event);
                log.debug("发布领域事件: eventType={}, globalMemoryId={}",
                         event.getEventType(), 
                         globalMemory.getId());
            }
            // 清除已发布的事件
            globalMemory.clearDomainEvents();
        } catch (Exception e) {
            log.error("发布领域事件失败: globalMemoryId={}", globalMemory.getId(), e);
            throw new RuntimeException("发布领域事件失败", e);
        }
    }
}
