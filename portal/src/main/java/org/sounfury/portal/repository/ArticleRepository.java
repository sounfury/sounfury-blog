package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.types.UInteger;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.sounfury.jooq.tables.daos.ArticleDao;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.TagPageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.jooq.tables.Article.ARTICLE;
import static org.sounfury.jooq.tables.ArticleTag.ARTICLE_TAG;

@Repository

public class ArticleRepository extends ArticleDao {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;


    @Autowired
    public ArticleRepository(Configuration configuration) {
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
                        .and(ARTICLE.CATEGORY_ID.eq(UInteger.valueOf(pageReqDto.getCategoryId()))),
                pageReqDto,
                dsl,
                PageArticleRep.MAPPER);
    }


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
                                        .where(ARTICLE_TAG.TAG_ID.eq(UInteger.valueOf(pageReqDto.getTagId())))
                        )),
                pageReqDto,
                dsl,
                PageArticleRep.MAPPER);
    }
}
