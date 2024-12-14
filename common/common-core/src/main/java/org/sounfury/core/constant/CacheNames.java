package org.sounfury.core.constant;

public interface CacheNames {
    /**
     * 系统配置
     */
    String SYS_CONFIG = "sys_config";

    /**
     * 主题配置
     */
    String SYS_THEME = "sys_theme";

    /**
     * 网站信息
     */
    String SITE_INFO = "site_info";

    /**
     * 数据字典
     */
    String SYS_DICT = "sys_dict";

    /**
     * 客户端
     */
    String SYS_CLIENT = GlobalConstants.GLOBAL_REDIS_KEY + "sys_client#30d";

    /**
     * 用户账户
     */
    String SYS_USER_NAME = "sys_user_name#30d";

    /**
     * 用户名称
     */
    String SYS_NICKNAME = "sys_nickname#30d";

    /**
     * OSS内容
     */
    String SYS_OSS = "sys_oss#30d";

    /**
     * OSS配置
     */
    String SYS_OSS_CONFIG = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss_config";

    /**
     * 在线用户
     */
    String ONLINE_TOKEN = "online_tokens";

    /**
     * 登陆用户
     */
    String LOGIN_USER = "token:login:user:";

}
