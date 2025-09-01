package org.sounfury.aki.domain.conversation.session;

import lombok.Builder;
import lombok.Getter;

/**
 * 会话配置值对象
 * 包含会话的各种配置信息
 */
@Getter
@Builder
public class SessionMeta {
    
    /**
     * 角色ID
     */
    private final String characterId;
    
    /**
     * 对话模式
     */
    private final ConversationMode mode;


    private final SessionMemoryPolicy memoryPolicy;
    
    /**
     * 是否是站长会话
     */
    private final boolean isOwnerSession;
    
    /**
     * 是否启用记忆
     */
    private final boolean enableMemory;
    
    /**
     * 是否启用RAG
     */
    private final boolean enableRag;
    
    /**
     * 是否启用工具调用
     */
    private final boolean enableTools;

    /**
     * 统计信息：消息数量
     */
    private int messageCount = 0;

    /**
     * 统计信息：total token 数量
     */
    private long totalTokens = 0;


    /**
     * 检查是否支持工具调用
     * Agent模式且站长会话才支持工具调用
     */
    public boolean supportsToolCalling() {
        return enableTools && mode == ConversationMode.AGENT && isOwnerSession;
    }
    
    /**
     * 检查是否需要记忆
     */
    public boolean needsMemory() {
        return enableMemory && (mode == ConversationMode.AGENT || mode == ConversationMode.CHAT);
    }
    
    /**
     * 检查是否需要持久化记忆
     */
    public boolean needsPersistentMemory() {
        return isOwnerSession && needsMemory();
    }
    
    /**
     * 检查是否需要RAG
     */
    public boolean needsRag() {
        return enableRag && characterId != null && !characterId.trim().isEmpty();
    }
    
    /**
     * 验证配置是否有效
     */
    public boolean isValid() {
        return characterId != null && !characterId.trim().isEmpty()
               && mode != null;
    }
}
