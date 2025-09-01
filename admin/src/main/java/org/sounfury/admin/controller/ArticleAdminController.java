package org.sounfury.admin.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.rep.ArticleDetailRep;
import org.sounfury.admin.dto.rep.ArticlePageRep;
import org.sounfury.admin.dto.req.ArticleAddReq;
import org.sounfury.admin.dto.req.ArticleUpdateReq;
import org.sounfury.admin.service.ArticleService;
import org.sounfury.admin.service.CategoryService;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sounfury.admin.common.constant.Constants.DEFAULT_CATEGORY_ID;

/**
 * 文章管理
 */
@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class ArticleAdminController {
    private final ArticleService articleService;
    private final CategoryService categoryService;

    /**
     * 新增文章
     *
     * @param articleAddReq
     * @return
     */
    @PostMapping
    public Result<Map<String, Long>> addArticle(@RequestBody @Valid ArticleAddReq articleAddReq) {
        if (articleAddReq.getCategoryId() == null) {
            articleAddReq.setCategoryId(DEFAULT_CATEGORY_ID);
        } else if (categoryService.isExist(articleAddReq.getCategoryId())) {
            Results.failure(new ClientException("分类不存在"));
        }

        long articleId = articleService.addArticle(articleAddReq);
        Map<String,Long> resultMap=new HashMap<>();
        resultMap.put("id",articleId);
        return Results.success(resultMap);
    }

    /**
     * 更新文章
     *
     * @param articleUpdateReq
     * @return
     */
    @PutMapping
    public Result<Long> updateArticle(@RequestBody @Valid ArticleUpdateReq articleUpdateReq) {
        if (categoryService.isExist(articleUpdateReq.getCategoryId())) {
            Results.failure(new ClientException("要变更的分类不存在"));
        }
        long articleId = articleService.updateArticle(articleUpdateReq);
        return Results.success(articleId);
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable @NotNull Long id) {
        articleService.deleteArticle(id);
        return Results.success();
    }

    /**
     * 分页查询文章
     */
    @GetMapping("/page")
    public Result<PageRepDto<List<ArticlePageRep>>> pageArticle(@Valid PageReqDto articlePageReq) {
        return Results.success(articleService.pageArticle(articlePageReq));
    }

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ArticleDetailRep> getArticle(@PathVariable @NotNull Long id) {
        return Results.success(articleService.getArticle(id));
    }


}
