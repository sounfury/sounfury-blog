package org.sounfury.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.constant.CacheNames;
import org.sounfury.jooq.tables.pojos.SysConfig;
import org.sounfury.system.dto.rep.SysConfigRep;
import org.sounfury.system.repository.SysConfigRepository;
import org.sounfury.system.service.SysConfigService;
import org.sounfury.utils.CacheUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sounfury.core.constant.DefaultConfigKey.SYS_CONFIG_THEME_KEY;
import static org.sounfury.core.constant.ThemeConstant.ENABLED_THEME;

@RequiredArgsConstructor
@Service

public class SysConfigServiceImpl implements SysConfigService {
    private final SysConfigRepository sysConfigRepository;

    @PostConstruct
    private void init() {
        loadingConfigCache();
    }


    @Override
    public List<SysConfigRep> getSysConfigList() {
        List<SysConfig> sysConfigs = sysConfigRepository.findAll();
        return BeanUtil.copyToList(sysConfigs, SysConfigRep.class);
    }

    @Override
    public SysConfigRep getSysConfigById(Integer id) {
        SysConfig sysConfig = sysConfigRepository.fetchOneByConfigId(id);
        SysConfigRep sysConfigRep = new SysConfigRep();
        BeanUtil.copyProperties(sysConfig, sysConfigRep);
        return sysConfigRep;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
    public String getConfigByKey(String configKey) {
        return sysConfigRepository.fetchOneByConfigKey(configKey)
                .getConfigValue();
    }

    @Override
    public Map<String, String> getAllConfig() {
        Map<Object, Object> all = CacheUtils.getAll(CacheNames.SYS_CONFIG);
        Map<String, String> result = new HashMap<>();
        all.forEach((key, value) -> {
            if (key instanceof String && value instanceof String) {
                result.put((String) key, (String) value);
            }
        });
        return result;
    }


    @Override
    @Transactional
    @CachePut(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
    public String updateSysConfigByConfigKey(String configKey, String value) {
        sysConfigRepository.updateConfigValueByConfigKey(configKey, value);
        if (configKey.equals(SYS_CONFIG_THEME_KEY)) {
            CacheUtils.evict(CacheNames.SYS_THEME , ENABLED_THEME);
            //更新后重设
            CacheUtils.put(CacheNames.SYS_THEME , ENABLED_THEME, configKey);
        }

        return value;
    }

    @Override
    public boolean checkConfigKey(String configKey) {
        if (CacheUtils.get(CacheNames.SYS_CONFIG, configKey) != null) {
            return true;
        }

        if (sysConfigRepository.fetchOneByConfigKey(configKey) != null) {
            CacheUtils.put(CacheNames.SYS_CONFIG, configKey, configKey);
            return true;
        }

        return false;
    }


    @Override
    public void loadingConfigCache() {
        List<SysConfig> sysConfigs = sysConfigRepository.findAll();
        sysConfigs.forEach(sysConfig -> {
            CacheUtils.put(CacheNames.SYS_CONFIG, sysConfig.getConfigKey(), sysConfig.getConfigValue());
        });
    }

    @Override
    public void clearConfigCache() {
        CacheUtils.clear(CacheNames.SYS_CONFIG);

    }

    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }
}
