package org.sounfury.aki.domain.prompt;

/**
 * 提示词类型枚举
 * 区分模板和全局提示词
 */
public enum PromptType {
    
    /**
     * 模板类型
     * 包含占位符，需要渲染的提示词模板
     * 如：角色问候语模板、用户称呼模板等
     */
    TEMPLATE("template", "模板"),
    
    /**
     * 全局类型
     * 直接使用的全局提示词
     * 如：基础系统提示词、行为指导提示词等
     */
    GLOBAL("global", "全局");
    
    private final String code;
    private final String description;
    
    PromptType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static PromptType fromCode(String code) {
        for (PromptType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的提示词类型代码: " + code);
    }
    
    /**
     * 检查是否为模板类型
     */
    public boolean isTemplate() {
        return this == TEMPLATE;
    }
    
    /**
     * 检查是否为全局类型
     */
    public boolean isGlobal() {
        return this == GLOBAL;
    }
}
