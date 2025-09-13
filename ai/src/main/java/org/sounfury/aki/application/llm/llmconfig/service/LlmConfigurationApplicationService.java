package org.sounfury.aki.application.llm.llmconfig.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.llm.llmconfig.dto.CreateLlmConfigurationCommand;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationListRequest;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationListResponse;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationResponse;
import org.sounfury.aki.application.llm.llmconfig.dto.UpdateLlmConfigurationCommand;
import org.sounfury.aki.application.shared.event.DomainEventPublisher;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.ModelProvider;
import org.sounfury.aki.domain.llm.ModelSettings;
import org.sounfury.aki.domain.llm.repository.LlmConfigurationRepository;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LLM配置应用服务
 * 负责处理LLM配置相关的业务流程，包括DTO转换、事务管理和事件发布
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmConfigurationApplicationService {

    private final LlmConfigurationRepository configurationRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * 创建LLM配置
     * @param command 创建命令
     * @return 创建后的配置
     */
    @Transactional
    public ModelConfiguration createConfiguration(CreateLlmConfigurationCommand command) {
        log.info("创建LLM配置: providerType={}, modelName={}", command.getProviderType(), command.getModelName());
        
        try {
            // 构建模型提供商
            ModelProvider provider = buildModelProvider(command);
            
            // 构建模型设置
            ModelSettings settings = buildModelSettings(command);
            
            // 创建配置实例
            ModelConfiguration newConfig = ModelConfiguration.create(
                provider, 
                settings, 
                command.getDescription() != null ? command.getDescription() : "LLM配置", 
                command.getEnabled() != null ? command.getEnabled() : false
            );
            
            // 持久化
            ModelConfiguration savedConfig = configurationRepository.save(newConfig);
            
            log.info("LLM配置创建成功: id={}", savedConfig.getId());
            return savedConfig;
            
        } catch (Exception e) {
            log.error("创建LLM配置失败: providerType={}, modelName={}", command.getProviderType(), command.getModelName(), e);
            throw new RuntimeException("创建LLM配置失败", e);
        }
    }

    /**
     * 更新LLM配置
     * @param command 更新命令
     * @return 更新后的配置
     */
    @Transactional
    public ModelConfiguration updateConfiguration(UpdateLlmConfigurationCommand command) {
        log.info("更新LLM配置: configId={}", command.getConfigId());
        
        try {
            // 查找现有配置

            ModelConfiguration updatedConfig = configurationRepository
                    .findConfigById(command.getConfigId())
                    .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + command.getConfigId()));

            // 根据变更类型执行相应的业务操作
            if (command.hasProviderChanges()) {
                ModelProvider newProvider = buildModelProvider(command);
                updatedConfig = updatedConfig.updateProvider(newProvider);
                log.debug("更新模型提供商: configId={}", command.getConfigId());
            }

            if (command.hasSettingsChanges()) {
                ModelSettings newSettings = buildModelSettings(command);
                updatedConfig = updatedConfig.updateSettings(newSettings);
                log.debug("更新模型设置: configId={}", command.getConfigId());
            }

            if (command.hasEnabledChange()) {
                updatedConfig = command.getEnabled() ? 
                    updatedConfig.enable() : 
                    updatedConfig.disable();
                log.debug("更新启用状态: configId={}, enabled={}", command.getConfigId(), command.getEnabled());
            }

            // 持久化
            ModelConfiguration savedConfig = configurationRepository.save(updatedConfig);

            // 发布聚合中的领域事件
            publishDomainEvents(savedConfig);

            log.info("LLM配置更新成功: configId={}", command.getConfigId());
            return savedConfig;

        } catch (Exception e) {
            log.error("更新LLM配置失败: configId={}", command.getConfigId(), e);
            throw new RuntimeException("更新LLM配置失败", e);
        }
    }

    /**
     * 启用配置
     * @param configId 配置ID
     * @return 更新后的配置
     */
    @Transactional
    public ModelConfiguration enableConfiguration(Integer configId) {
        log.info("启用LLM配置: configId={}", configId);
        
        ModelConfiguration existingConfig = configurationRepository
                .findConfigById(configId)
                .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + configId));

        ModelConfiguration enabledConfig = existingConfig.enable();
        ModelConfiguration savedConfig = configurationRepository.save(enabledConfig);
        
        publishDomainEvents(savedConfig);
        
        log.info("LLM配置启用成功: configId={}", configId);
        return savedConfig;
    }

    /**
     * 禁用配置
     * @param configId 配置ID
     * @return 更新后的配置
     */
    @Transactional
    public ModelConfiguration disableConfiguration(Integer configId) {
        log.info("禁用LLM配置: configId={}", configId);
        
        ModelConfiguration existingConfig = configurationRepository
                .findConfigById(configId)
                .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + configId));

        ModelConfiguration disabledConfig = existingConfig.disable();
        ModelConfiguration savedConfig = configurationRepository.save(disabledConfig);
        
        publishDomainEvents(savedConfig);
        
        log.info("LLM配置禁用成功: configId={}", configId);
        return savedConfig;
    }

    /**
     * 获取全局配置
     * @return 当前启用的配置
     */
    public Optional<ModelConfiguration> getGlobalConfiguration() {
        return configurationRepository.findGlobalConfiguration();
    }

    /**
     * 根据ID获取配置
     * @param configId 配置ID
     * @return 配置信息
     */
    public Optional<ModelConfiguration> getConfigurationById(Integer configId) {
        return configurationRepository.findConfigById(configId);
    }
    
    /**
     * 分页查询配置列表
     * @param request 查询请求
     * @return 分页结果
     */
    public LlmConfigurationListResponse getConfigurationList(LlmConfigurationListRequest request) {
        log.info("分页查询LLM配置列表: page={}, size={}", request.getPage(), request.getSize());
        
        try {
            // 执行分页查询
            org.sounfury.jooq.page.PageRepDto<List<ModelConfiguration>> pageResult = 
                    configurationRepository.findConfigurationPage(request);
            
            // 转换为响应DTO
            LlmConfigurationListResponse response = LlmConfigurationListResponse.from(pageResult);
            
            log.info("分页查询LLM配置列表成功: 返回{}条记录，总计{}条", 
                    response.getConfigurations().size(), response.getTotal());
            return response;
            
        } catch (Exception e) {
            log.error("分页查询LLM配置列表失败: page={}, size={}", request.getPage(), request.getSize(), e);
            throw new RuntimeException("查询配置列表失败", e);
        }
    }

    /**
     * 构建模型提供商（更新命令）
     */
    private ModelProvider buildModelProvider(UpdateLlmConfigurationCommand command) {
        return ModelProvider.builder()
                .type(ModelProvider.ProviderType.valueOf(command.getProviderType()))
                .baseUrl(command.getBaseUrl())
                .apiKey(command.getApiKey())
                .modelName(command.getModelName())
                .build();
    }

    /**
     * 构建模型提供商（创建命令）
     */
    private ModelProvider buildModelProvider(CreateLlmConfigurationCommand command) {
        return ModelProvider.create(
                ModelProvider.ProviderType.valueOf(command.getProviderType()),
                command.getBaseUrl(),
                command.getApiKey(),
                command.getModelName()
        );
    }

    /**
     * 构建模型设置（更新命令）
     */
    private ModelSettings buildModelSettings(UpdateLlmConfigurationCommand command) {
        return ModelSettings.builder()
                .maxTokens(command.getMaxTokens())
                .temperature(command.getTemperature())
                .topP(command.getTopP())
                .frequencyPenalty(command.getFrequencyPenalty())
                .presencePenalty(command.getPresencePenalty())
                .stopSequences(command.getStopSequences())
                .streamEnabled(command.getStreamEnabled())
                .timeoutSeconds(command.getTimeoutSeconds())
                .retryCount(command.getRetryCount())
                .build();
    }

    /**
     * 构建模型设置（创建命令）
     */
    private ModelSettings buildModelSettings(CreateLlmConfigurationCommand command) {
        ModelSettings.ModelSettingsBuilder builder = ModelSettings.builder();
        
        if (command.getMaxTokens() != null) {
            builder.maxTokens(command.getMaxTokens());
        }
        if (command.getTemperature() != null) {
            builder.temperature(command.getTemperature());
        }
        if (command.getTopP() != null) {
            builder.topP(command.getTopP());
        }
        if (command.getFrequencyPenalty() != null) {
            builder.frequencyPenalty(command.getFrequencyPenalty());
        }
        if (command.getPresencePenalty() != null) {
            builder.presencePenalty(command.getPresencePenalty());
        }
        if (command.getStopSequences() != null) {
            builder.stopSequences(command.getStopSequences());
        }
        if (command.getStreamEnabled() != null) {
            builder.streamEnabled(command.getStreamEnabled());
        }
        if (command.getTimeoutSeconds() != null) {
            builder.timeoutSeconds(command.getTimeoutSeconds());
        }
        if (command.getRetryCount() != null) {
            builder.retryCount(command.getRetryCount());
        }
        
        return builder.build();
    }

    /**
     * 发布聚合中的领域事件
     */
    private void publishDomainEvents(ModelConfiguration config) {
        try {
            for (DomainEvent event : config.getDomainEvents()) {
                eventPublisher.publish(event);
                log.debug("发布领域事件: eventType={}, configId={}", 
                         event.getEventType(), 
                         config.getId());
            }
            // 清除已发布的事件
            config.clearDomainEvents();
        } catch (Exception e) {
            log.error("发布领域事件失败: configId={}", config.getId(), e);
            throw new RuntimeException("发布领域事件失败", e);
        }
    }
}
