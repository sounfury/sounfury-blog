package org.sounfury.satoken.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 静态配置类
 */
@Component
public class TokenPropertiesUtil {

    // 定义静态变量
    private static String tokenName;
    private static long timeout;
    private static long activeTimeout;
    private static boolean isConcurrent;
    private static boolean isShare;
    private static String tokenStyle;
    private static boolean isLog;
    private static String jwtSecretKey;

    // 使用 @Value 注入配置值
    @Value("${sa-token.token-name}")
    private String tokenNameValue;

    @Value("${sa-token.timeout}")
    private long timeoutValue;

    @Value("${sa-token.active-timeout}")
    private long activeTimeoutValue;

    @Value("${sa-token.is-concurrent}")
    private boolean isConcurrentValue;

    @Value("${sa-token.is-share}")
    private boolean isShareValue;

    @Value("${sa-token.token-style}")
    private String tokenStyleValue;

    @Value("${sa-token.is-log}")
    private boolean isLogValue;

    @Value("${sa-token.jwt-secret-key}")
    private String jwtSecretKeyValue;

    // 初始化静态变量
    @PostConstruct
    private void init() {
        tokenName = tokenNameValue;
        timeout = timeoutValue;
        activeTimeout = activeTimeoutValue;
        isConcurrent = isConcurrentValue;
        isShare = isShareValue;
        tokenStyle = tokenStyleValue;
        isLog = isLogValue;
        jwtSecretKey = jwtSecretKeyValue;
    }

    // 提供静态方法访问
    public static String getTokenName() {
        return tokenName;
    }

    public static long getTimeout() {
        return timeout;
    }

    public static long getActiveTimeout() {
        return activeTimeout;
    }

    public static boolean isConcurrent() {
        return isConcurrent;
    }

    public static boolean isShare() {
        return isShare;
    }

    public static String getTokenStyle() {
        return tokenStyle;
    }

    public static boolean isLog() {
        return isLog;
    }

    public static String getJwtSecretKey() {
        return jwtSecretKey;
    }
}
