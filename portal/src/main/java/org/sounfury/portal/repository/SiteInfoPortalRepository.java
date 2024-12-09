package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.SiteInfoDao;
import org.sounfury.portal.dto.rep.InfoCountRep;
import org.springframework.stereotype.Repository;

import static org.sounfury.jooq.tables.Article.ARTICLE;
import static org.sounfury.jooq.tables.Category.CATEGORY;
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
}
