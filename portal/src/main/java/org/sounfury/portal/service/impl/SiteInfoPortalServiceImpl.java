package org.sounfury.portal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.jooq.tables.daos.SiteCreatorInfoDao;
import org.sounfury.jooq.tables.pojos.SiteCreatorInfo;
import org.sounfury.jooq.tables.pojos.SiteInfo;
import org.sounfury.portal.dto.rep.InfoCountRep;
import org.sounfury.portal.dto.rep.SiteCreatorInfoRep;
import org.sounfury.portal.repository.SiteInfoPortalRepository;
import org.sounfury.portal.service.SiteInfoPortalService;
import org.sounfury.utils.RedisCache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static org.sounfury.core.constant.CacheNames.SITE_INFO;

@RequiredArgsConstructor
@Service
public class SiteInfoPortalServiceImpl implements SiteInfoPortalService {
    private final SiteInfoPortalRepository siteInfoRepository;
    private final SiteCreatorInfoDao siteCreatorInfoRepository;


    @Override
    @Cacheable(value = SITE_INFO, key = "'siteInfo'")
    public SiteInfo getSiteInfo() {
       return siteInfoRepository.fetchOneById((byte) 1);
    }

    @Override
    @Cacheable(value = "siteCreatorInfoCache", key = "'siteCreatorInfo'")
    public SiteCreatorInfoRep getSiteCreatorInfo() {
        SiteCreatorInfo siteCreatorInfo = siteCreatorInfoRepository.fetchOneById((byte) 1);
        return new SiteCreatorInfoRep(siteCreatorInfo);
    }

    @Override
    public InfoCountRep count() {
        return siteInfoRepository.countInfo();
    }
}
