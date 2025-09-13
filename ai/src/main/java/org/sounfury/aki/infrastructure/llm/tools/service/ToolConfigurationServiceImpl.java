package org.sounfury.aki.infrastructure.llm.tools.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.tools.service.ToolConfigurationService;
import org.sounfury.aki.infrastructure.llm.tools.BlogTools;
import org.sounfury.aki.infrastructure.llm.tools.CommonTools;
import org.sounfury.utils.RedisUtils;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工具配置管理服务实现
 * 基于Redis存储工具启用状态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolConfigurationServiceImpl implements ToolConfigurationService {

    private static final String TOOL_CONFIG_KEY = "tools:config:global";
    private final BlogTools blogTools;
    private final CommonTools commonTools;

    @Override
    public Set<String> getEnabledTools() {
        try {
            Map<String, Boolean> configMap = RedisUtils.getCacheMap(TOOL_CONFIG_KEY);
            return configMap.entrySet().stream()
                    .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取启用工具列表失败", e);
            return Set.of();
        }
    }

    @Override
    public boolean isToolEnabled(String toolName) {
        try {
            Boolean enabled = RedisUtils.getCacheMapValue(TOOL_CONFIG_KEY, toolName);
            return Boolean.TRUE.equals(enabled);
        } catch (Exception e) {
            log.error("检查工具启用状态失败: toolName={}", toolName, e);
            return false;
        }
    }

    @Override
    public void enableTool(String toolName) {
        try {
            RedisUtils.setCacheMapValue(TOOL_CONFIG_KEY, toolName, true);
            log.info("工具已启用: {}", toolName);
        } catch (Exception e) {
            log.error("启用工具失败: toolName={}", toolName, e);
        }
    }

    @Override
    public void disableTool(String toolName) {
        try {
            RedisUtils.setCacheMapValue(TOOL_CONFIG_KEY, toolName, false);
            log.info("工具已禁用: {}", toolName);
        } catch (Exception e) {
            log.error("禁用工具失败: toolName={}", toolName, e);
        }
    }

    @Override
    public void updateToolStates(Map<String, Boolean> toolStates) {
        try {
            RedisUtils.setCacheMap(TOOL_CONFIG_KEY, toolStates);
            log.info("批量更新工具状态完成, 更新数量: {}", toolStates.size());
        } catch (Exception e) {
            log.error("批量更新工具状态失败", e);
        }
    }

    @Override
    public void initializeDefaultConfiguration() {
        try {
            // 检查是否已有配置
            if (RedisUtils.hasKey(TOOL_CONFIG_KEY)) {
                log.debug("工具配置已存在，跳过初始化");
                return;
            }

            // 使用ToolCallbacks从BlogTools和CommonTools获取所有工具
            ToolCallback[] blogToolCallbacks = ToolCallbacks.from(blogTools);
            ToolCallback[] commonToolCallbacks = ToolCallbacks.from(commonTools);
            
            ToolCallback[] allToolCallbacks = new ToolCallback[blogToolCallbacks.length + commonToolCallbacks.length];
            System.arraycopy(blogToolCallbacks, 0, allToolCallbacks, 0, blogToolCallbacks.length);
            System.arraycopy(commonToolCallbacks, 0, allToolCallbacks, blogToolCallbacks.length, commonToolCallbacks.length);
            
            log.info("从BlogTools和CommonTools扫描到{}个工具", allToolCallbacks.length);
            
            Map<String, Boolean> defaultConfig = Arrays.stream(allToolCallbacks)
                    .collect(Collectors.toMap(
                            tc -> tc.getToolDefinition().name(), // key：工具名
                            tc -> Boolean.TRUE                   // 值：默认启用
                    ));

            if (!defaultConfig.isEmpty()) {
                RedisUtils.setCacheMap(TOOL_CONFIG_KEY, defaultConfig);
                log.info("工具默认配置初始化完成，发现{}个工具: {}", defaultConfig.size(), defaultConfig.keySet());
            } else {
                log.warn("未发现任何工具，跳过初始化");
            }

        } catch (Exception e) {
            log.error("初始化默认工具配置失败", e);
        }
    }

    /**
     * 获取启用的工具回调列表
     * 支持动态筛选，只返回当前启用的工具
     * @return 启用的ToolCallback数组
     */

    public ToolCallback[] getEnabledToolCallbacks() {
        try {
            // 获取所有工具
            ToolCallback[] blogToolCallbacks = ToolCallbacks.from(blogTools);
            ToolCallback[] commonToolCallbacks = ToolCallbacks.from(commonTools);
            
            ToolCallback[] allToolCallbacks = new ToolCallback[blogToolCallbacks.length + commonToolCallbacks.length];
            System.arraycopy(blogToolCallbacks, 0, allToolCallbacks, 0, blogToolCallbacks.length);
            System.arraycopy(commonToolCallbacks, 0, allToolCallbacks, blogToolCallbacks.length, commonToolCallbacks.length);
            
            // 获取启用的工具名称集合
            Set<String> enabledToolNames = getEnabledTools();
            
            // 动态筛选：只保留启用的工具
            ToolCallback[] enabledCallbacks = Arrays.stream(allToolCallbacks)
                    .filter(tc -> enabledToolNames.contains(tc.getToolDefinition().name()))
                    .toArray(ToolCallback[]::new);
                    
            log.debug("获取到{}个启用的工具回调，总共{}个工具", enabledCallbacks.length, allToolCallbacks.length);
            return enabledCallbacks;
            
        } catch (Exception e) {
            log.error("获取启用工具回调失败", e);
            return new ToolCallback[0];
        }
    }
}
