package org.sounfury.admin.service;

import org.sounfury.admin.dto.req.CommentPageReq;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.portal.dto.rep.CommentTreeNode;
import org.sounfury.portal.dto.req.CommentArticlePageReq;

import java.util.List;

public interface CommentService {

    /**
     * 分页查询评论
     */
    PageRepDto<List<CommentTreeNode>> listComments(CommentPageReq commentPageReq);


    /**
     * 审核评论
     */
    void auditComment(Long commentId, Boolean pass);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId);
}
