package org.sounfury.portal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.portal.dto.rep.CommentTreeNode;
import org.sounfury.portal.dto.req.CommentAddReq;
import org.sounfury.portal.dto.req.CommentArticlePageReq;
import org.sounfury.portal.service.CommentPortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/comment")
public class CommentPortalController {
    private final CommentPortalService commentService;

    /**
     * 查询某个文章下的所有评论，分页
     */
    @SaIgnore
    @GetMapping()
    public Result<PageRepDto<List<CommentTreeNode>>> getCommentsByArticleId(CommentArticlePageReq commentPageReq) {
        return Results.success(commentService.getCommentsByArticleId(commentPageReq));
    }

    @SaCheckPermission("COMMENT")
    @PutMapping()
    /**
     * 新增评论
     */
    public Result<Void> addComment(@Valid CommentAddReq commentAddReq) {
        commentService.addComment(commentAddReq);
        return Results.success();
    }
}
