package org.sounfury.aki.contracts.service;

import org.sounfury.aki.contracts.spec.PromptSpec;

/**
 * ChatClient缓存服务接口
 * 负责ChatClient的缓存管理
 */
public interface AdvisorFactoryPort {
    //清理所有缓存
    void clearAll();

    //移除指定角色的缓存
    void removeForCharacter(String characterId);

    //检查是否存在指定角色的缓存
    boolean containsCharacter(String characterId);

    //创建Advisor并放入缓存
    void createAndCacheAdvisors(PromptSpec promptSpec, String characterId);


}
