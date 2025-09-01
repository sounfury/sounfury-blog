package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static org.sounfury.blog.jooq.Tables.*;
import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
@Repository
public class TagPortalRepository extends org.sounfury.blog.jooq.tables.daos.TagDao {

    @Autowired
    public TagPortalRepository(Configuration configuration) {
        super(configuration);
    }

    /**
     * 根据文章id查询该文章下的标签
     */
    public List<org.sounfury.blog.jooq.tables.pojos.Tag> fetchByArticleId(Long articleId) {
        return ctx().select(TAG.asterisk())
                .from(TAG)
                .where(TAG.ID.in(
                        ctx().select(ARTICLE_TAG.TAG_ID)
                                .from(ARTICLE_TAG)
                                .where(ARTICLE_TAG.ARTICLE_ID.eq(articleId))
                ))
                .and(TAG.DEL_FLAG.eq(NOT_DEL_FLAG))
                .fetchInto(org.sounfury.blog.jooq.tables.pojos.Tag.class);
    }


    /**
     * 查到所有标签并且对标签下文章数进行统计
     */
    public List<TagsQueryRep> fetchAllTag() {
        return ctx().select(TAG.asterisk(), DSL.count(ARTICLE_TAG.ARTICLE_ID)
                        .as("article_count"))
                .from(TAG)
                .leftJoin(ARTICLE_TAG)
                .on(TAG.ID.eq(ARTICLE_TAG.TAG_ID))
                .where(TAG.DEL_FLAG.eq(NOT_DEL_FLAG))
                .groupBy(TAG.ID)
                .fetchInto(TagsQueryRep.class);
    }
}