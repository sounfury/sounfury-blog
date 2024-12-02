package org.sounfury.admin.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.ThemeSettingsDao;
import org.sounfury.jooq.tables.pojos.ThemeSettings;
import org.springframework.stereotype.Repository;

import static org.sounfury.core.constant.Constants.DEL_FLAG;
import static org.sounfury.jooq.tables.ThemeSettings.THEME_SETTINGS;

@Repository
public class ThemeSettingsRepository extends ThemeSettingsDao {

  public ThemeSettingsRepository(Configuration configuration) {
    super(configuration);
  }


  public void deleteByThemeKey(String key) {
    //del_flag设置为DEL_FLAG
    ctx().update(THEME_SETTINGS)
        .set(THEME_SETTINGS.DEL_FLAG, DEL_FLAG)
        .where(THEME_SETTINGS.THEME_KEY.eq(key))
        .execute();
  }

    public void updateTheme(ThemeSettings themeSettings) {
        ctx().update(THEME_SETTINGS)
                .set(THEME_SETTINGS.THEME_NAME, themeSettings.getThemeName())
                .set(THEME_SETTINGS.DESCRIPTION, themeSettings.getDescription())
                .set(THEME_SETTINGS.MODE, themeSettings.getMode())
                .set(THEME_SETTINGS.SETTINGS, themeSettings.getSettings())
                .where(THEME_SETTINGS.THEME_KEY.eq(themeSettings.getThemeKey()))
                .execute();
    }

  public void insertTheme(ThemeSettings themeSettings) {
    ctx().insertInto(THEME_SETTINGS)
        .set(THEME_SETTINGS.THEME_KEY, themeSettings.getThemeKey())
        .set(THEME_SETTINGS.THEME_NAME, themeSettings.getThemeName())
        .set(THEME_SETTINGS.DESCRIPTION, themeSettings.getDescription())
        .set(THEME_SETTINGS.MODE, themeSettings.getMode())
        .set(THEME_SETTINGS.SETTINGS, themeSettings.getSettings())
        .execute();
  }
}
