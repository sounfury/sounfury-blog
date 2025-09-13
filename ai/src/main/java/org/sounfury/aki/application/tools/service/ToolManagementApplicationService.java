package org.sounfury.aki.application.tools.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.tools.dto.*;
import org.sounfury.aki.domain.llm.tools.model.ToolMetadata;
import org.sounfury.aki.domain.llm.tools.service.ToolConfigurationService;
import org.sounfury.aki.domain.llm.tools.service.ToolDiscoveryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工具管理应用服务
 * 提供工具管理的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolManagementApplicationService {

    private final ToolDiscoveryService toolDiscoveryService;
    private final ToolConfigurationService toolConfigurationService;

    /**
     * 获取所有工具列表
     * @return 工具列表响应
     */
    public ToolListResponse getAllTools() {
        try {
            List<ToolMetadata> allTools = toolDiscoveryService.discoverAllTools();
            
            List<ToolInfoResponse> toolInfos = allTools.stream()
                    .map(this::convertToToolInfoResponse)
                    .collect(Collectors.toList());
            
            int enabledCount = toolInfos.stream()
                                        .mapToInt(tool -> tool.isEnabled() ? 1 : 0)
                                        .sum();
            
            return ToolListResponse.builder()
                    .tools(toolInfos)
                    .totalCount(toolInfos.size())
                    .enabledCount(enabledCount)
                    .build();
                    
        } catch (Exception e) {
            log.error("获取工具列表失败", e);
            return ToolListResponse.builder()
                    .tools(List.of())
                    .totalCount(0)
                    .enabledCount(0)
                    .build();
        }
    }

    /**
     * 更新单个工具状态
     * @param request 工具状态更新请求
     */
    public void updateToolState(ToolStateUpdateRequest request) {
        try {
            String toolName = request.getToolName();
            boolean enabled = request.getEnabled();
            
            // 检查工具是否存在
            if (toolDiscoveryService.getToolMetadata(toolName) == null) {
                throw new IllegalArgumentException("工具不存在: " + toolName);
            }
            
            if (enabled) {
                toolConfigurationService.enableTool(toolName);
            } else {
                toolConfigurationService.disableTool(toolName);
            }
            
            log.info("工具状态更新成功: {} -> {}", toolName, enabled);
            
        } catch (Exception e) {
            log.error("更新工具状态失败: {}", request.getToolName(), e);
            throw new RuntimeException("更新工具状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量更新工具状态
     * @param request 批量工具状态更新请求
     */
    public void batchUpdateToolStates(BatchToolStateUpdateRequest request) {
        try {
            Map<String, Boolean> toolStates = request.getToolStates().stream()
                    .collect(Collectors.toMap(
                            ToolStateUpdateRequest::getToolName,
                            ToolStateUpdateRequest::getEnabled
                    ));
            
            // 验证所有工具是否存在
            for (String toolName : toolStates.keySet()) {
                if (toolDiscoveryService.getToolMetadata(toolName) == null) {
                    throw new IllegalArgumentException("工具不存在: " + toolName);
                }
            }
            
            toolConfigurationService.updateToolStates(toolStates);
            
            log.info("批量更新工具状态成功，更新数量: {}", toolStates.size());
            
        } catch (Exception e) {
            log.error("批量更新工具状态失败", e);
            throw new RuntimeException("批量更新工具状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取指定工具信息
     * @param toolName 工具名称
     * @return 工具信息响应
     */
    public ToolInfoResponse getToolInfo(String toolName) {
        try {
            ToolMetadata metadata = toolDiscoveryService.getToolMetadata(toolName);
            if (metadata == null) {
                throw new IllegalArgumentException("工具不存在: " + toolName);
            }
            
            return convertToToolInfoResponse(metadata);
            
        } catch (Exception e) {
            log.error("获取工具信息失败: {}", toolName, e);
            throw new RuntimeException("获取工具信息失败: " + e.getMessage(), e);
        }
    }


    /**
     * 转换工具元数据为响应DTO
     */
    private ToolInfoResponse convertToToolInfoResponse(ToolMetadata metadata) {
        return ToolInfoResponse.builder()
                .name(metadata.getName())
                .description(metadata.getDescription())
                .className(metadata.getClassName())
                .methodName(metadata.getMethodName())
                .enabled(metadata.isEnabled())
                .build();
    }
}
