package org.sounfury.admin.service;

import org.sounfury.jooq.tables.pojos.SiteInfo;
import org.sounfury.portal.dto.rep.SiteCreatorInfoRep;

public interface SiteInfoService {


    /**
     * 查询网站信息
     */
    SiteInfo getSiteInfo();


    /**
     * 查询网站创始人信息
     */
    SiteCreatorInfoRep getSiteCreatorInfo();

}
