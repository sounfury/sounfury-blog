package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserLoginReqDTO;
import org.sounfury.system.dto.req.UserRegisterReqDTO;
import org.sounfury.system.service.LoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sys")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    /**
     * 登录
     * @param requestParam
     */
    @SaIgnore
    @PostMapping("/login")
    public Result<String> login(UserLoginReqDTO requestParam) {
        StpUtil.login(loginService.login(requestParam), requestParam.getRememberMe());
        return Results.success("登录成功");
    }

    /**
     *
     * @param requestParam
     * @return
     */
    @SaIgnore
    @PostMapping("/register")
    public Result<String> register(UserRegisterReqDTO requestParam) {
        loginService.register(requestParam);
        return Results.success("注册成功");
    }

    /**
     * 检查用户名是否存在
     * @param username
     * @return True: 存在 False: 不存在
     */
    @SaIgnore
    @GetMapping("/checkUsername")
    public Result<Boolean> checkUsername(String username) {
        return Results.success(loginService.checkUsername(username));
    }


    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Results.success();
    }


    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public Result<Void> changePassword(ChangePwdReqDTO requestParam) {
        loginService.changePassword(requestParam);
        return Results.success();
    }

    /**
     * 重置密码
     * TODO :理论上应该发送邮件或者短信验证码，这个接口先不用
     */
    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(String username) {
        loginService.resetPassword(username);
        return Results.success();
    }




}
