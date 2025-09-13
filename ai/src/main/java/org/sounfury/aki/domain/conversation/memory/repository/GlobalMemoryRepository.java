package org.sounfury.aki.domain.conversation.memory.repository;

import org.sounfury.aki.domain.conversation.memory.GlobalMemory;

import java.util.List;
import java.util.Optional;

/**
 * 全局记忆仓储接口
 * 定义全局记忆的持久化操作
 */
public interface GlobalMemoryRepository {

    /**
     * 缓存名称常量
     */
    String CACHE_NAME = "global_memory";

    /**
     * 根据ID查找全局记忆
     * @param id 记忆ID
     * @return 全局记忆实体，如果不存在则返回空
     */
    Optional<GlobalMemory> findGMemoryById(Long id);

    /**
     * 查找所有全局记忆
     * @return 所有全局记忆列表
     */
    List<GlobalMemory> findGMemoryAll();

    /**
     * 按时间戳倒序查找所有全局记忆
     * @return 按时间倒序的全局记忆列表
     */
    List<GlobalMemory> findAllOrderByTimestampDesc();

    /**
     * 根据内容关键词查找全局记忆
     * @param keyword 关键词
     * @return 包含关键词的全局记忆列表
     */
    List<GlobalMemory> findByContentContaining(String keyword);

    /**
     * 查找最近的N条全局记忆
     * @param limit 数量限制
     * @return 最近的全局记忆列表
     */
    List<GlobalMemory> findRecentMemories(int limit);

    /**
     * 保存全局记忆
     * @param globalMemory 全局记忆实体
     * @return 保存后的全局记忆实体
     */
    GlobalMemory save(GlobalMemory globalMemory);

    /**
     * 删除全局记忆
     * @param id 记忆ID
     */
    void deleteById(Long id);

    /**
     * 检查全局记忆是否存在
     * @param id 记忆ID
     * @return 如果存在返回true，否则返回false
     */
    boolean existsById(Long id);

    /**
     * 获取全局记忆总数
     * @return 记忆总数
     */
    long count();

    /**
     * 删除所有全局记忆
     */
    void deleteAll();
}
