package org.sounfury.aki.domain.llm.tools.service;

import org.sounfury.aki.domain.llm.tools.model.ToolMetadata;

import java.util.List;
import java.util.Set;

/**
 * 工具发现服务接口
 * 负责扫描和发现系统中的工具
 */
public interface ToolDiscoveryService {

    /**
     * 发现所有可用的工具
     * @return 工具元数据列表
     */
    List<ToolMetadata> discoverAllTools();

    /**
     * 获取所有工具名称
     * @return 工具名称集合
     */
    Set<String> getAllToolNames();

    /**
     * 根据工具名称获取工具元数据
     * @param toolName 工具名称
     * @return 工具元数据，如果不存在返回null
     */
    ToolMetadata getToolMetadata(String toolName);
}
