package org.sounfury.redis.manager;

import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.sounfury.utils.RedisUtils;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *重写RedissonSpringCacheManager，支持多参数cacheName
 * 1.支持TTL、最大空闲时间和最大大小
 * 2，提供可选的事务感知（transactionAware）功能
 * A {@link org.springframework.cache.CacheManager} implementation
 */
@SuppressWarnings("unchecked")
public class PlusSpringCacheManager implements CacheManager {

    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean transactionAware = true;

    private Map<String, CacheConfig> configMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Cache> instanceMap = new ConcurrentHashMap<>();

    public PlusSpringCacheManager() {
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

    public void setCacheNames(Collection<String> names) {
        if (names != null) {
            for (String name : names) {
                getCache(name);
            }
            dynamic = false;
        } else {
            dynamic = true;
        }
    }

    public void setConfig(Map<String, ? extends CacheConfig> config) {
        this.configMap = (Map<String, CacheConfig>) config;
    }

    protected CacheConfig createDefaultConfig() {
        return new CacheConfig();
    }

    @Override
    public Cache getCache(String name) {
        // 支持多参数 cacheName
        String[] array = StringUtils.delimitedListToStringArray(name, "#");
        name = array[0];

        Cache cache = instanceMap.get(name);
        if (cache != null) {
            return cache;
        }

        if (!dynamic) {
            return null;
        }

        CacheConfig config = configMap.get(name);
        if (config == null) {
            config = createDefaultConfig();
            configMap.put(name, config);
        }

        if (array.length > 1) {
            config.setTTL(DurationStyle.detectAndParse(array[1]).toMillis());
        }
        if (array.length > 2) {
            config.setMaxIdleTime(DurationStyle.detectAndParse(array[2]).toMillis());
        }
        if (array.length > 3) {
            config.setMaxSize(Integer.parseInt(array[3]));
        }

        return config.getMaxIdleTime() == 0 && config.getTTL() == 0 && config.getMaxSize() == 0
                ? createMap(name, config)
                : createMapCache(name, config);
    }

    private Cache createMap(String name, CacheConfig config) {
        RMap<Object, Object> map = RedisUtils.getClient().getMap(name);

        Cache cache = new RedissonCache(map, allowNullValues);
        if (transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }

        Cache oldCache = instanceMap.putIfAbsent(name, cache);
        return oldCache != null ? oldCache : cache;
    }

    private Cache createMapCache(String name, CacheConfig config) {
        RMapCache<Object, Object> map = RedisUtils.getClient().getMapCache(name);

        Cache cache = new RedissonCache(map, config, allowNullValues);
        if (transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }

        Cache oldCache = instanceMap.putIfAbsent(name, cache);
        if (oldCache == null) {
            map.setMaxSize(config.getMaxSize());
        }
        return oldCache != null ? oldCache : cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(configMap.keySet());
    }
}
