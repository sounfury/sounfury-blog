package org.sounfury.aki.domain.task.enums;

import lombok.Getter;

/**
 * 任务模式枚举
 * 定义具体的任务类型
 */
@Getter
public enum TaskMode {
    
    /**
     * 文章总结
     */
    ARTICLE_SUMMARY("article_summary", "文章总结"),
    
    /**
     * 文章摘录
     */
    ARTICLE_EXCERPT("article_excerpt", "文章摘录"),
    
    /**
     * 发布祝贺
     */
    PUBLISH_CONGRATULATION("publish_congratulation", "发布祝贺"),
    
    /**
     * 登录欢迎
     */
    LOGIN_WELCOME("login_welcome", "登录欢迎");
    
    private final String code;
    private final String name;
    
    TaskMode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取任务模式
     */
    public static TaskMode fromCode(String code) {
        for (TaskMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("未知的任务模式代码: " + code);
    }
    
    /**
     * 判断是否为文章相关任务
     */
    public boolean isArticleTask() {
        return this == ARTICLE_SUMMARY || this == ARTICLE_EXCERPT;
    }
    
    /**
     * 判断是否为陪伴相关任务
     */
    public boolean isCompanionTask() {
        return this == PUBLISH_CONGRATULATION || this == LOGIN_WELCOME;
    }
}
