package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sys")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @SaIgnore
    @GetMapping("/pageUser")
    public Result<PageRepDto<List<UserPageQueryRepDTO>>> pageUser(UserPageQueryReqDTO pageReqDto) {
        return Results.success(userService.pageQueryUser(pageReqDto));
    }
}
