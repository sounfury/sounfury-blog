package org.sounfury.aki.domain.conversation.session;

/**
 * 对话模式枚举
 * 简化为两种核心模式
 */
public enum ConversationMode {
    
    /**
     * 普通聊天模式
     * 启用记忆和角色卡，不启用工具调用
     */
    CHAT("chat", "聊天模式"),
    
    /**
     * Agent模式
     * 启用记忆、角色卡和工具调用
     */
    AGENT("agent", "Agent模式");
    
    private final String code;
    private final String name;
    
    ConversationMode(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * 从代码获取模式
     */
    public static ConversationMode fromCode(String code) {
        for (ConversationMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("未知的对话模式: " + code);
    }
}
