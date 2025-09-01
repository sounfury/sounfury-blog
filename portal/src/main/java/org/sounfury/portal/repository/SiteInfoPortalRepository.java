package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.sounfury.portal.dto.rep.InfoCountRep;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.coalesce;
import static org.sounfury.blog.jooq.Tables.*;

@Repository
public class SiteInfoPortalRepository extends org.sounfury.blog.jooq.tables.daos.SiteInfoDao {
    public SiteInfoPortalRepository(Configuration configuration) {
        super(configuration);
    }

    public InfoCountRep countInfo() {
        return ctx().select(
                ctx().selectCount().from(ARTICLE).asField("article_count"),
                ctx().selectCount().from(TAG).asField("tags_count"),
                ctx().selectCount().from(CATEGORY).asField("category_count")
        ).fetchOneInto(InfoCountRep.class);
    }

    public void statusArticleInfo(Long totalWords, Long articleCount) {
        ctx().update(SITE_INFO)
                .set(SITE_INFO.TOTAL_WORDS, SITE_INFO.TOTAL_WORDS.add(totalWords))
                .set(SITE_INFO.ARTICLE_COUNT, articleCount)
                .execute();
    }


    public void statusUpdateArticleInfo(Long newTotalWords) {
        ctx().update(SITE_INFO)
                .set(SITE_INFO.TOTAL_WORDS, SITE_INFO.TOTAL_WORDS.add(newTotalWords))
                .execute();
    }

    public void updateViewCount() {
        ctx().update(SITE_INFO)
             .set(SITE_INFO.TOTAL_VISITS, DSL.coalesce(SITE_INFO.TOTAL_VISITS, DSL.val(0L)).add(1L))
             .execute();
    }
}
