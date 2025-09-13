package org.sounfury.aki.infrastructure.llm.advisor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.infrastructure.llm.advisor.base.PromptLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Advisor缓存管理器
 * 负责缓存通用advisor，支持热重载和类型分组管理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvisorCacheManager {

    // 全局记忆advisor缓存（兼容性保留）
    private final AtomicReference<Advisor> globalMemoryAdvisor = new AtomicReference<>();
    
    // 系统级advisor缓存（按behaviorType区分）
    private final ConcurrentMap<String, List<Advisor>> systemAdvisors = new ConcurrentHashMap<>();
    
    // 角色advisor缓存（懒加载）
    private final ConcurrentMap<String, List<Advisor>> characterAdvisors = new ConcurrentHashMap<>();



    /**
     * 获取全局记忆advisor
     */
    public Advisor getGlobalMemoryAdvisor() {
        return globalMemoryAdvisor.get();
    }

    /**
     * 设置全局记忆advisor
     */
    public void setGlobalMemoryAdvisor(Advisor advisor) {
        Advisor old = globalMemoryAdvisor.getAndSet(advisor);
        if (old != advisor) {
            log.info("更新全局记忆advisor");
        }
    }

    /**
     * 获取角色advisor（懒加载）
     */
    public List<Advisor> getCharacterAdvisor(String characterId) {
        return characterAdvisors.get(characterId);
    }

    /**
     * 设置角色advisor
     */
    public void setCharacterAdvisor(String characterId, List<Advisor> advisors) {
        characterAdvisors.put(characterId, advisors);
    }

    /**
     * 获取系统级advisor
     */
    public List<Advisor> getSystemAdvisors(String behaviorType) {
        return systemAdvisors.get(behaviorType);
    }

    /**
     * 设置系统级advisor
     */
    public void setSystemAdvisors(String behaviorType, List<Advisor> advisors) {
        systemAdvisors.put(behaviorType, advisors);
        log.info("更新系统级advisor缓存: behaviorType={}", behaviorType);
    }

    /**
     * 移除系统级advisor
     */
    public void removeSystemAdvisors(String behaviorType) {
        systemAdvisors.remove(behaviorType);
        log.info("移除系统级advisor缓存: behaviorType={}", behaviorType);
    }

    /**
     * 检查系统级advisor是否存在
     */
    public boolean hasSystemAdvisors(String behaviorType) {
        return systemAdvisors.containsKey(behaviorType);
    }

    /**
     * 移除角色advisor
     */
    public void removeCharacterAdvisor(String characterId) {
        characterAdvisors.remove(characterId);
    }

    /**
     * 检查角色advisor是否存在
     */
    public boolean hasCharacterAdvisor(String characterId) {
        return characterAdvisors.containsKey(characterId);
    }

    /**
     * 清除所有系统级advisor缓存
     */
    public void clearAllSystemAdvisors() {
        int count = systemAdvisors.size();
        systemAdvisors.clear();
        log.info("清除所有系统级advisor缓存，共{}个", count);
    }

    /**
     * 清除所有角色advisor缓存
     */
    public void clearAllCharacterAdvisors() {
        int count = characterAdvisors.size();
        characterAdvisors.clear();
        log.info("清除所有角色advisor缓存，共{}个", count);
    }

    /**
     * 清除所有advisor缓存
     */
    public void clearAllAdvisors() {
        globalMemoryAdvisor.set(null);
        clearAllSystemAdvisors();
        clearAllCharacterAdvisors();
        log.info("清除所有advisor缓存");
    }
}