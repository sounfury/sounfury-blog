package org.sounfury.aki.domain.llm.tools.service;

import org.springframework.ai.tool.ToolCallback;

import java.util.Set;

/**
 * 工具配置管理服务接口
 * 负责工具启用状态的管理
 */
public interface ToolConfigurationService {

    /**
     * 获取所有启用的工具名称
     * @return 启用的工具名称集合
     */
    Set<String> getEnabledTools();

    /**
     * 检查指定工具是否启用
     * @param toolName 工具名称
     * @return 是否启用
     */
    boolean isToolEnabled(String toolName);

    /**
     * 启用指定工具
     * @param toolName 工具名称
     */
    void enableTool(String toolName);

    /**
     * 禁用指定工具
     * @param toolName 工具名称
     */
    void disableTool(String toolName);

    /**
     * 批量更新工具启用状态
     * @param toolStates 工具名称和启用状态的映射
     */
    void updateToolStates(java.util.Map<String, Boolean> toolStates);

    /**
     * 初始化默认工具配置
     */
    void initializeDefaultConfiguration();

    /**
     * 获取启用的工具回调列表
     * 支持动态筛选，只返回当前启用的工具
     * @return 启用的ToolCallback数组
     */
    ToolCallback[] getEnabledToolCallbacks();
}
