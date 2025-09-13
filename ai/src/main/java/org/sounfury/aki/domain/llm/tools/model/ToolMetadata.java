package org.sounfury.aki.domain.llm.tools.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 工具元数据
 * 封装工具的基本信息
 */
@Getter
@Builder
public class ToolMetadata {

    /**
     * 工具名称
     */
    private final String name;

    /**
     * 工具描述
     */
    private final String description;

    /**
     * 工具类名
     */
    private final String className;

    /**
     * 工具方法名
     */
    private final String methodName;

    /**
     * 是否启用
     */
    private final boolean enabled;
}
