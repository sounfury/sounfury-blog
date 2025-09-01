package org.sounfury.aki.domain.llm.service;

import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.ModelProvider;
import org.sounfury.aki.domain.llm.ModelSettings;

/**
 * LLM配置管理器接口
 * 领域层只关注配置的业务逻辑，不涉及ChatClient创建
 */
public interface LlmConfigurationManager {

    /**
     * 获取当前活跃的配置
     * @return 当前启用的LLM配置
     */
    ModelConfiguration getCurrentConfiguration();

    /**
     * 切换到指定配置
     * @param configurationId 配置ID
     * @return 切换后的配置
     */
    ModelConfiguration switchToConfiguration(String configurationId);

    /**
     * 更新配置
     * @param configurationId 配置ID
     * @param newProvider 新的提供商信息
     * @param newSettings 新的设置
     * @return 更新后的配置
     */
    ModelConfiguration updateConfiguration(String configurationId,
                                           ModelProvider newProvider,
                                           ModelSettings newSettings);

    /**
     * 启用配置
     * @param configurationId 配置ID
     * @return 启用后的配置
     */
    ModelConfiguration enableConfiguration(String configurationId);

    /**
     * 禁用配置
     * @param configurationId 配置ID
     * @return 禁用后的配置
     */
    ModelConfiguration disableConfiguration(String configurationId);
}
