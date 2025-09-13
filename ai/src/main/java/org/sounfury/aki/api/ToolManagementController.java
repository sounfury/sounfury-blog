package org.sounfury.aki.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.tools.dto.*;
import org.sounfury.aki.application.tools.service.ToolManagementApplicationService;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 工具管理控制器
 * 提供工具管理的REST API接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/tools")
public class ToolManagementController {

    private final ToolManagementApplicationService toolManagementApplicationService;

    /**
     * 获取所有工具列表
     * @return 工具列表响应
     */
    @GetMapping
    public Result<ToolListResponse> getAllTools() {
        log.debug("获取所有工具列表");
        return Results.success(toolManagementApplicationService.getAllTools());
    }

    /**
     * 获取指定工具信息
     * @param toolName 工具名称
     * @return 工具信息响应
     */
    @GetMapping("/{toolName}")
    public Result<ToolInfoResponse> getToolInfo(@PathVariable String toolName) {
        log.debug("获取工具信息: {}", toolName);
        return Results.success(toolManagementApplicationService.getToolInfo(toolName));
    }

    /**
     * 更新单个工具状态
     * @param request 工具状态更新请求
     */
    @PutMapping("/state")
    public void updateToolState(@Valid @RequestBody ToolStateUpdateRequest request) {
        log.info("更新工具状态: {} -> {}", request.getToolName(), request.getEnabled());
        toolManagementApplicationService.updateToolState(request);
    }

    /**
     * 批量更新工具状态
     * @param request 批量工具状态更新请求
     */
    @PutMapping("/states/batch")
    public void batchUpdateToolStates(@Valid @RequestBody BatchToolStateUpdateRequest request) {
        log.info("批量更新工具状态，数量: {}", request.getToolStates().size());
        toolManagementApplicationService.batchUpdateToolStates(request);
    }

    /**
     * 启用指定工具
     * @param toolName 工具名称
     */
    @PutMapping("/{toolName}/enable")
    public void enableTool(@PathVariable String toolName) {
        log.info("启用工具: {}", toolName);
        ToolStateUpdateRequest request = new ToolStateUpdateRequest();
        request.setToolName(toolName);
        request.setEnabled(true);
        toolManagementApplicationService.updateToolState(request);
    }

    /**
     * 禁用指定工具
     * @param toolName 工具名称
     */
    @PutMapping("/{toolName}/disable")
    public void disableTool(@PathVariable String toolName) {
        log.info("禁用工具: {}", toolName);
        ToolStateUpdateRequest request = new ToolStateUpdateRequest();
        request.setToolName(toolName);
        request.setEnabled(false);
        toolManagementApplicationService.updateToolState(request);
    }

}
