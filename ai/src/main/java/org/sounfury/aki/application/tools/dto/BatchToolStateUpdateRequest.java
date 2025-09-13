package org.sounfury.aki.application.tools.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量工具状态更新请求DTO
 */
@Data
public class BatchToolStateUpdateRequest {

    /**
     * 工具状态更新列表
     */
    @NotEmpty(message = "工具状态更新列表不能为空")
    @Valid
    private List<ToolStateUpdateRequest> toolStates;
}
