package org.sounfury.system.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.system.util.IpUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/ip")
@SaIgnore
public class IpController {

    @GetMapping
    public Result<String> getIp(HttpServletRequest request) {
        return Results.success(IpUtils.getExternalIp(request));
    }

}
