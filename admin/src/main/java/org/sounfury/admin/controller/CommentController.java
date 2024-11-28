package org.sounfury.admin.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.req.CommentPageReq;
import org.sounfury.admin.service.CommentService;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.portal.dto.rep.CommentTreeNode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comment")
public class CommentController {
    private final CommentService commentService;

    /**
     * 查询评论列表
     */
    @GetMapping("/list")
    public Result<PageRepDto<List<CommentTreeNode>>> listComment(CommentPageReq commentPageReq) {
        return Results.success(commentService.listComments(commentPageReq));
    }

    /**
     * 审核评论
     */
    @GetMapping("/audit")
    public Result<Void> auditComment(@RequestParam Long commentId, Boolean pass) {
        commentService.auditComment(commentId, pass);
        return Results.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("{commentId}")
    public Result<Void> deleteComment(@PathVariable @NotNull Long commentId) {
        commentService.deleteComment(commentId);
        return Results.success();
    }


}
