package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.sounfury.portal.dto.req.CommentArticlePageReq;
import org.springframework.stereotype.Repository;
import static org.sounfury.blog.jooq.Tables.*;
import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;

@Repository
public class CommentPortalRepository extends org.sounfury.blog.jooq.tables.daos.CommentDao {

    public CommentPortalRepository(Configuration configuration) {
        super(configuration);
    }


    /**
     * 查询某一文章下所有评论
     */
    public PageRepDto<List<org.sounfury.blog.jooq.tables.records.CommentRecord>> getComments(CommentArticlePageReq req) {
        SelectConditionStep<org.sounfury.blog.jooq.tables.records.CommentRecord> and = ctx()
                .selectFrom(COMMENT)
                .where(COMMENT.ARTICLE_ID.eq(req.getArticleId())) // 限制某篇文章
                .and(COMMENT.DEL_FLAG.eq(NOT_DEL_FLAG)) // 未删除
                .and(COMMENT.ENABLE_STATUS.eq(STATUS_ENABLE));

        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(and, req, dsl);
    }



    /**
     * // 查询文章下的所有一级评论记录（parent_id 为空或）
     */
    public PageRepDto<List<org.sounfury.blog.jooq.tables.records.CommentRecord>> getParentComments(CommentArticlePageReq req) {
        SelectConditionStep<org.sounfury.blog.jooq.tables.records.CommentRecord> and = ctx()
                .selectFrom(COMMENT)
                .where(COMMENT.ARTICLE_ID.eq(req.getArticleId())) // 限制某篇文章
                .and(COMMENT.PARENT_ID.isNull())
                .and(COMMENT.DEL_FLAG.eq(NOT_DEL_FLAG)) // 未删除
                .and(COMMENT.ENABLE_STATUS.eq(STATUS_ENABLE));

        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(and, req, dsl);
    }

    /**
     * 查询某个评论的所有子评论
     */
    public List<org.sounfury.blog.jooq.tables.records.CommentRecord> getChildrenComments(Long parentId) {
        return ctx()
                .selectFrom(COMMENT)
                .where(COMMENT.PARENT_ID.eq(parentId))
                .and(COMMENT.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(COMMENT.ENABLE_STATUS.eq(STATUS_ENABLE))
                .fetch();
    }
}
