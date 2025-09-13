package org.sounfury.portal.repository;

import org.jooq.*;
import org.jooq.Record;

import org.sounfury.blog.jooq.tables.pojos.Article;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.sounfury.portal.dto.rep.HistoryCount;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.HistoryPageArticlesReq;
import org.sounfury.portal.dto.req.TagPageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.blog.jooq.Tables.*;
import org.sounfury.blog.jooq.tables.daos.ArticleDao;

@Repository

public class ArticlePortalRepository extends org.sounfury.blog.jooq.tables.daos.ArticleDao {
    @Autowired
    public ArticlePortalRepository(Configuration configuration) {
        super(configuration);
    }

    /**
     * 分页查询文章
     */
    public PageRepDto<List<PageArticleRep>> pageQueryArticle(PageReqDto pageReqDto) {
        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(ctx().select(PageArticleRep.ARTICLE_FIELDS)
                        .from(ARTICLE)
                        .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                        .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE)),
                pageReqDto,
                dsl,
                PageArticleRep.MAPPER);
    }

    /**
     * 分页查询某个分类id下的文章
     */
    public PageRepDto<List<PageArticleRep>> pageQueryArticleByCategoryId(CategoryPageReq pageReqDto) {
        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(ctx().select(PageArticleRep.ARTICLE_FIELDS)
                        .from(ARTICLE)
                        .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                        .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE))
                        .and(ARTICLE.CATEGORY_ID.eq(pageReqDto.getCategoryId())),
                pageReqDto,
                dsl,
                PageArticleRep.MAPPER);
    }

    /**
     * 分页查询某个标签id下的文章
     *
     * @param pageReqDto
     * @return
     */
    public PageRepDto<List<PageArticleRep>> pageQueryArticleByTagId(TagPageReq pageReqDto) {
        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(ctx().select(PageArticleRep.ARTICLE_FIELDS)
                        .from(ARTICLE)
                        .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                        .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE))
                        .and(ARTICLE.ID.in(
                                ctx().selectDistinct(ARTICLE.ID)
                                        .from(ARTICLE)
                                        .join(ARTICLE_TAG)
                                        .on(ARTICLE.ID.eq(ARTICLE_TAG.ARTICLE_ID))
                                        .where(ARTICLE_TAG.TAG_ID.eq(pageReqDto.getTagId()))
                        )),
                pageReqDto,
                dsl,
                PageArticleRep.MAPPER);
    }

    /**
     * 分页查询历史文章
     *
     * @param historyPageArticlesReq
     * @return
     */
    public PageRepDto<List<PageArticleRep>> pageQueryHistoryArticleByYear(
            HistoryPageArticlesReq historyPageArticlesReq) {
        SelectConditionStep<Record> select = ctx().select(PageArticleRep.ARTICLE_FIELDS)
                .from(ARTICLE)
                .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE))
                .and(ARTICLE.CREATE_TIME
                        .between(historyPageArticlesReq.getHistoryTime(),
                                historyPageArticlesReq.getHistoryTime()
                                        .plusYears(1)));

        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(
                select,
                historyPageArticlesReq,
                dsl,
                PageArticleRep.MAPPER);
    }

    public PageRepDto<List<PageArticleRep>> pageQueryHistoryArticleByMonth(
            HistoryPageArticlesReq historyPageArticlesReq) {
        SelectConditionStep<Record> select = ctx().select(PageArticleRep.ARTICLE_FIELDS)
                .from(ARTICLE)
                .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE))
                .and(ARTICLE.CREATE_TIME
                        .between(historyPageArticlesReq.getHistoryTime(),
                                historyPageArticlesReq.getHistoryTime()
                                        .plusMonths(1)));

        DSLContext dsl = configuration().dsl();
        return JooqPageHelper.getPage(
                select,
                historyPageArticlesReq,
                dsl,
                PageArticleRep.MAPPER);
    }


    public List<HistoryCount> historyArticleCount() {
        String sql = "SELECT DATE_FORMAT(create_time, '%Y-%m') as date, " +
                "COUNT(*) as count " +
                "FROM blog.article " +
                "WHERE del_flag = ? " +
                "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
                "ORDER BY date DESC " +
                "LIMIT ?";

        return ctx().resultQuery(sql, NOT_DEL_FLAG, 5)
                    .fetchInto(HistoryCount.class);
    }

    /**
     * 根据标题模糊查询文章内容
     */
    public List<PageArticleRep> searchArticlesByTitle(String titleKeyword) {
        List<Article> articles = ctx()
                .select(PageArticleRep.ARTICLE_FIELDS)
                .from(ARTICLE)
                .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE))
                .and(ARTICLE.TITLE.contains(titleKeyword))
                .limit(10)
                .fetchInto(Article.class);

        //批量转换为 PageArticleRep
        List<PageArticleRep> pageArticleReps = new ArrayList<>();
        for (Article article : articles) {
            pageArticleReps.add(new PageArticleRep(article));
        }
        return pageArticleReps;
    }
}
