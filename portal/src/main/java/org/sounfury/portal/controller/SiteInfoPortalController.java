package org.sounfury.portal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.sounfury.blog.jooq.tables.pojos.SiteInfo;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.blog.jooq.tables.pojos.SiteInfo;
import org.sounfury.portal.dto.rep.InfoCountRep;
import org.sounfury.portal.dto.rep.SiteCreatorInfoRep;
import org.sounfury.portal.dto.rep.SiteInfoRep;
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
     *
     * @return
     */
    @GetMapping("/info")
    public Result<SiteInfoRep> getSiteInfo() {
        SiteInfo siteInfo = siteInfoService.getSiteInfo();
        return Results.success(BeanUtil.copyProperties(siteInfo, SiteInfoRep.class));
    }

    /**
     * 获取站点创建者信息
     *
     * @return
     */
    @GetMapping("/creator-info")
    public Result<SiteCreatorInfoRep> getSiteCreatorInfo() {
        return Results.success(siteInfoService.getSiteCreatorInfo());
    }

    /**
     * 统计文章，标签，分类
     */
    @GetMapping("/count")
    public Result<InfoCountRep> count() {
        return Results.success(siteInfoService.count());
    }


}
