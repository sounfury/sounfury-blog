package org.sounfury.aki.infrastructure.shared.context;

import cn.dev33.satoken.stp.StpUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户身份上下文持有器
 * 用于在请求处理过程中传递用户身份信息
 * 开发阶段使用静态实现，生产环境通过拦截器设置
 */
@Slf4j
public class UserContextHolder {

    private static final ThreadLocal<UserContext> contextHolder = new ThreadLocal<>();

    /**
     * 设置当前用户上下文
     */
    public static void setContext(UserContext context) {
        contextHolder.set(context);

        log.debug("设置用户上下文: {}", context);
    }

    /**
     * 获取当前用户上下文
     */
    public static UserContext getContext() {
        UserContext context = contextHolder.get();
        if (context == null) {
            // 开发阶段返回默认游客身份
            log.debug("用户上下文为空，返回默认游客身份");
            return UserContext.guest();
        }
        return context;
    }

    /**
     * 清除当前用户上下文
     */
    public static void clearContext() {
        contextHolder.remove();
        log.debug("清除用户上下文");
    }

    /**
     * 检查当前用户是否为站长
     */
    public static boolean isOwner() {
        return getContext().isOwner();
    }

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        return getContext().getUserId();
    }

    /**
     * 获取当前用户角色
     */
    public static UserRole getCurrentUserRole() {
        return getContext().getRole();
    }

    /**
     * 用户上下文信息
     */
    public static class UserContext {
        @Getter
        private final String userId;
        private final String userName;
        @Getter
        private final UserRole role;
        private final String token;

        private UserContext(String userId, String userName, UserRole role, String token) {
            this.userId = userId;
            this.userName = userName;
            this.role = role;
            this.token = token;
        }

        public static UserContext of(String userId, String userName, UserRole role, String token) {
            return new UserContext(userId, userName, role, token);
        }

        public static UserContext owner(String userId, String userName, String token) {
            return new UserContext(userId, userName, UserRole.OWNER, token);
        }

        public static UserContext guest() {
            return new UserContext("guest", "游客", UserRole.GUEST,null);
        }

        //获取当前用户的token
        public static String getToken() {
            UserContext context = getContext();
            return context.token;
        }

        public boolean isOwner() {
            return role == UserRole.OWNER;
        }

        public boolean isGuest() {
            return role == UserRole.GUEST;
        }

        @Override
        public String toString() {
            return String.format("UserContext{userId='%s', userName='%s', role=%s}", 
                    userId, userName, role);
        }
    }

    /**
     * 用户角色枚举
     */
    @Getter
    public enum UserRole {
        OWNER("站长"),
        GUEST("旅行者"),
        USER("普通用户");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

    }
}
