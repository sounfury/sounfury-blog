package org.sounfury.portal.service;

import org.sounfury.jooq.tables.pojos.SiteInfo;
import org.sounfury.portal.dto.rep.InfoCountRep;
import org.sounfury.portal.dto.rep.SiteCreatorInfoRep;

public interface SiteInfoPortalService {


    /**
     * 查询网站信息
     */
    SiteInfo getSiteInfo();


    /**
     * 查询网站创始人信息
     */
    SiteCreatorInfoRep getSiteCreatorInfo();

    InfoCountRep count();
}
