package org.sounfury.aki.domain.conversation.memory.event;

/**
 * 全局记忆操作类型枚举
 * 定义全局记忆的各种变更操作
 */
public enum GlobalMemoryOperationType {
    
    /**
     * 创建操作
     */
    CREATE("创建"),
    
    /**
     * 更新操作
     */
    UPDATE("更新"),
    
    /**
     * 删除操作
     */
    DELETE("删除");
    
    private final String description;
    
    GlobalMemoryOperationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
