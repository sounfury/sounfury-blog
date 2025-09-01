package org.sounfury.aki.infrastructure.shared.context;

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

    // ========== 开发阶段的静态方法 ==========

    /**
     * 开发阶段：设置为站长身份
     */
    public static void setAsOwner() {
        setContext(UserContext.owner("dev-owner-001", "开发站长"));
        log.info("开发模式：设置为站长身份");
    }

    /**
     * 开发阶段：设置为游客身份
     */
    public static void setAsGuest() {
        setContext(UserContext.guest());
        log.info("开发模式：设置为游客身份");
    }

    /**
     * 开发阶段：设置为指定用户
     */
    public static void setAsUser(String userId, String userName, UserRole role) {
        setContext(UserContext.of(userId, userName, role));
        log.info("开发模式：设置为用户身份 - userId: {}, role: {}", userId, role);
    }

    /**
     * 用户上下文信息
     */
    public static class UserContext {
        private final String userId;
        private final String userName;
        private final UserRole role;

        private UserContext(String userId, String userName, UserRole role) {
            this.userId = userId;
            this.userName = userName;
            this.role = role;
        }

        public static UserContext of(String userId, String userName, UserRole role) {
            return new UserContext(userId, userName, role);
        }

        public static UserContext owner(String userId, String userName) {
            return new UserContext(userId, userName, UserRole.OWNER);
        }

        public static UserContext guest() {
            return new UserContext("guest", "游客", UserRole.GUEST);
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public UserRole getRole() {
            return role;
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
    public enum UserRole {
        OWNER("站长"),
        GUEST("旅行者"),
        USER("普通用户");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
