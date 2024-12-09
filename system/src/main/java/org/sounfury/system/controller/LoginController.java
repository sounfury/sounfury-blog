package org.sounfury.system.controller;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.system.dto.rep.UserInfo;
import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserLoginReqDTO;
import org.sounfury.system.dto.req.UserRegisterReqDTO;
import org.sounfury.system.model.LoginUser;
import org.sounfury.system.service.LoginService;
import org.sounfury.system.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sounfury.core.constant.CacheNames.LOGIN_USER;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final UserService userService;

    /**
     * 登录
     *
     * @param requestParam
     */
    @SaIgnore
    @PostMapping("/login")
    public Result<Map<String,String>> login(@Valid @RequestBody UserLoginReqDTO requestParam) {
        if(StpUtil.isLogin()){
            StpUtil.logout();
        }
        LoginUser loginUser = loginService.login(requestParam);
        StpUtil.login(loginUser.getId());
        StpUtil.getSession().set(LOGIN_USER, loginUser);
        Map<String,String> token=new HashMap<>();
        token.put("token",StpUtil.getTokenValue());
        return Results.success(token);
    }

    /**
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
     *
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
        System.out.println("当前是否处于登录状态：" + StpUtil.isLogin());
        return Results.success();
    }

    /**
     * 获取登陆用户详细信息
     */
    @GetMapping("/getInfo")
    public Result<UserInfo> getInfo() {
        Object loginUser = StpUtil.getSession().get(LOGIN_USER);
        List<String> roleList = StpUtil.getRoleList();
        List<String> permissionList = StpUtil.getPermissionList();
        return Results.success(UserInfo.builder()
                .loginUser((LoginUser) loginUser)
                .roles(roleList)
                .permissions(permissionList)
                .build());
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
