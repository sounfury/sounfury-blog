package org.sounfury.aki.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.springframework.stereotype.Repository;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.ModelProvider;
import org.sounfury.aki.domain.llm.ModelSettings;
import org.sounfury.aki.domain.llm.repository.LlmConfigurationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

/**
 * JOOQ实现的LLM配置仓储
 * 负责值对象与数据库字段的转换
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcLlmConfigurationRepository implements LlmConfigurationRepository {
    
    private final Configuration configuration;
    
    private DSLContext ctx() {
        return configuration.dsl();
    }
    
    @Override
    public ModelConfiguration save(ModelConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("配置不能为空");
        }
        
        try {
            // 如果是新配置（id为null），执行插入
            if (config.getId() == null) {
                return insertConfiguration(config);
            } else {
                return updateConfiguration(config);
            }
        } catch (Exception e) {
            log.error("保存LLM配置失败: {}", config, e);
            throw new RuntimeException("保存LLM配置失败", e);
        }
    }
    
    @Override
    public Optional<ModelConfiguration> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        
        try {
            var record = ctx()
                    .select()
                    .from(table("model_configuration"))
                    .where(field("id").eq(id))
                    .fetchOne();
                    
            return Optional.ofNullable(record).map(this::toDomain);
        } catch (Exception e) {
            log.error("根据ID查找LLM配置失败: id={}", id, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<ModelConfiguration> findGlobalConfiguration() {
        try {
            var record = ctx()
                    .select()
                    .from(table("model_configuration"))
                    .where(field("enabled").eq(1))
                    .fetchOne();
                    
            return Optional.ofNullable(record).map(this::toDomain);
        } catch (Exception e) {
            log.error("查找全局LLM配置失败", e);
            return Optional.empty();
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            return;
        }
        
        try {
            int deleted = ctx()
                    .deleteFrom(table("model_configuration"))
                    .where(field("id").eq(id))
                    .execute();
                    
            log.debug("删除LLM配置: id={}, deleted={}", id, deleted > 0);
        } catch (Exception e) {
            log.error("删除LLM配置失败: id={}", id, e);
            throw new RuntimeException("删除LLM配置失败", e);
        }
    }
    
    @Override
    public boolean existsById(Integer id) {
        if (id == null) {
            return false;
        }
        
        try {
            return ctx()
                    .fetchExists(
                            selectOne()
                                    .from(table("model_configuration"))
                                    .where(field("id").eq(id))
                    );
        } catch (Exception e) {
            log.error("检查LLM配置存在性失败: id={}", id, e);
            return false;
        }
    }
    
    /**
     * 插入新配置
     */
    private ModelConfiguration insertConfiguration(ModelConfiguration config) {
        // 如果启用新配置，先禁用其他配置
        if (config.isEnabled()) {
            disableAllConfigurations();
        }
        
        var insertResult = ctx()
                .insertInto(table("model_configuration"))
                .columns(
                        field("provider_type"),
                        field("base_url"),
                        field("api_key"),
                        field("model_name"),
                        field("max_tokens"),
                        field("temperature"),
                        field("top_p"),
                        field("frequency_penalty"),
                        field("presence_penalty"),
                        field("stop_sequences"),
                        field("stream_enabled"),
                        field("timeout_seconds"),
                        field("retry_count"),
                        field("enabled"),
                        field("description"),
                        field("create_time"),
                        field("update_time")
                )
                .values(
                        config.getProvider().getType().name(),
                        config.getProvider().getBaseUrl(),
                        config.getProvider().getApiKey(),
                        config.getProvider().getModelName(),
                        config.getSettings().getMaxTokens(),
                        config.getSettings().getTemperature(),
                        config.getSettings().getTopP(),
                        config.getSettings().getFrequencyPenalty(),
                        config.getSettings().getPresencePenalty(),
                        config.getSettings().getStopSequences() != null ? 
                            JSON.valueOf(Arrays.toString(config.getSettings().getStopSequences())) : null,
                        config.getSettings().isStreamEnabled() ? 1 : 0,
                        config.getSettings().getTimeoutSeconds(),
                        config.getSettings().getRetryCount(),
                        config.isEnabled() ? 1 : 0,
                        config.getDescription(),
                        config.getCreatedAt(),
                        config.getUpdatedAt()
                )
                .returningResult(field("id"))
                .fetchOne();
                
        Integer generatedId = insertResult.getValue(field("id", Integer.class));
        
        return ModelConfiguration.builder()
                .id(generatedId)
                .provider(config.getProvider())
                .settings(config.getSettings())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .enabled(config.isEnabled())
                .description(config.getDescription())
                .build();
    }
    
    /**
     * 更新配置
     */
    private ModelConfiguration updateConfiguration(ModelConfiguration config) {
        // 如果启用此配置，先禁用其他配置
        if (config.isEnabled()) {
            disableOtherConfigurations(config.getId());
        }
        
        ctx()
                .update(table("model_configuration"))
                .set(field("provider_type"), config.getProvider().getType().name())
                .set(field("base_url"), config.getProvider().getBaseUrl())
                .set(field("api_key"), config.getProvider().getApiKey())
                .set(field("model_name"), config.getProvider().getModelName())
                .set(field("max_tokens"), config.getSettings().getMaxTokens())
                .set(field("temperature"), config.getSettings().getTemperature())
                .set(field("top_p"), config.getSettings().getTopP())
                .set(field("frequency_penalty"), config.getSettings().getFrequencyPenalty())
                .set(field("presence_penalty"), config.getSettings().getPresencePenalty())
                .set(field("stop_sequences"), config.getSettings().getStopSequences() != null ? 
                    JSON.valueOf(Arrays.toString(config.getSettings().getStopSequences())) : null)
                .set(field("stream_enabled"), config.getSettings().isStreamEnabled() ? 1 : 0)
                .set(field("timeout_seconds"), config.getSettings().getTimeoutSeconds())
                .set(field("retry_count"), config.getSettings().getRetryCount())
                .set(field("enabled"), config.isEnabled() ? 1 : 0)
                .set(field("description"), config.getDescription())
                .set(field("update_time"), LocalDateTime.now())
                .where(field("id").eq(config.getId()))
                .execute();
                
        return config;
    }
    
    /**
     * 禁用所有配置
     */
    private void disableAllConfigurations() {
        ctx()
                .update(table("model_configuration"))
                .set(field("enabled"), 0)
                .execute();
    }
    
    /**
     * 禁用除指定ID外的其他配置
     */
    private void disableOtherConfigurations(Integer excludeId) {
        ctx()
                .update(table("model_configuration"))
                .set(field("enabled"), 0)
                .where(field("id").ne(excludeId))
                .execute();
    }
    
    /**
     * 将数据库记录转换为域对象
     */
    private ModelConfiguration toDomain(org.jooq.Record record) {
        try {
            // 构建ModelProvider值对象
            ModelProvider provider = ModelProvider.builder()
                    .type(ModelProvider.ProviderType.valueOf(record.getValue("provider_type", String.class)))
                    .baseUrl(record.getValue("base_url", String.class))
                    .apiKey(record.getValue("api_key", String.class))
                    .modelName(record.getValue("model_name", String.class))
                    .build();
            
            // 处理stopSequences
            String[] stopSequences = null;
            String stopSeqJson = record.getValue("stop_sequences", String.class);
            if (stopSeqJson != null && !stopSeqJson.trim().isEmpty()) {
                // 简单解析JSON数组字符串，实际项目中应使用JSON库
                stopSequences = stopSeqJson.replace("[", "").replace("]", "")
                        .split(",");
            }
            
            // 构建ModelSettings值对象
            ModelSettings settings = ModelSettings.builder()
                    .maxTokens(record.getValue("max_tokens", Integer.class))
                    .temperature(record.getValue("temperature", Double.class))
                    .topP(record.getValue("top_p", Double.class))
                    .frequencyPenalty(record.getValue("frequency_penalty", Double.class))
                    .presencePenalty(record.getValue("presence_penalty", Double.class))
                    .stopSequences(stopSequences)
                    .streamEnabled(record.getValue("stream_enabled", Integer.class) == 1)
                    .timeoutSeconds(record.getValue("timeout_seconds", Integer.class))
                    .retryCount(record.getValue("retry_count", Integer.class))
                    .build();
            
            // 构建ModelConfiguration聚合根
            return ModelConfiguration.builder()
                    .id(record.getValue("id", Integer.class))
                    .provider(provider)
                    .settings(settings)
                    .createdAt(record.getValue("create_time", LocalDateTime.class))
                    .updatedAt(record.getValue("update_time", LocalDateTime.class))
                    .enabled(record.getValue("enabled", Integer.class) == 1)
                    .description(record.getValue("description", String.class))
                    .build();
                    
        } catch (Exception e) {
            log.error("转换数据库记录为域对象失败: {}", record, e);
            throw new RuntimeException("数据转换失败", e);
        }
    }
}
