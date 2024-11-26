package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.SiteInfoDao;
import org.springframework.stereotype.Repository;

@Repository
public class SiteInfoPortalRepository extends SiteInfoDao {
    public SiteInfoPortalRepository(Configuration configuration) {
        super(configuration);
    }

}
