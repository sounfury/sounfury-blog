package org.sounfury.aki.domain.prompt.context;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.prompt.contract.TemplateAlias;

/**
 * 提示词渲染上下文
 * 包含模板渲染所需的所有数据对象
 */
@Getter
@Builder
public class PromptContext {
    
    /**
     * 用户上下文
     */
    private final UserCtx user;
    
    /**
     * 角色上下文
     * {{char.xxx}} 模板占位符使用
     */
    @TemplateAlias("char")
    private final CharCtx charCtx;
    
    /**
     * 任务上下文
     */
    private final TaskCtx task;

    
    /**
     * 创建空的上下文
     */
    public static PromptContext empty() {
        return PromptContext.builder().build();
    }

}
