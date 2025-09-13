package org.sounfury.aki.application.tools.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 工具信息响应DTO
 */
@Data
@Builder
public class ToolInfoResponse {

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 工具类名
     */
    private String className;

    /**
     * 工具方法名
     */
    private String methodName;

    /**
     * 是否启用
     */
    private boolean enabled;
}
