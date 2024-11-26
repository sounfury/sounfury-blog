package org.sounfury.oss.factory;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.core.constant.CacheNames;
import org.sounfury.core.utils.JsonUtils;
import org.sounfury.core.utils.StringUtils;
import org.sounfury.oss.constant.OssConstant;
import org.sounfury.oss.core.OssClient;
import org.sounfury.oss.exception.OssException;
import org.sounfury.oss.properties.OssProperties;
import org.sounfury.utils.CacheUtils;
import org.sounfury.utils.RedisUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ossFactory
 * 单例模式
 * @author Lion Li
 */
@Slf4j
public class OssFactory {

    private static final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();


    /**
     * 获取已启用的实例
     */
    public static OssClient instance() {
        // 获取redis 默认类型
        String configKey = RedisUtils.getCacheObject(OssConstant.ENABLED_CONFIG_KEY);
        if (StringUtils.isEmpty(configKey)) {
            throw new OssException("文件存储服务类型无法找到!");
        }
        return instance(configKey);
    }

    public static OssClient instance(String configKey) {
        String json = CacheUtils.get(CacheNames.SYS_OSS_CONFIG, configKey);
        if (json == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        OssProperties properties = JsonUtils.parseObject(json, OssProperties.class);
        // 使用租户标识避免多个租户相同key实例覆盖
        String key = configKey;

        OssClient client = CLIENT_CACHE.get(key);
        // 客户端不存在或配置不相同则重新构建
        if (client == null || !client.checkPropertiesSame(properties)) {
            client = CLIENT_CACHE.get(key);
            if (client == null || !client.checkPropertiesSame(properties)) {
                CLIENT_CACHE.put(key, new OssClient(configKey, properties));
                log.info("创建OSS实例 key => {}", configKey);
                return CLIENT_CACHE.get(key);
            }
        }
        return client;
    }

}
