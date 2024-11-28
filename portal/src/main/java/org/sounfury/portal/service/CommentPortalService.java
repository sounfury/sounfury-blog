package org.sounfury.portal.service;

import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.portal.dto.rep.CommentTreeNode;
import org.sounfury.portal.dto.req.CommentAddReq;
import org.sounfury.portal.dto.req.CommentArticlePageReq;

import java.util.List;

public interface CommentPortalService {

    /**
     * 查询文章下的评论
     */
    PageRepDto<List<CommentTreeNode>> getCommentsByArticleId(CommentArticlePageReq commentPageReq);

    void addComment(CommentAddReq commentAddReq);
}
