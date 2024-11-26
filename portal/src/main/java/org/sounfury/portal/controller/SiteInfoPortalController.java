package org.sounfury.portal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.tables.pojos.SiteInfo;
import org.sounfury.portal.dto.rep.SiteCreatorInfoRep;
import org.sounfury.portal.service.SiteInfoPortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/site-info")
@RequiredArgsConstructor
@SaIgnore
public class SiteInfoPortalController {
    private final SiteInfoPortalService siteInfoService;

    /**
     * 获取站点信息
     * @return
     */
    @GetMapping("/info")
    public Result<SiteInfo> getSiteInfo() {
        SiteInfo siteInfo = siteInfoService.getSiteInfo();
        return Results.success(siteInfo);
    }

    /**
     * 获取站点创建者信息
     * @return
     */
    @GetMapping("/creator-info")
    public Result<SiteCreatorInfoRep> getSiteCreatorInfo() {
        return Results.success(siteInfoService.getSiteCreatorInfo());
    }
}
