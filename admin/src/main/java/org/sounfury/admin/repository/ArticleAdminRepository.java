package org.sounfury.admin.repository;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.sounfury.admin.dto.rep.ArticlePageRep;
import org.sounfury.jooq.mapper.JooqFieldMapper;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.sounfury.jooq.tables.daos.ArticleDao;
import org.sounfury.jooq.tables.pojos.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static org.sounfury.admin.common.constant.Constants.DEFAULT_CATEGORY_ID;
import static org.sounfury.core.constant.Constants.*;
import static org.sounfury.jooq.tables.Article.ARTICLE;

@Repository

public class ArticleAdminRepository extends ArticleDao {
    @Autowired
    public ArticleAdminRepository(Configuration configuration) {
        super(configuration);
    }

    public Long insertArticle(Article article) {
        Map<Field<?>, Object> values = JooqFieldMapper.toFieldMap(article, ARTICLE);
        return ctx().insertInto(ARTICLE)
                .set(values)
                .returning(ARTICLE.ID)
                .fetchOne()
                .getId();
    }

    public void updateCategoryToDefault(List<Long> allChildIds) {
        ctx().update(ARTICLE)
                .set(ARTICLE.CATEGORY_ID, DEFAULT_CATEGORY_ID)
                .where(ARTICLE.CATEGORY_ID.in(allChildIds))
                .execute();
    }

    public void updateArticle(Article convert) {
        //返回变更对象的主键
        ctx().update(ARTICLE)
                .set(JooqFieldMapper.toFieldMap(convert, ARTICLE))
                .where(ARTICLE.ID.eq(convert.getId()))
                .execute();

    }

    public void deleteArticleById(Long id) {
        //删除文章。这里的删除是逻辑删除，只是将文章的删除标记置为1
        ctx().update(ARTICLE)
                .set(ARTICLE.DEL_FLAG, DEL_FLAG)
                .where(ARTICLE.ID.eq(id))
                .execute();
    }


    public PageRepDto<List<ArticlePageRep>> pageArticle(PageReqDto articlePageReq) {
        DSLContext dsl = configuration().dsl();

        return JooqPageHelper.getPage(ctx().select(ArticlePageRep.ARTICLE_FIELDS)
                        .from(ARTICLE)
                        .where(ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG)),
                articlePageReq,
                dsl,
                ArticlePageRep.MAPPER);
    }

    public int countEnabledArticle() {
        // 统计启用且未删除的文章数量
        return ctx().fetchCount(ARTICLE, ARTICLE.DEL_FLAG.eq(NOT_DEL_FLAG)
                .and(ARTICLE.ENABLE_STATUS.eq(STATUS_ENABLE)));
    }


}