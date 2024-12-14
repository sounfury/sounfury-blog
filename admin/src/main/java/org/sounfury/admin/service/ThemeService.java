package org.sounfury.admin.service;

import org.sounfury.admin.dto.rep.ThemeRep;
import org.sounfury.admin.dto.req.ThemeReq;
import org.sounfury.admin.model.ThemeSetting;

import java.util.List;

public interface ThemeService {

    /**
     * 获取主题列表
     * @return
     */
    List<ThemeRep> list();

    /**
     * 根据key获取主题设置
     *
     * @param key
     * @return
     */
    ThemeRep getByKey(String key);


    /**
     * 获取目前启用的主题
     * @return
     */
    ThemeRep getNowTheme();

    /**
     * 修改主题设置
     *
     * @param key
     * @param setting
     * @return
     */
    ThemeRep update(ThemeReq themeUpdateReq);


    /**
     * 删除主题设置
     * @param key
     */
    void delete(String key);

    /**
     * 检查是否是默认主题
     * @param key
     * @return
     */
    boolean checkDefaultTheme(String key);

    /**
     * 添加主题
     */
    void add(ThemeReq themeAddReq);

    /**
     * 检查是否存在
     * @param key
     * @return
     */
    boolean checkKeyExist(String key);

    /**
     * init预热缓存
     */
    void initCache();

}
