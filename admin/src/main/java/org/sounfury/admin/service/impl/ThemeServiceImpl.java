package org.sounfury.admin.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jooq.JSON;
import org.sounfury.admin.dto.rep.ThemeListRep;
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

import java.util.List;

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
    public List<ThemeListRep> list() {
        return themeSettingsRepository.findAll()
                .stream()
                .map(themeSettings -> {
                    ThemeListRep themeListRep = new ThemeListRep();
                    themeListRep.setThemeId(themeSettings.getThemeId());
                    themeListRep.setThemeKey(themeSettings.getThemeKey());
                    themeListRep.setThemeName(themeSettings.getThemeName());
                    themeListRep.setSettings(JsonUtils.parseObject(themeSettings.getSettings()
                            .toString(), ThemeSetting.class));
                    themeListRep.setDescription(themeSettings.getDescription());
                    themeListRep.setMode(themeSettings.getMode());
                    return themeListRep;
                })
                .toList();
    }

    @Override
    @Cacheable(value = CacheNames.SYS_THEME, key = "#key")
    public ThemeSetting getByKey(String key) {
        ThemeSettings themeSettings = themeSettingsRepository.fetchOneByThemeKey(key);
        if (themeSettings == null) {
            return null;
        }
        JSON settings = themeSettings.getSettings();

        return JsonUtils.parseObject(settings.toString(), ThemeSetting.class);
    }

    @Override
    public ThemeSetting getNowTheme() {
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
        themeSettingsRepository.findAll()
                .forEach(themeSettings ->
                {
                    String jsonSettings = themeSettings.getSettings()
                            .toString();
                    CacheUtils.put(CacheNames.SYS_THEME, themeSettings.getThemeKey(),
                            JsonUtils.parseObject(jsonSettings, ThemeSetting.class));
                });
        SysConfig sysConfig = sysConfigRepository.fetchOneByConfigKey(SYS_CONFIG_THEME_KEY);
        CacheUtils.put(CacheNames.SYS_THEME, ENABLED_THEME, sysConfig.getConfigValue());
    }
}
