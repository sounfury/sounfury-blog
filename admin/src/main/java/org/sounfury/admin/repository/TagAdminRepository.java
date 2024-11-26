package org.sounfury.admin.repository;

import cn.dev33.satoken.stp.StpUtil;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.daos.TagDao;
import org.sounfury.jooq.tables.pojos.Tag;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.jooq.tables.ArticleTag.ARTICLE_TAG;
import static org.sounfury.jooq.tables.Tag.TAG;

@Repository
public class TagAdminRepository extends TagDao {

    @Autowired
    public TagAdminRepository(Configuration configuration) {
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

    /**
     * 插入或维护标签以及文章标签关联表
     */
    public void insertTags(UInteger articleId,List<String> tags){
        UInteger userId= (UInteger) StpUtil.getLoginId();

        tags.forEach(tag -> {
            UInteger tagId = ctx().select(TAG.ID)
                    .from(TAG)
                    .where(TAG.NAME.eq(tag))
                    .fetchOneInto(UInteger.class);
            //先看看标签是否存在，不存在则插入
            if (tagId == null) {
                tagId = ctx().insertInto(TAG, TAG.NAME,TAG.CREATE_BY,TAG.UPDATE_BY)
                        .values(tag,userId,userId)
                        .returning(TAG.ID)
                        .fetchOne()
                        .getId();
            }
            ctx().insertInto(ARTICLE_TAG, ARTICLE_TAG.ARTICLE_ID, ARTICLE_TAG.TAG_ID)
                    .values(articleId, tagId)
                    .execute();
        });
    }
}