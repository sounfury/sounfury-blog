package org.sounfury.aki.contracts.plan;

import lombok.Builder;
import lombok.Getter;

import org.sounfury.aki.contracts.spec.MemorySpec;
import org.sounfury.aki.contracts.spec.PromptSpec;
import org.sounfury.aki.contracts.spec.RagSpec;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;
import org.sounfury.aki.domain.llm.ModelConfiguration;

import java.util.List;


/**
 * INIT阶段计划对象
 * 用于ChatClient的创建
 */
@Getter
@Builder
public class InitPlan {

    /**
     * 系统模型配置
     */
    private final ModelConfiguration modelConfiguration;

    /**
     * 提示词规格,只有任务初始化时设置，后续请求不变
     */
    private final PromptSpec promptSpec;

    /**
     * Todo RAG规格，某些世界书是挂载到全局的，某些是挂载到角色的，这里先不实现
     */
    @Builder.Default
    private final RagSpec ragSpec = RagSpec.disabled();

    /**
     * 是否启用日志记录
     */
    @Builder.Default
    private final boolean enableLogging = true;

    /**
     * 全局记忆规格
     */
    private final List<GlobalMemory> globalMemories;

    /**
     * 角色ID（用于标识和调试）
     */
    private final String characterId;

    /**
     * 检查计划是否有效
     */
    public boolean isValid() {
        return modelConfiguration != null ;
    }


    /**
     * 创建空的INIT计划
     */
    public static InitPlan empty(String characterId) {
        return InitPlan.builder()
                .characterId(characterId)
                .promptSpec(PromptSpec.empty())
                .build();
    }
}
