package org.sounfury.aki.domain.llm;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.llm.event.ModelConfigurationChangedEvent;

import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
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
     * 更新配置
     * @param newProvider 新的模型提供商
     * @param newSettings 新的模型设置
     * @param eventPublisher 事件发布器
     * @return 更新后的配置
     */
    public ModelConfiguration updateConfiguration(
            ModelProvider newProvider, 
            ModelSettings newSettings,
            ApplicationEventPublisher eventPublisher) {
        
        // 检查是否有实际变更
        boolean hasChanges = !Objects.equals(this.provider, newProvider) || 
                           !Objects.equals(this.settings, newSettings);
        
        if (!hasChanges) {
            return this;
        }
        
        // 创建新的配置实例
        ModelConfiguration newConfiguration = ModelConfiguration
                .builder()
                .id(this.id)
                .provider(newProvider)
                .settings(newSettings)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .enabled(this.enabled)
                .description(this.description)
                .build();
        
        // 发布配置变更事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                                                                             .configurationId(this.id)
                                                                             .oldConfiguration(this)
                                                                             .newConfiguration(newConfiguration)
                                                                             .changeTime(LocalDateTime.now())
                                                                             .build();
        
        eventPublisher.publishEvent(event);
        
        return newConfiguration;
    }
    
    /**
     * 启用配置
     */
    public ModelConfiguration enable(ApplicationEventPublisher eventPublisher) {
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
        
        // 发布启用事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                .configurationId(this.id)
                .oldConfiguration(this)
                .newConfiguration(newConfiguration)
                .changeTime(LocalDateTime.now())
                .build();
        
        eventPublisher.publishEvent(event);
        
        return newConfiguration;
    }
    
    /**
     * 禁用配置
     */
    public ModelConfiguration disable(ApplicationEventPublisher eventPublisher) {
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
        
        // 发布禁用事件
        ModelConfigurationChangedEvent event = ModelConfigurationChangedEvent.builder()
                .configurationId(this.id)
                .oldConfiguration(this)
                .newConfiguration(newConfiguration)
                .changeTime(LocalDateTime.now())
                .build();
        
        eventPublisher.publishEvent(event);
        
        return newConfiguration;
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
