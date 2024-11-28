package org.sounfury.admin.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.sounfury.admin.dto.req.CategoryAddReq;
import org.sounfury.admin.dto.req.CategoryUpdateReq;
import org.sounfury.admin.service.CategoryService;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 更新分类
     */
    @PutMapping()
    public Result<Void> updateCategory(CategoryUpdateReq categoryUpdateReq) {
        categoryService.updateCategory(categoryUpdateReq);
        return Results.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping()
    public Result<Void> deleteCategory(@RequestParam Long id) {
        if(id==1L){
            return Results.failure(new ClientException("默认分类不能删除"));
        }
        categoryService.deleteCategory(id);
        return Results.success();
    }

    /**
     * 新增分类
     */
    @PostMapping()
    public Result<Void> addCategory(@Valid CategoryAddReq categoryAddReq) {
        categoryService.addCategory(categoryAddReq);
        return Results.success();
    }

}
