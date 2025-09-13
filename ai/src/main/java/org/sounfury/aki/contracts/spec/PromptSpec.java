package org.sounfury.aki.contracts.spec;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.prompt.AssembledPrompt;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;

import java.util.List;

/**
 * 提示词规格
 * 封装提示词相关的配置
 */
@Getter
@Builder
public class PromptSpec {

    /**
     * 组装后的提示词（角色相关）
     */
    private final AssembledPrompt assembledPrompt;

    /**
     * 全局记忆列表（全局策略提示词）
     * 如"必须说中文"等系统级别的策略
     */
    @Builder.Default
    private final List<GlobalMemory> globalMemories = List.of();

    /**
     * 是否使用分离模式
     * true: 创建多个独立的advisor（系统/行为/角色/全局）
     * false: 创建单个合并的advisor
     */
    @Builder.Default
    private final boolean separatedMode = true;

    /**
     * 检查是否有有效的提示词内容
     */
    public boolean isValid() {
        return (assembledPrompt != null && !assembledPrompt.isEmpty()) || 
               (globalMemories != null && !globalMemories.isEmpty());
    }

    /**
     * 是否有全局策略提示词
     */
    public boolean hasGlobalMemories() {
        return globalMemories != null && !globalMemories.isEmpty();
    }

    /**
     * 创建空的提示词规格
     */
    public static PromptSpec empty() {
        return PromptSpec.builder()
                .assembledPrompt(AssembledPrompt.empty())
                .build();
    }
}
