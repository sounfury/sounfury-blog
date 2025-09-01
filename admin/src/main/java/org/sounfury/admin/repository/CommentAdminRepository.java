package org.sounfury.admin.repository;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.sounfury.admin.dto.req.CommentPageReq;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import static org.sounfury.blog.jooq.Tables.*;
import org.sounfury.portal.dto.req.CommentArticlePageReq;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sounfury.core.constant.Constants.*;
import static org.sounfury.blog.jooq.tables.Comment.COMMENT;

@Repository
public class CommentAdminRepository extends org.sounfury.blog.jooq.tables.daos.CommentDao {
    public CommentAdminRepository(Configuration configuration) {
        super(configuration);
    }


    public void updateCommentStatus(Long commentId, Byte pass) {
        ctx().update(COMMENT)
                .set(COMMENT.ENABLE_STATUS, pass)
                .where(COMMENT.ID.eq(commentId))
                .execute();
    }

    public void deleteCommentById(Long commentId) {
        ctx().update(COMMENT)
                .set(COMMENT.DEL_FLAG,DEL_FLAG)
                .where(COMMENT.ID.eq(commentId))
                .execute();
    }

    /**
     * 分页查询所有评论
     */
    public PageRepDto<List<org.sounfury.blog.jooq.tables.records.CommentRecord>> getComments(CommentPageReq req) {
        SelectConditionStep<org.sounfury.blog.jooq.tables.records.CommentRecord> and = ctx()
                .selectFrom(COMMENT)
                .where(COMMENT.DEL_FLAG.eq(NOT_DEL_FLAG));

        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(and, req, dsl);
    }
}
