package org.sounfury.admin.repository;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.types.UInteger;
import org.sounfury.jooq.mapper.JooqFieldMapper;
import org.sounfury.jooq.tables.daos.ArticleDao;
import org.sounfury.jooq.tables.pojos.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static org.sounfury.jooq.tables.Article.ARTICLE;

@Repository

public class ArticleAdminRepository extends ArticleDao {
    @Autowired
    public ArticleAdminRepository(Configuration configuration) {
        super(configuration);
    }

    public UInteger insertArticle(Article article) {
        Map<Field<?>, Object> values = JooqFieldMapper.toFieldMap(article, ARTICLE);

        return ctx().insertInto(ARTICLE)
                .set(values)
                .returning(ARTICLE.ID)
                .fetchOne()
                .getId();

    }


    /**
     * 添加文章
     */





}