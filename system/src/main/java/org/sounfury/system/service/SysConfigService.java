package org.sounfury.system.service;

import org.sounfury.system.dto.rep.SysConfigRep;

import java.util.List;
import java.util.Map;

public interface SysConfigService {

    /**
     * 获取全部系统配置
     */
    List<SysConfigRep> getSysConfigList();

    /**
     * 查询单个系统配置
     */
    SysConfigRep getSysConfigById(Integer id);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String getConfigByKey(String configKey);

    /**
     * 获取全部的配置键值对
     */
    Map<String,String> getAllConfig();

    /**
     * 根据配置key修改配置
     */
    String updateSysConfigByConfigKey(String ByConfigKey, String value);

    /**
     * 检测配置是否存在
     */
    boolean checkConfigKey(String configKey);


    /**
     * 加载参数缓存数据
     */
    public void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    public void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    public void resetConfigCache();





}
