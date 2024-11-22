package org.sounfury.portal.controller;

import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.sounfury.portal.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portal/tag")
public class TagController {
    private TagService tagService;

    @GetMapping("/all")
    public Result<List<TagsQueryRep>> getAllTags() {
        return Results.success(tagService.getAllTags());
    }
}
