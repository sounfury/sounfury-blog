package org.sounfury.admin.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jooq.JSON;
import org.sounfury.admin.dto.rep.ThemeRep;
import org.sounfury.admin.dto.req.ThemeReq;
import org.sounfury.admin.model.ThemeSetting;
import org.sounfury.admin.repository.ThemeSettingsRepository;
import org.sounfury.admin.service.ThemeService;
import org.sounfury.core.constant.CacheNames;
import org.sounfury.core.utils.JsonUtils;
import org.sounfury.blog.jooq.tables.pojos.SysConfig;
import org.sounfury.blog.jooq.tables.pojos.ThemeSettings;
import org.sounfury.system.repository.SysConfigRepository;
import org.sounfury.utils.CacheUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.core.constant.DefaultConfigKey.SYS_CONFIG_THEME_KEY;
import static org.sounfury.core.constant.ThemeConstant.DEFAULT_THEME;
import static org.sounfury.core.constant.ThemeConstant.ENABLED_THEME;

@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {
    private final ThemeSettingsRepository themeSettingsRepository;
    private final ApplicationContext applicationContext;

    @Override
    public List<ThemeRep> list() {
        Map<Object, Object> cacheData = CacheUtils.getAll(CacheNames.SYS_THEME);

        if (cacheData.isEmpty()) {
            return Collections.emptyList();
        }

        return cacheData.values().stream()
                .map(value -> {
                    if (value instanceof ThemeRep) {
                        return (ThemeRep) value;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    @Override
    @Cacheable(value = CacheNames.SYS_THEME, key = "#key")
    public ThemeRep getByKey(String key) {
        // 从数据库中获取对应的主题设置
        ThemeSettings themeSettings = themeSettingsRepository.fetchOneByThemeKey(key);
        if (themeSettings == null) {
            return null; // 如果未找到，返回 null
        }
        // 将 ThemeSettings 转换为 ThemeRep
        ThemeRep themeRep = new ThemeRep();
        themeRep.setThemeKey(themeSettings.getThemeKey());
        themeRep.setThemeName(themeSettings.getThemeName());
        themeRep.setSettings(JsonUtils.parseObject(themeSettings.getSettings().toString(), ThemeSetting.class));
        themeRep.setDescription(themeSettings.getDescription());
        themeRep.setMode(themeSettings.getMode());
        themeRep.setEnableStatus(themeSettings.getEnableStatus());
        return themeRep; // 返回转换后的 ThemeRep 对象
    }
    @Override
    public ThemeRep getNowTheme() {
        String themeKey = CacheUtils.get(CacheNames.SYS_THEME, ENABLED_THEME);
        ThemeService proxy = applicationContext.getBean(ThemeService.class);
        return proxy.getByKey(themeKey);
    }

    @Override
    @Transactional
    @CachePut(value = CacheNames.SYS_THEME, key = "#themeUpdateReq.themeKey")
    public ThemeRep update(ThemeReq themeUpdateReq) {
        ThemeSettings themeSettings = new ThemeSettings();
        themeSettings.setThemeKey(themeUpdateReq.getThemeKey());
        themeSettings.setThemeName(themeUpdateReq.getThemeName());
        themeSettings.setDescription(themeUpdateReq.getDescription());
        themeSettings.setMode(themeUpdateReq.getMode());
        themeSettings.setSettings(JSON.valueOf(JsonUtils.toJsonString(themeUpdateReq.getSettings())));
        themeSettings.setEnableStatus(themeUpdateReq.getEnableStatus());
        if(Objects.equals(themeUpdateReq.getEnableStatus(), STATUS_ENABLE)){
            //覆盖之前的启用主题
            CacheUtils.put(CacheNames.SYS_THEME, ENABLED_THEME, themeUpdateReq.getThemeKey());
        }

        themeSettingsRepository.updateTheme(themeSettings);
        ThemeRep themeRep = ThemeRep.builder()
                .themeKey(themeSettings.getThemeKey())
                .themeName(themeSettings.getThemeName())
                .settings(JsonUtils.parseObject(themeSettings.getSettings().toString(), ThemeSetting.class))
                .description(themeSettings.getDescription())
                .mode(themeSettings.getMode())
                .enableStatus(themeSettings.getEnableStatus())
                .build();
        return themeRep;
    }

    @Override
    @Transactional
    public void delete(String key) {
        themeSettingsRepository.deleteByThemeKey(key);
        CacheUtils.evict(CacheNames.SYS_THEME, key);
    }

    @Override
    public boolean checkDefaultTheme(String key) {
        return key.contains(DEFAULT_THEME);
    }

    @Override
    @Transactional
    @CachePut(value = CacheNames.SYS_THEME, key = "#themeAddReq.themeKey")
    public void add(ThemeReq themeAddReq) {
        ThemeSettings themeSettings = new ThemeSettings();
        themeSettings.setThemeKey(themeAddReq.getThemeKey());
        themeSettings.setThemeName(themeAddReq.getThemeName());
        themeSettings.setDescription(themeAddReq.getDescription());
        themeSettings.setMode(themeAddReq.getMode());
        themeSettings.setSettings(JSON.valueOf(JsonUtils.toJsonString(themeAddReq.getSettings())));
        themeSettings.setEnableStatus(themeAddReq.getEnableStatus());
        if(Objects.equals(themeAddReq.getEnableStatus(), STATUS_ENABLE)){
            //覆盖之前的启用主题
            CacheUtils.put(CacheNames.SYS_THEME, ENABLED_THEME, themeAddReq.getThemeKey());
        }
        themeSettingsRepository.insertTheme(themeSettings);
    }

    @Override
    public boolean checkKeyExist(String key) {
        return CacheUtils.get(CacheNames.SYS_THEME, key) != null;
    }

    @Override
    @PostConstruct
    public void initCache() {
        //目前启用的主题,因为status是1的只有一个
        themeSettingsRepository.fetchByEnableStatus(STATUS_ENABLE).forEach(themeSettings -> {
            CacheUtils.put(CacheNames.SYS_THEME, ENABLED_THEME, themeSettings.getThemeKey());
        });
        // 预热所有主题到缓存
        List<ThemeSettings> allThemes = themeSettingsRepository.findAll();
        allThemes.forEach(themeSettings -> {
            // 将 ThemeSettings 转换为 ThemeRep
            ThemeRep themeRep = new ThemeRep();
            themeRep.setThemeKey(themeSettings.getThemeKey());
            themeRep.setThemeName(themeSettings.getThemeName());
            themeRep.setSettings(JsonUtils.parseObject(themeSettings.getSettings().toString(), ThemeSetting.class));
            themeRep.setDescription(themeSettings.getDescription());
            themeRep.setMode(themeSettings.getMode());
            themeRep.setEnableStatus(themeSettings.getEnableStatus());

            // 将主题信息写入缓存，key 为 themeKey，value 为 ThemeRep
            CacheUtils.put(CacheNames.SYS_THEME, themeSettings.getThemeKey(), themeRep);
        });
    }
}
