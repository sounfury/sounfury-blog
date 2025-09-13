package org.sounfury.aki.application.tools.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 工具状态更新请求DTO
 */
@Data
public class ToolStateUpdateRequest {

    /**
     * 工具名称
     */
    @NotBlank(message = "工具名称不能为空")
    private String toolName;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;
}
