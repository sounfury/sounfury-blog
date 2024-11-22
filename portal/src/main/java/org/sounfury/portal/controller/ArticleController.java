package org.sounfury.portal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.AllArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.rep.SingleArticleRep;
import org.sounfury.portal.service.ArticleService;
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
public class ArticleController {
    private final ArticleService articleService;

    /**
     * 根据id获取文章
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SingleArticleRep> getArticleById(@PathVariable Long id) {
        return Results.success(
                articleService.getArticleById(id)
        );
    }

//    /**
//     * 分页查询文章
//     */
//    @GetMapping("/page")
//    public Result<List<PageArticleRep>> pageQueryArticle() {
//        return Results.success();
//    }




}
