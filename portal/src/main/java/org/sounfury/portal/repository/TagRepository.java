package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.daos.TagDao;
import org.sounfury.jooq.tables.pojos.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sounfury.core.constant.Constants.DEL_FLAG;
import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.jooq.tables.ArticleTag.ARTICLE_TAG;
import static org.sounfury.jooq.tables.Tag.TAG;

@Repository
public class TagRepository extends TagDao {

    @Autowired
    public TagRepository(Configuration configuration) {
        super(configuration);
    }

    /**
     * 根据文章id查询该文章下的标签
     */
    public List<Tag> fetchByArticleId(UInteger articleId) {
        return ctx().select(TAG.asterisk())
                .from(TAG)
                .leftJoin(ARTICLE_TAG)
                .on(TAG.ID.eq(ARTICLE_TAG.TAG_ID))
                .where(ARTICLE_TAG.TAG_ID.eq(articleId))
                .and(TAG.DEL_FLAG.eq((NOT_DEL_FLAG)))
                .fetchInto(Tag.class);

    }
}