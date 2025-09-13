package org.sounfury.aki.contracts.constant;

/**
 * 记忆类型枚举
 */
public enum MemoryType {
    
    /**
     * 会话内存：仅在当前会话有效，页面刷新即清除
     * 适用于游客用户
     */
    SESSION_ONLY,
    
    /**
     * 持久化记忆：存储到数据库，跨会话保持
     * 适用于注册用户或站长
     */
    PERSISTENT
}
