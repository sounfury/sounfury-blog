package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.sounfury.jooq.tables.daos.ArticleDao;
import org.sounfury.portal.dto.rep.HistoryCount;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.HistoryPageArticlesReq;
import org.sounfury.portal.dto.req.TagPageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.jooq.tables.Article.ARTICLE;
import static org.sounfury.jooq.tables.ArticleTag.ARTICLE_TAG;

@Repository

public class ArticlePortalRepository extends ArticleDao {
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


    public List<PageArticleRep> pageQueryArticleTest(PageReqDto pageReqDto) {
        return ctx().select(PageArticleRep.ARTICLE_FIELDS)
                .from(ARTICLE)
                .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE))
                .fetch()
                .map(PageArticleRep.MAPPER);

    }

    public List<HistoryCount> historyArticleCount() {
        return ctx().select(
                        ARTICLE.CREATE_TIME.cast(LocalDate.class)
                                .as("date"), // 使用 cast 转换为 LocalDate
                        DSL.count()
                                .as("count") // 统计文章数量
                )
                .from(ARTICLE)
                .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG)) // 仅查询未删除的文章
                .groupBy(ARTICLE.CREATE_TIME.cast(LocalDate.class)) // 按 LocalDate 分组
                .orderBy(ARTICLE.CREATE_TIME.cast(LocalDate.class)
                        .desc()) // 按时间倒序排序
                .limit(5) // 限制结果为最近 5 个时间段
                .fetchInto(HistoryCount.class); // 映射为自定义类
    }
}
