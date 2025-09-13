package org.sounfury.aki.contracts.plan;

import lombok.Builder;
import lombok.Getter;

import org.sounfury.aki.contracts.spec.MemorySpec;
import org.sounfury.aki.contracts.spec.PromptSpec;

/**
 * PER_REQUEST阶段计划对象
 * 简化版本，用于每轮对话时的配置
 */
@Getter
@Builder
public class RequestPlan {
    @Override
    public String toString() {
        return "RequestPlan{" +
                "sessionId='" + sessionId + '\'' +
                ", promptSpec=" + promptSpec +
                ", characterId='" + characterId + '\'' +
                ", memorySpec=" + memorySpec +
                ", enableTools=" + enableTools +
                '}';
    }

    /**
     * 会话ID
     */
    private final String sessionId;

    /**
     * 提示词规格
     */
    private final PromptSpec promptSpec;

    /**
     * 角色ID
     */
    private final String characterId;


    /**
     * 记忆规格
     */
    private final MemorySpec memorySpec;

    /**
     * 是否启用工具（简化为布尔值）
     */
    @Builder.Default
    private final boolean enableTools = false;

    /**
     * 检查计划是否有效
     */
    public boolean isValid() {
        return sessionId != null && !sessionId.trim().isEmpty();
    }

    /**
     * 是否需要记忆advisor
     */
    public boolean needsMemory() {
        return memorySpec != null && memorySpec.isValid();
    }

    /**
     * 创建简单的请求计划
     */
    public static RequestPlan create(String sessionId, MemorySpec memorySpec, boolean enableTools,String characterId) {
        //guest_1f08eeae-6859-6d1e-9fd7-b942a76d3057或者

        return RequestPlan.builder()
                .sessionId(sessionId)
                .characterId(characterId)
                .memorySpec(memorySpec)
                .enableTools(enableTools)
                .build();
    }
}
