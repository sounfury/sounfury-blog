package org.sounfury.admin.controller;

import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.req.ArticleAddReq;
import org.sounfury.admin.service.ArticleService;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.tables.pojos.Article;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public Result<Void> addArticle(@RequestBody ArticleAddReq articleAddReq) {
        articleService.addArticle(articleAddReq);
        return Results.success();
    }

}
