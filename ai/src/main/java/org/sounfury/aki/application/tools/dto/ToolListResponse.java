package org.sounfury.aki.application.tools.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 工具列表响应DTO
 */
@Data
@Builder
public class ToolListResponse {

    /**
     * 工具列表
     */
    private List<ToolInfoResponse> tools;

    /**
     * 总数量
     */
    private int totalCount;

    /**
     * 启用数量
     */
    private int enabledCount;
}
