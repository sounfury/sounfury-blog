package org.sounfury.aki.domain.llm.repository;

import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationListRequest;

import java.util.List;
import java.util.Optional;

/**
 * LLM配置仓储接口
 * 定义LLM配置的持久化操作
 */
public interface LlmConfigurationRepository {
    
    /**
     * 保存配置
     * @param configuration LLM配置
     * @return 保存后的配置
     */
    ModelConfiguration save(ModelConfiguration configuration);
    
    /**
     * 根据配置ID查找配置
     * @param id 配置ID
     * @return 配置信息，如果不存在则返回空
     */
    Optional<ModelConfiguration> findConfigById(Integer id);

    /**
     * 获取当前全局配置（enabled=1的配置）
     * @return 当前全局配置
     */
    Optional<ModelConfiguration> findGlobalConfiguration();

    /**
     * 删除配置
     * @param id 配置ID
     */
    void deleteById(Integer id);

    /**
     * 检查配置是否存在
     * @param id 配置ID
     * @return true表示存在，false表示不存在
     */
    boolean existsById(Integer id);
    
    /**
     * 分页查询配置列表
     * @param request 查询请求参数
     * @return 分页结果
     */
    org.sounfury.jooq.page.PageRepDto<List<ModelConfiguration>> findConfigurationPage(LlmConfigurationListRequest request);
}
