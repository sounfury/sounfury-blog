package org.sounfury.aki.domain.prompt.context;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 任务上下文
 * 用于模板中的 {{task.xxx}} 占位符
 */
@Getter
@Builder
public class TaskCtx {
    
    /**
     * 任务输入内容
     * 模板占位符: {{task.input}}
     */
    private final String input;
    
    /**
     * 任务代码
     * 模板占位符: {{task.code}}
     */
    private final String code;
    
    /**
     * 任务类型
     * 模板占位符: {{task.type}}
     */
    private final String type;
    
    /**
     * 任务元数据（可选）
     * 模板占位符: {{task.meta.xxx}}
     */
    private final Map<String, Object> meta;
    
    /**
     * 创建任务上下文
     */
    public static TaskCtx of(String input, String code) {
        return TaskCtx.builder()
                .input(input)
                .code(code)
                .build();
    }
    
    /**
     * 创建完整的任务上下文
     */
    public static TaskCtx of(String input, String code, String type, Map<String, Object> meta) {
        return TaskCtx.builder()
                .input(input)
                .code(code)
                .type(type)
                .meta(meta)
                .build();
    }
    
    /**
     * 创建空的任务上下文
     */
    public static TaskCtx empty() {
        return TaskCtx.builder().build();
    }
    
    /**
     * 检查是否有有效的任务数据
     */
    public boolean hasValidData() {
        return input != null && !input.trim().isEmpty();
    }
}
