package org.sounfury.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import lombok.RequiredArgsConstructor;
import org.sounfury.admin.service.TagService;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequestMapping("/admin/tag")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class TagAdminController {
    private final TagService tagService;
    //返回tag字典
    @GetMapping("/dict")
    public Result<Map<Long, String>> tagDict() {
        return Results.success(tagService.tagDict());
    }
}
