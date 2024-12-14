package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.system.dto.rep.SysConfigRep;
import org.sounfury.system.dto.req.SysConfigReq;
import org.sounfury.system.dto.req.SysOssConfigReq;
import org.sounfury.system.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class ConfigController {
    private final SysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @GetMapping("/list")
    public Result<List<SysConfigRep>> list() {
        return Results.success(configService.getSysConfigList());
    }

    /**
     * 获取单个参数配置
     */
    @GetMapping("/{id}")
    public Result<SysConfigRep> get(@PathVariable Integer id) {
        return Results.success(configService.getSysConfigById(id));
    }

    /**
     * 根据key获取参数配置
     */
    @SaIgnore
    @GetMapping("/configKey/{configKey}")
    public Result<String> getByKey(@PathVariable String configKey) {
        return Results.success(configService.getConfigByKey(configKey));
    }


    /**
     * 修改参数配置
     */
    @PutMapping()
    public Result<Void> update(@RequestParam String key, String value) {
        if (!configService.checkConfigKey(key)) {
            return Results.failure(new ClientException("配置键不存在"));
        }
        configService.updateSysConfigByConfigKey(key, value);
        return Results.success();
    }

    /**
     * 批量修改参数配置
     */
    @PutMapping("/batch")
    public Result<Void> updateBatch(@RequestBody List<SysConfigReq>  configMap) {
        configService.updateSysConfigBatch(configMap);
        return Results.success();
    }



    /**
     * 获取全部的配置键值对
     */
    @SaIgnore
    @GetMapping("/all")
    public Result<Map<String,String>> all() {
        return Results.success(configService.getAllConfig());
    }

    /**
     * 清空参数缓存数据
     */
    @DeleteMapping("/cache/clear")
    public Result<Void> clear() {
        configService.clearConfigCache();
        return Results.success();
    }

    /**
     * 重置缓存
     */
    @PostMapping("/cache/reset")
    public Result<Void> reset() {
        configService.resetConfigCache();
        return Results.success();
    }

}
