package org.sounfury.aki.domain.llm;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.llm.event.ModelConfigurationChangedEvent;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LLM配置聚合根
 * 管理LLM模型的全局配置状态
 */
@Getter
@Builder
public class ModelConfiguration {
    
    /**
     * 配置ID（数据库主键）
     */
    private final Integer id;
    
    /**
     * 模型提供商信息
     */
    private final ModelProvider provider;
    
    /**
     * 模型设置
     */
    private final ModelSettings settings;
    
    /**
     * 配置创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 配置更新时间
     */
    private final LocalDateTime updatedAt;
    
    /**
     * 是否启用
     */
    private final boolean enabled;
    
    /**
     * 配置描述
     */
    private final String description;
    
    /**
     * 领域事件记录容器
     */
    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 创建被启用的配置，这里先写死（配置表中只能有一个enabled是true）
     * 使用统一的ModelProvider创建方法
     * Qwen/Qwen3-235B-A22B-Instruct-2507
     * deepseek-ai/DeepSeek-V3
     */
    public static ModelConfiguration createDefault() {
        return ModelConfiguration
                .builder()
                .id(1) // 默认配置使用ID=1
                .provider(ModelProvider.create(
                    ModelProvider.ProviderType.DEEPSEEK,
                    "https://api.siliconflow.cn",
                    "sk-nvvdrwdosspjsmvqwyzpydppglryorujwzynmxilfqumfqad",
                    "deepseek-ai/DeepSeek-V3"
                ))
                .settings(ModelSettings.createDefault())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .enabled(true)
                .description("默认LLM配置")
                .build();
    }
    
    /**
     * 创建新的LLM配置
     * @param provider 模型提供商
     * @param settings 模型设置  
     * @param description 配置描述
     * @param enabled 是否启用
     * @return 新的配置实例
     */
    public static ModelConfiguration create(ModelProvider provider, ModelSettings settings, String description, boolean enabled) {
        return ModelConfiguration
                .builder()
                .provider(provider)
                .settings(settings)
                .enabled(enabled)
                .description(description)
                .build();
    }
    
    /**
     * 更新模型提供商（浅层修改）
     * 需要完全重建ChatClient
     * @param newProvider 新的模型提供商
     * @return 更新后的配置
     */
    public ModelConfiguration updateProvider(ModelProvider newProvider) {
        if (Objects.equals(this.provider, newProvider)) {
            return this;
        }
        
        ModelConfiguration newConfiguration = ModelConfiguration
                .builder()
                .id(this.id)
                .provider(newProvider)
                .settings(this.settings)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .enabled(this.enabled)
                .description(this.description)
                .build();
        
        // 记录提供商变更事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                .configurationId(this.id)
                .oldConfiguration(this)
                .newConfiguration(newConfiguration)
                .changeTime(LocalDateTime.now())
                .changeType(ModelConfigurationChangedEvent.ChangeType.PROVIDER_CHANGED)
                .changeReason("模型提供商变更")
                .build();
        
        newConfiguration.recordEvent(event);
        return newConfiguration;
    }
    
    /**
     * 更新模型设置（深层修改）
     * 可以使用mutate()优化
     * @param newSettings 新的模型设置
     * @return 更新后的配置
     */
    public ModelConfiguration updateSettings(ModelSettings newSettings) {
        if (Objects.equals(this.settings, newSettings)) {
            return this;
        }
        
        ModelConfiguration newConfiguration = ModelConfiguration
                .builder()
                .id(this.id)
                .provider(this.provider)
                .settings(newSettings)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .enabled(this.enabled)
                .description(this.description)
                .build();
        
        // 记录设置变更事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                .configurationId(this.id)
                .oldConfiguration(this)
                .newConfiguration(newConfiguration)
                .changeTime(LocalDateTime.now())
                .changeType(ModelConfigurationChangedEvent.ChangeType.SETTINGS_CHANGED)
                .changeReason("模型设置变更")
                .build();
        
        newConfiguration.recordEvent(event);
        return newConfiguration;
    }
    
    /**
     * 启用配置（优先级最高）
     * @return 更新后的配置
     */
    public ModelConfiguration enable() {
        if (this.enabled) {
            return this;
        }
        
        ModelConfiguration newConfiguration = ModelConfiguration
                .builder()
                .id(this.id)
                .provider(this.provider)
                .settings(this.settings)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .enabled(true)
                .description(this.description)
                .build();
        
        // 记录启用事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                .configurationId(this.id)
                .oldConfiguration(this)
                .newConfiguration(newConfiguration)
                .changeTime(LocalDateTime.now())
                .changeType(ModelConfigurationChangedEvent.ChangeType.ENABLED_CHANGED)
                .changeReason("启用配置")
                .build();
        
        newConfiguration.recordEvent(event);
        return newConfiguration;
    }
    
    /**
     * 禁用配置（优先级最高）
     * @return 更新后的配置
     */
    public ModelConfiguration disable() {
        if (!this.enabled) {
            return this;
        }
        
        ModelConfiguration newConfiguration = ModelConfiguration
                .builder()
                .id(this.id)
                .provider(this.provider)
                .settings(this.settings)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .enabled(false)
                .description(this.description)
                .build();
        
        // 记录禁用事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                .configurationId(this.id)
                .oldConfiguration(this)
                .newConfiguration(newConfiguration)
                .changeTime(LocalDateTime.now())
                .changeType(ModelConfigurationChangedEvent.ChangeType.ENABLED_CHANGED)
                .changeReason("禁用配置")
                .build();
        
        newConfiguration.recordEvent(event);
        return newConfiguration;
    }
    
    /**
     * 记录领域事件
     */
    private void recordEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 获取所有领域事件
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    /**
     * 清除领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
    
    /**
     * 验证配置是否有效
     */
    public boolean isValid() {
        return provider != null && 
               provider.isValid() && 
               settings != null && 
               settings.isValid() &&
               enabled;
    }
}
