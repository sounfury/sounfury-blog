package org.sounfury.aki.domain.conversation.session;

public enum SessionMemoryType {

    //user,assistant,system

    USER("user", "用户记忆"),

    ASSISTANT("assistant", "助手记忆"),

    SYSTEM("system", "系统记忆");

    private final String code;
    private final String name;
    SessionMemoryType(String code, String name) {
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
     * 从代码获取类型
     */
    public static SessionMemoryType fromCode(String code) {
        for (SessionMemoryType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的会话记忆类型: " + code);
    }
}
