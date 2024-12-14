package org.sounfury.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.rep.ThemeRep;
import org.sounfury.admin.dto.req.ThemeReq;
import org.sounfury.admin.model.ThemeSetting;
import org.sounfury.admin.service.ThemeService;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/theme")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    /**
     * 获取主题列表
     */
    @SaIgnore
    @GetMapping("/list")
    public Result<List<ThemeRep>> list() {
        return Results.success(themeService.list());
    }

    /**
     * 根据key获取主题设置
     */
    @SaIgnore
    @GetMapping("/{key}")
    public Result<ThemeRep> getByKey(@PathVariable String key) {
        return Results.success(themeService.getByKey(key));
    }


    /**
     * 获取目前启用的主题
     */
    @SaIgnore
    @GetMapping()
    public Result<ThemeRep> getNowTheme() {
        return Results.success(themeService.getNowTheme());
    }


    /**
     * 修改主题设置
     */
    @SaCheckRole("ADMIN")
    @PutMapping()
    public Result<Void> update(@RequestBody @Valid ThemeReq themeUpdateReq) {
        themeService.update(themeUpdateReq);
        return Results.success();
    }

    /**
     * 删除主题设置
     */
    @SaCheckRole("ADMIN")
    @DeleteMapping("/{key}")
    public Result<Void> delete(@PathVariable String key) {
        if (themeService.checkDefaultTheme(key)) {
            return Results.failure(new ClientException("\"默认主题不能删除\""));
        }
        themeService.delete(key);
        return Results.success();
    }

    /**
     * 添加主题设置
     */
    @SaCheckRole("ADMIN")
    @PostMapping()
    public Result<Void> add(@RequestBody @Valid ThemeReq themeAddReq) {
        if(themeService.checkKeyExist(themeAddReq.getThemeKey())){
            return Results.failure(new ClientException("主题key已存在"));
        }
        themeService.add(themeAddReq);
        return Results.success();
    }


}
