package org.sounfury.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.core.constant.CacheNames;
import org.sounfury.core.convention.exception.ServiceException;
import org.sounfury.core.utils.JsonUtils;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.core.utils.StringUtils;
import org.sounfury.jooq.tables.pojos.SysOssConfig;
import org.sounfury.oss.constant.OssConstant;
import org.sounfury.system.dto.rep.SysOssConfigRep;
import org.sounfury.system.dto.req.SysOssConfigReq;
import org.sounfury.system.repository.SysOssConfigRepository;
import org.sounfury.system.service.SysOssConfigService;
import org.sounfury.utils.CacheUtils;
import org.sounfury.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.sounfury.core.constant.Constants.STATUS_DISABLE;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;

@Service
@RequiredArgsConstructor
public class SysOssConfigServiceImpl implements SysOssConfigService {
    private final SysOssConfigRepository sysOssConfigRepository;

    @Override
    public void init() {
        //查询数据库并放到缓存中
        List<SysOssConfig> sysOssConfigs = sysOssConfigRepository.fetchList();
        for (SysOssConfig sysOssConfig : sysOssConfigs) {
            if (STATUS_ENABLE.equals(sysOssConfig.getEnableStatus())) {
                RedisUtils.setCacheObject(OssConstant.ENABLED_CONFIG_KEY, sysOssConfig.getConfigKey());
            }
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey(),
                    JsonUtils.toJsonString(sysOssConfig));
        }
    }

    @Override
    public SysOssConfigRep queryById(Long ossConfigId) {
        SysOssConfig sysOssConfig = sysOssConfigRepository.fetchOneByOssConfigId(ossConfigId);
        return BeanUtil.copyProperties(sysOssConfig, SysOssConfigRep.class);
    }

    @Override
    public List<SysOssConfigRep> queryList() {
        List<SysOssConfig> sysOssConfigs = sysOssConfigRepository.fetchList();
        return sysOssConfigs
                .stream()
                .map(sysOssConfig -> {
                    SysOssConfigRep rep = new SysOssConfigRep();
                    BeanUtil.copyProperties(sysOssConfig, rep);
                    return rep;
                })
                .toList();

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insert(SysOssConfigReq bo) {
        SysOssConfig convert = MapstructUtils.convert(bo, SysOssConfig.class);
        validEntityBeforeSave(convert);
        sysOssConfigRepository.insert(convert);
        SysOssConfig sysOssConfig = sysOssConfigRepository.fetchOneByOssConfigId(bo.getOssConfigId());
        //为空返回false
        if (sysOssConfig == null) {
            return false;
        }
        //放到缓存中
        CacheUtils.put(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey(), JsonUtils.toJsonString(sysOssConfig));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SysOssConfigReq bo) {
        SysOssConfig convert = MapstructUtils.convert(bo, SysOssConfig.class);
        validEntityBeforeSave(convert);
        sysOssConfigRepository.update(convert);
        SysOssConfig sysOssConfig = sysOssConfigRepository.fetchOneByOssConfigId(bo.getOssConfigId());
        //为空返回false
        if (sysOssConfig == null) {
            return false;
        }
        //放到缓存中
        CacheUtils.put(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey(), JsonUtils.toJsonString(sysOssConfig));
        return true;
    }

    private void validEntityBeforeSave(SysOssConfig entity) {
        if (entity == null) {
            throw new ServiceException("操作配置失败, 配置信息不能为空!");
        }
        if (StringUtils.isNotEmpty(entity.getConfigKey()) && !checkConfigKeyUnique(entity)) {
            throw new ServiceException("操作配置'" + entity.getConfigKey() + "'失败, 配置key已存在!");
        }
    }

    private boolean checkConfigKeyUnique(SysOssConfig sysOssConfig) {
        SysOssConfig ossConfig = sysOssConfigRepository.fetchOneByConfigKey(sysOssConfig.getConfigKey());
        return ObjectUtil.isNull(ossConfig) || ossConfig.getOssConfigId()
                .equals(sysOssConfig.getOssConfigId());
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        List<SysOssConfig> list = CollUtil.newArrayList();
        for (Long configId : ids) {
            SysOssConfig config = sysOssConfigRepository.fetchOneByOssConfigId(configId);
            list.add(config);
        }
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(sysOssConfig -> {
                CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey());
                RedisUtils.deleteObject(OssConstant.ENABLED_CONFIG_KEY);
            });
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOssConfigStatus(SysOssConfigReq bo) {
        SysOssConfig sysOssConfig = MapstructUtils.convert(bo, SysOssConfig.class);

        //转换启用状态
        if (bo.getEnableStatus()
                .equals(STATUS_ENABLE)) {
            //禁用其他配置
            sysOssConfigRepository.updateAllEnableStatus(STATUS_DISABLE);
            RedisUtils.setCacheObject(OssConstant.ENABLED_CONFIG_KEY, bo.getConfigKey());
        } else {
            RedisUtils.deleteObject(OssConstant.ENABLED_CONFIG_KEY);
            sysOssConfigRepository.update(sysOssConfig);
        }

    }
}
