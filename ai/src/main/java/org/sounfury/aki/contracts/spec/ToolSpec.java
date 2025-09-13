package org.sounfury.aki.contracts.spec;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * 工具规格
 * 封装工具调用相关的配置参数
 */
@Getter
@Builder
public class ToolSpec {

    /**
     * 是否启用工具调用
     */
    @Builder.Default
    private final boolean enabled = false;

    /**
     * 启用的工具名称集合
     */
    @Builder.Default
    private final Set<String> enabledTools = Set.of();


    /**
     * 检查工具配置是否有效
     */
    public boolean isValid() {
        return enabled && enabledTools != null && !enabledTools.isEmpty();
    }

    /**
     * 创建禁用的工具规格
     */
    public static ToolSpec disabled() {
        return ToolSpec.builder()
                .enabled(false)
                .build();
    }

    /**
     * 创建Agent模式的工具规格
     */
    public static ToolSpec agentMode(Set<String> tools) {
        return ToolSpec.builder()
                .enabled(true)
                .enabledTools(tools)
                .build();
    }
}
