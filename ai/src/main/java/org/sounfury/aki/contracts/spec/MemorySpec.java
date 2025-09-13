package org.sounfury.aki.contracts.spec;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.contracts.constant.MemoryType;

/**
 * 记忆规格
 * 封装记忆相关的配置参数
 */
@Getter
@Builder
public class MemorySpec {

    /**
     * 记忆类型
     */
    private final MemoryType type;

    /**
     * 消息窗口大小（保留最近N条消息）
     */
    @Builder.Default
    private final int windowSize = 10;

    @Override
    public String toString() {
        return "MemorySpec{" +
                "type=" + type +
                ", windowSize=" + windowSize +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }

    /**
     * 会话ID（用于持久化记忆的标识）
     */
    private final String conversationId;

    /**
     * 检查记忆配置是否有效
     */
    public boolean isValid() {
        if (type == null) {
            return false;
        }
        
        // 持久化记忆需要conversationId
        if (type == MemoryType.PERSISTENT) {
            return conversationId != null && !conversationId.trim().isEmpty();
        }
        
        // 会话内存不需要conversationId
        return true;
    }

    /**
     * 创建禁用的记忆规格
     */
    public static MemorySpec disabled() {
        return MemorySpec.builder()
                .type(null)
                .build();
    }

    /**
     * 创建会话内存规格
     */
    public static MemorySpec sessionOnly(int windowSize) {
        return MemorySpec.builder()
                .type(MemoryType.SESSION_ONLY)
                .windowSize(windowSize)
                .build();
    }

    /**
     * 创建持久化记忆规格
     */
    public static MemorySpec persistent(String conversationId, int windowSize) {
        return MemorySpec.builder()
                .type(MemoryType.PERSISTENT)
                .conversationId(conversationId)
                .windowSize(windowSize)
                .build();
    }
}
