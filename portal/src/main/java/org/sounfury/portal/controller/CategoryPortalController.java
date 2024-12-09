package org.sounfury.portal.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.portal.dto.rep.CategoryTreeNode;
import org.sounfury.portal.service.CategoryPortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@SaIgnore
@RequestMapping("/portal/category")
public class CategoryPortalController {
    private final CategoryPortalService categoryService;

    /**
     * 查询所有的分类
     */
    @GetMapping("/all")
    public Result<List<CategoryTreeNode>> getAllCategory() {
        return Results.success(categoryService.getAllCategory());
    }


}
