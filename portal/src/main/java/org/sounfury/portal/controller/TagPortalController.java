package org.sounfury.portal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.sounfury.portal.service.TagPortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portal/tag")
@SaIgnore
@RequiredArgsConstructor
public class TagPortalController {
    private final TagPortalService tagService;

    /**
     * 获取所有标签,以及对应的文章数量
     * @return
     */
    @GetMapping("/all")
    public Result<List<TagsQueryRep>> getAllTags() {
        return Results.success(tagService.getAllTags());
    }
}
