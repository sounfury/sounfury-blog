package org.sounfury.aki.infrastructure.persistence;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.domain.conversation.memory.repository.GlobalMemoryRepository;
import org.sounfury.aki.jooq.tables.daos.GlobalMemoryDao;
import org.sounfury.aki.jooq.tables.pojos.GlobalMemoryPojo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.sounfury.aki.jooq.tables.GlobalMemory.GLOBAL_MEMORY;

/**
 * JOOQ实现的全局记忆仓储
 */
@Slf4j
@Repository
public class GlobalMemoryRepositoryImpl extends GlobalMemoryDao implements GlobalMemoryRepository {

    public GlobalMemoryRepositoryImpl(Configuration configuration) {
        super(configuration);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Optional<GlobalMemory> findGMemoryById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        try {
            GlobalMemoryPojo pojo = fetchOptionalById(id).orElse(null);
            return Optional.ofNullable(pojo).map(this::toDomain);
        } catch (Exception e) {
            log.error("根据ID查找全局记忆失败: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "'all'")
    public List<GlobalMemory> findGMemoryAll() {
        log.info("Fetching all global memories from database");
        try {
            List<GlobalMemoryPojo> pojos = ctx()
                    .selectFrom(GLOBAL_MEMORY)
                    .fetchInto(GlobalMemoryPojo.class);

            return pojos.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找所有全局记忆失败", e);
            return List.of();
        }
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "'all_ordered'")
    public List<GlobalMemory> findAllOrderByTimestampDesc() {
        try {
            List<GlobalMemoryPojo> pojos = ctx()
                    .selectFrom(GLOBAL_MEMORY)
                    .orderBy(GLOBAL_MEMORY.TIMESTAMP.desc())
                    .fetchInto(GlobalMemoryPojo.class);

            return pojos.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按时间戳倒序查找全局记忆失败", e);
            return List.of();
        }
    }

    @Override
    public List<GlobalMemory> findByContentContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        try {
            List<GlobalMemoryPojo> pojos = ctx()
                    .selectFrom(GLOBAL_MEMORY)
                    .where(GLOBAL_MEMORY.CONTENT.contains(keyword.trim()))
                    .orderBy(GLOBAL_MEMORY.TIMESTAMP.desc())
                    .fetchInto(GlobalMemoryPojo.class);

            return pojos.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据关键词查找全局记忆失败: {}", keyword, e);
            return List.of();
        }
    }

    @Override
    public List<GlobalMemory> findRecentMemories(int limit) {
        if (limit <= 0) {
            return List.of();
        }

        try {
            List<GlobalMemoryPojo> pojos = ctx()
                    .selectFrom(GLOBAL_MEMORY)
                    .orderBy(GLOBAL_MEMORY.TIMESTAMP.desc())
                    .limit(limit)
                    .fetchInto(GlobalMemoryPojo.class);

            return pojos.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找最近全局记忆失败, limit: {}", limit, e);
            return List.of();
        }
    }

    @Override
    @Caching(
        put = @CachePut(cacheNames = CACHE_NAME, key = "#result.id"),
        evict = {
            @CacheEvict(cacheNames = CACHE_NAME, key = "'all'"),
            @CacheEvict(cacheNames = CACHE_NAME, key = "'all_ordered'")
        }
    )
    public GlobalMemory save(GlobalMemory globalMemory) {

        if (globalMemory == null || !globalMemory.isValid()) {
            throw new IllegalArgumentException("无效的全局记忆对象");
        }

        try {
            GlobalMemoryPojo pojo = toPojo(globalMemory);
            log.info("Saved GlobalMemory: {}", pojo);
            if (globalMemory.getId() == null) {
                // 新增
                pojo.setCreateTime(LocalDateTime.now());
                insert(pojo);
            } else {
                // 更新
                update(pojo);
            }

            log.debug("全局记忆保存成功: {}", globalMemory.getId());
            log.info("Saved GlobalMemory: {}", pojo);
            return toDomain(pojo);
        } catch (Exception e) {
            log.error("保存全局记忆失败: {}", globalMemory, e);
            throw new RuntimeException("保存全局记忆失败", e);
        }
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = CACHE_NAME, key = "#id"),
        @CacheEvict(cacheNames = CACHE_NAME, key = "'all'"),
        @CacheEvict(cacheNames = CACHE_NAME, key = "'all_ordered'")
    })
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }

        try {
            super.deleteById(id);
            log.debug("删除全局记忆成功: {}", id);
        } catch (Exception e) {
            log.error("删除全局记忆失败: {}", id, e);
            throw new RuntimeException("删除全局记忆失败", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }

        try {
            return super.existsById(id);
        } catch (Exception e) {
            log.error("检查全局记忆是否存在失败: {}", id, e);
            return false;
        }
    }

    @Override
    public long count() {
        try {
            return ctx().selectCount().from(GLOBAL_MEMORY).fetchOne(0, long.class);
        } catch (Exception e) {
            log.error("获取全局记忆总数失败", e);
            return 0L;
        }
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public void deleteAll() {
        try {
            ctx().deleteFrom(GLOBAL_MEMORY).execute();
            log.debug("删除所有全局记忆成功");
        } catch (Exception e) {
            log.error("删除所有全局记忆失败", e);
            throw new RuntimeException("删除所有全局记忆失败", e);
        }
    }

    /**
     * 领域对象转换为POJO
     */
    private GlobalMemoryPojo toPojo(GlobalMemory domain) {
        if (domain == null) {
            return null;
        }

        GlobalMemoryPojo pojo = new GlobalMemoryPojo();
        pojo.setId(domain.getId());
        pojo.setContent(domain.getContent());
        pojo.setTimestamp(domain.getTimestamp());
        return pojo;
    }

    /**
     * POJO转换为领域对象
     */
    private GlobalMemory toDomain(GlobalMemoryPojo pojo) {
        if (pojo == null) {
            return null;
        }

        return new GlobalMemory(pojo.getId(), pojo.getContent(), pojo.getTimestamp());
    }
}
