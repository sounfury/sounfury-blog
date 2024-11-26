package org.sounfury.portal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.AllArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.rep.SingleArticleRep;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.HistoryPageArticlesReq;
import org.sounfury.portal.dto.req.TagPageReq;
import org.sounfury.portal.service.ArticlePortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SaIgnore
@RequestMapping("/portal/article")
@AllArgsConstructor
/**
 * 文章控制层
 */
public class ArticlePortalController {
    private final ArticlePortalService articleService;

    /**
     * 根据id获取文章
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SingleArticleRep> getArticleById(@PathVariable Long id) {
        return Results.success(
                articleService.getArticleById(id)
        );
    }

    /**
     * 分页查询文章
     */
    @GetMapping("/page")
    public Result<PageRepDto<List<PageArticleRep>>> pageQueryArticle(PageReqDto pageReqDto) {
        return Results.success(articleService.pageQueryArticle(pageReqDto));
    }

    /**
     * 查询历史文章
     */
    @GetMapping("/history")
    public Result<PageRepDto<List<PageArticleRep>>> historyArticle(HistoryPageArticlesReq historyPageArticlesReq) {
        return Results.success(articleService.historyArticle(historyPageArticlesReq));
    }

    /**
     * 根据分类id分页查询文章
     */
    @GetMapping("/category/page")
    public Result<PageRepDto<List<PageArticleRep>>>
    pageQueryArticleByCategoryId(CategoryPageReq pageReqDto) {
        return Results.success(articleService.pageQueryArticleByCategoryId(pageReqDto));
    }

    /**
     * 根据tagId分页查询文章
     */
    @GetMapping("/tag/page")
    public Result<PageRepDto<List<PageArticleRep>>>
    pageQueryArticleByTagId(TagPageReq pageReqDto) {
        return Results.success(articleService.pageQueryArticleByTagId(pageReqDto));
    }

}
