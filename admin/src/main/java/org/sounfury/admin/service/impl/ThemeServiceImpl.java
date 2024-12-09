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
import org.sounfury.jooq.tables.pojos.SysConfig;
import org.sounfury.jooq.tables.pojos.ThemeSettings;
import org.sounfury.system.repository.SysConfigRepository;
import org.sounfury.utils.CacheUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.sounfury.core.constant.DefaultConfigKey.SYS_CONFIG_THEME_KEY;
import static org.sounfury.core.constant.ThemeConstant.DEFAULT_THEME;
import static org.sounfury.core.constant.ThemeConstant.ENABLED_THEME;

@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {
    private final ThemeSettingsRepository themeSettingsRepository;
    private final SysConfigRepository sysConfigRepository;
    private final ApplicationContext applicationContext;

    @Override
    public List<ThemeRep> list() {
        // 获取缓存中的数据
        Map<Object, Object> cacheData = CacheUtils.getAll(CacheNames.SYS_THEME);

        // 如果缓存为空，则返回空列表
        if (cacheData.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换类型为 Map<String, ThemeRep>
        Map<String, ThemeRep> convertedCacheData = cacheData.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> (String) entry.getKey(),   // 转换 key 类型
                        entry -> (ThemeRep) entry.getValue() // 转换 value 类型
                ));

        // 返回所有 ThemeRep 对象的列表
        return new ArrayList<>(convertedCacheData.values());
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
        themeRep.setThemeId(themeSettings.getThemeId());
        themeRep.setThemeKey(themeSettings.getThemeKey());
        themeRep.setThemeName(themeSettings.getThemeName());
        themeRep.setSettings(JsonUtils.parseObject(themeSettings.getSettings().toString(), ThemeSetting.class));
        themeRep.setDescription(themeSettings.getDescription());
        themeRep.setMode(themeSettings.getMode());
        return themeRep; // 返回转换后的 ThemeRep 对象
    }
    @Override
    public ThemeRep getNowTheme() {
        String themeKey = CacheUtils.get(CacheNames.SYS_THEME, ENABLED_THEME);
        ThemeService proxy = applicationContext.getBean(ThemeService.class);
        return proxy.getByKey(themeKey);
    }

    @Override
    @CachePut(value = CacheNames.SYS_THEME, key = "#themeUpdateReq.themeKey")
    public ThemeSetting update(ThemeReq themeUpdateReq) {
        ThemeSettings themeSettings = new ThemeSettings();
        themeSettings.setThemeKey(themeUpdateReq.getThemeKey());
        themeSettings.setThemeName(themeUpdateReq.getThemeName());
        themeSettings.setDescription(themeUpdateReq.getDescription());
        themeSettings.setMode(themeUpdateReq.getMode());
        themeSettings.setSettings(JSON.valueOf(JsonUtils.toJsonString(themeUpdateReq.getSettings())));
        themeSettingsRepository.updateTheme(themeSettings);
        return themeUpdateReq.getSettings();
    }

    @Override
    public void delete(String key) {
        themeSettingsRepository.deleteByThemeKey(key);
        CacheUtils.evict(CacheNames.SYS_THEME, key);
    }

    @Override
    public boolean checkDefaultTheme(String key) {
        return key.contains(DEFAULT_THEME);
    }

    @Override
    @CachePut(value = CacheNames.SYS_THEME, key = "#themeAddReq.themeKey")
    public void add(ThemeReq themeAddReq) {
        ThemeSettings themeSettings = new ThemeSettings();
        themeSettings.setThemeKey(themeAddReq.getThemeKey());
        themeSettings.setThemeName(themeAddReq.getThemeName());
        themeSettings.setDescription(themeAddReq.getDescription());
        themeSettings.setMode(themeAddReq.getMode());
        themeSettings.setSettings(JSON.valueOf(JsonUtils.toJsonString(themeAddReq.getSettings())));
        themeSettingsRepository.insertTheme(themeSettings);
    }

    @Override
    public boolean checkKeyExist(String key) {
        return CacheUtils.get(CacheNames.SYS_THEME, key) != null;
    }

    @Override
    @PostConstruct
    public void initCache() {
        //目前启用的主题
        SysConfig sysConfig = sysConfigRepository.fetchOneByConfigKey(SYS_CONFIG_THEME_KEY);
        CacheUtils.put(CacheNames.SYS_THEME, ENABLED_THEME, sysConfig.getConfigValue());

        // 预热所有主题到缓存
        List<ThemeSettings> allThemes = themeSettingsRepository.findAll();
        allThemes.forEach(themeSettings -> {
            // 将 ThemeSettings 转换为 ThemeRep
            ThemeRep themeRep = new ThemeRep();
            themeRep.setThemeId(themeSettings.getThemeId());
            themeRep.setThemeKey(themeSettings.getThemeKey());
            themeRep.setThemeName(themeSettings.getThemeName());
            themeRep.setSettings(JsonUtils.parseObject(themeSettings.getSettings().toString(), ThemeSetting.class));
            themeRep.setDescription(themeSettings.getDescription());
            themeRep.setMode(themeSettings.getMode());

            // 将主题信息写入缓存，key 为 themeKey，value 为 ThemeRep
            CacheUtils.put(CacheNames.SYS_THEME, themeSettings.getThemeKey(), themeRep);
        });
    }
}
