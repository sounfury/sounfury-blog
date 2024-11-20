package org.sounfury.core.constant;

public interface LoginConstant {
    /**
     * 令牌
     */
    String TOKEN = "token";

    /**
     * 令牌前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌对应的uuid
     */
    String LOGIN_USER_KEY = "login_user_key";


    /**
     * 令牌对应的username
     */
    String LOGIN_USER_NAME = "login_user_name";


    /**
     * 令牌对应的subject
     */
    String TOKEN_SUBJECT = "sub";


    /**
     * jwt签发
     */
    String LOGIN_TOKEN_KEY = "login_tokens";
    String LOGIN_USERNAME_KEY = "login_username";
}
