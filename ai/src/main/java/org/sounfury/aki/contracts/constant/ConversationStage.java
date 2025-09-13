package org.sounfury.aki.contracts.constant;

/**
 * 对话阶段枚举
 * 区分advisor的添加时机
 */
public enum ConversationStage {
    
    /**
     * INIT阶段：ChatClient创建时
     * 适用于：提示词advisor、RAG advisor、日志advisor、可能的全局记忆advisor
     */
    INIT,
    
    /**
     * PER_REQUEST阶段：每轮对话时
     * 适用于：动态记忆advisor、工具调用advisor
     */
    PER_REQUEST
}
