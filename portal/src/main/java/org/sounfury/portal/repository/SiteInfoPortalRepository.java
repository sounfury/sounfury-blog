package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.SiteInfoDao;
import org.sounfury.portal.dto.rep.InfoCountRep;
import org.springframework.stereotype.Repository;

import static org.sounfury.jooq.tables.Article.ARTICLE;
import static org.sounfury.jooq.tables.Category.CATEGORY;
import static org.sounfury.jooq.tables.SiteInfo.SITE_INFO;
import static org.sounfury.jooq.tables.Tag.TAG;

@Repository
public class SiteInfoPortalRepository extends SiteInfoDao {
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
                .set(SITE_INFO.TOTAL_VISITS, SITE_INFO.TOTAL_VISITS.add(1L))
                .execute();
    }
}
