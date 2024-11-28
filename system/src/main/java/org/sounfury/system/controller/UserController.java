package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.dto.req.UserRoleEditReq;
import org.sounfury.system.repository.urp.UserRepository;
import org.sounfury.system.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/pageUser")
    public Result<PageRepDto<List<UserPageQueryRepDTO>>> pageUser(UserPageQueryReqDTO pageReqDto) {
        return Results.success(userService.pageQueryUser(pageReqDto));
    }

    @PutMapping("/userRole")
    public Result<Void> editUserRole(@RequestBody UserRoleEditReq userRoleEditReq) {
        userService.editUserRole(userRoleEditReq);
        return Results.success();
    }

    @DeleteMapping("/user")
    public Result<Void> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return Results.success();
    }


}
