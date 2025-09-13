package org.sounfury.aki.infrastructure.shared;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UserContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 1. 判断是否登录
            if (!StpUtil.isLogin()) {
                // 游客
                UserContextHolder.setContext(UserContextHolder.UserContext.guest());
            } else {
                // 已登录
                String userId = StpUtil.getLoginIdAsString();
                String token = StpUtil.getTokenValue();
                log.info("已登录用户ID: {}, Token: {}", userId, token);
                String userName = "用户" + userId; // 你可以从数据库或 SaSession 拿真实用户名
                // 2. 判断角色
                UserContextHolder.UserRole role;
                if (StpUtil.hasRole("ADMIN")) {
                    log.info("用户 {} 具有 ADMIN 角色", userId);
                    role = UserContextHolder.UserRole.OWNER;
                } else {
                    role = UserContextHolder.UserRole.USER;
                }

                UserContextHolder.setContext(
                        UserContextHolder.UserContext.of(userId, userName, role, token)
                );
            }

            chain.doFilter(request, response);
        } finally {
            // 确保请求结束时清理 ThreadLocal，避免内存泄露
            UserContextHolder.clearContext();
        }
    }
}
