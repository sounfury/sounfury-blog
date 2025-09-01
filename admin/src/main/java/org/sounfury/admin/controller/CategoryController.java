package org.sounfury.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.sounfury.admin.dto.req.CategoryAddReq;
import org.sounfury.admin.dto.req.SortCategoryReq;
import org.sounfury.admin.service.CategoryService;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.blog.jooq.tables.pojos.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@AllArgsConstructor
@SaCheckRole("ADMIN")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 更新分类
     */
    @PutMapping()
    public Result<Void> updateCategory(@RequestBody Category categoryUpdateReq) {
        if (categoryUpdateReq.getId() == 1 && categoryUpdateReq.getPid() != null) {
            return Results.failure(new ClientException("默认分类不能修改父分类"));
        }
        categoryService.updateCategory(categoryUpdateReq);
        return Results.success();
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/sort")
    public Result<Void> BatchUpdateCategorySort(@RequestBody List<SortCategoryReq> sortCategoryReq) {
        //遍历判断是否有默认分类
        for (SortCategoryReq sortCategoryReq1 : sortCategoryReq) {
            if (sortCategoryReq1.getId() == 1L && sortCategoryReq1.getPid() != null) {
                return Results.failure(new ClientException("默认分类不能修改父分类"));
            }
        }
        categoryService.BatchUpdateCategorySort(sortCategoryReq);
        return Results.success();
    }


    /**
     * 删除分类
     */
    @DeleteMapping("{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        if (id == 1L) {
            return Results.failure(new ClientException("默认分类不能删除"));
        }
        categoryService.deleteCategory(id);
        return Results.success();
    }

    /**
     * 新增分类
     */
    @PostMapping()
    public Result<Void> addCategory(@Valid @RequestBody CategoryAddReq categoryAddReq) {
        //拿到当前登录用户的id
        categoryService.addCategory(categoryAddReq);
        return Results.success();
    }

    /**
     * 返回分类字典,Map<id,name>
     */
    @GetMapping("/dict")
    public Result<Map<Long,String>> categoryDict() {
        return Results.success(categoryService.categoryDict());
    }


}
