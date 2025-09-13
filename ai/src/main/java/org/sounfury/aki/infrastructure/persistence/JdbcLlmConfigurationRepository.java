package org.sounfury.aki.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.Condition;
import org.jooq.JSON;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationListRequest;
import org.sounfury.aki.jooq.tables.daos.ModelConfigurationDao;
import org.sounfury.aki.jooq.tables.pojos.ModelConfigurationPojo;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.springframework.stereotype.Repository;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.ModelProvider;
import org.sounfury.aki.domain.llm.ModelSettings;
import org.sounfury.aki.domain.llm.repository.LlmConfigurationRepository;

import org.sounfury.core.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.sounfury.aki.jooq.tables.ModelConfiguration.MODEL_CONFIGURATION;

/**
 * JOOQ实现的LLM配置仓储
 * 负责值对象与数据库字段的转换
 */
@Slf4j
@Repository
public class JdbcLlmConfigurationRepository extends ModelConfigurationDao implements LlmConfigurationRepository {

    public JdbcLlmConfigurationRepository(Configuration configuration, ObjectMapper objectMapper) {
        super(configuration);
        this.mapper = objectMapper;
    }

    private final ObjectMapper mapper ;

    @Override
    public ModelConfiguration save(ModelConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("配置不能为空");
        }
        
        try {
            ModelConfigurationPojo pojo = fromDomain(config);
            boolean isNewConfig = config.getId() == null || !existsById(config.getId());
            
            if (isNewConfig) {
                // 如果启用新配置，先禁用其他配置
                if (config.isEnabled()) {
                    disableAllConfigurations();
                }
                insert(pojo);
                log.debug("插入LLM配置: id={}", config.getId());
            } else {
                // 如果启用此配置，先禁用其他配置
                if (config.isEnabled()) {
                    disableOtherConfigurations(config.getId());
                }
                update(pojo);
                log.debug("更新LLM配置: id={}", config.getId());
            }
            
            return config;
        } catch (Exception e) {
            log.error("保存LLM配置失败: id={}", config.getId(), e);
            throw new RuntimeException("保存LLM配置失败", e);
        }
    }
    
    @Override
    public Optional<ModelConfiguration> findConfigById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        
        try {
            ModelConfigurationPojo pojo = fetchOneById(id);
            return Optional.ofNullable(pojo).map(this::toDomain);
        } catch (Exception e) {
            log.error("根据ID查找LLM配置失败: id={}", id, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<ModelConfiguration> findGlobalConfiguration() {
        try {
            ModelConfigurationPojo pojo = ctx()
                    .selectFrom(MODEL_CONFIGURATION)
                    .where(MODEL_CONFIGURATION.ENABLED.eq((byte) 1))
                    .fetchOneInto(ModelConfigurationPojo.class);
                    
            return Optional.ofNullable(pojo).map(this::toDomain);
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
            super.deleteById(id);
            log.debug("删除LLM配置: id={}", id);
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
                            ctx().selectOne()
                                    .from(MODEL_CONFIGURATION)
                                    .where(MODEL_CONFIGURATION.ID.eq(id))
                    );
        } catch (Exception e) {
            log.error("检查LLM配置存在性失败: id={}", id, e);
            return false;
        }
    }
    
    @Override
    public PageRepDto<List<ModelConfiguration>> findConfigurationPage(LlmConfigurationListRequest request) {
        if (request == null) {
            return PageRepDto.empty();
        }
        
        try {
            log.debug("分页查询LLM配置列表: page={}, size={}", request.getPage(), request.getSize());
            
            // 构建基础查询
            SelectJoinStep<Record> query = ctx()
                    .select(MODEL_CONFIGURATION.fields())
                    .from(MODEL_CONFIGURATION);
            
            // 添加查询条件
            SelectConditionStep<Record> whereQuery = query.where(DSL.trueCondition());
            
            // 提供商类型过滤
            if (request.hasProviderTypeFilter()) {
                whereQuery = whereQuery.and(MODEL_CONFIGURATION.PROVIDER_TYPE.eq(request.getCleanProviderType()));
            }
            
            // 启用状态过滤
            if (request.hasEnabledFilter()) {
                byte enabledValue = request.getEnabled() ? (byte) 1 : (byte) 0;
                whereQuery = whereQuery.and(MODEL_CONFIGURATION.ENABLED.eq(enabledValue));
            }
            
            // 模型名称关键字搜索
            if (request.hasModelNameSearch()) {
                String keyword = "%" + request.getCleanModelName() + "%";
                whereQuery = whereQuery.and(MODEL_CONFIGURATION.MODEL_NAME.like(keyword));
            }
            
            // 描述关键字搜索
            if (request.hasDescriptionSearch()) {
                String keyword = "%" + request.getCleanDescription() + "%";
                whereQuery = whereQuery.and(MODEL_CONFIGURATION.DESCRIPTION.like(keyword));
            }
            
            // 执行分页查询
            PageRepDto<List<ModelConfigurationPojo>> pageResult = JooqPageHelper.getPage(
                    whereQuery,
                    request.toPageReqDto(),
                    ctx(),
                    ModelConfigurationPojo.class
            );
            
            log.debug("分页查询LLM配置POJO成功: 返回{}条记录", pageResult.getData().size());
            
            // 转换为领域对象
            List<ModelConfiguration> configurations = pageResult.getData().stream()
                    .map(this::toDomain)
                    .filter(config -> config != null)
                    .collect(Collectors.toList());
            
            log.debug("分页查询LLM配置成功: 返回{}条记录，总计{}条", configurations.size(), pageResult.getTotal());
            return new PageRepDto<>(pageResult.getTotal(), configurations);
            
        } catch (Exception e) {
            log.error("分页查询LLM配置失败", e);
            return PageRepDto.empty();
        }
    }
    
    /**
     * 禁用所有配置
     */
    private void disableAllConfigurations() {
        try {
            ctx()
                    .update(MODEL_CONFIGURATION)
                    .set(MODEL_CONFIGURATION.ENABLED, (byte) 0)
                    .execute();
        } catch (Exception e) {
            log.error("禁用所有LLM配置失败", e);
        }
    }
    
    /**
     * 禁用除指定ID外的其他配置
     */
    private void disableOtherConfigurations(Integer excludeId) {
        try {
            ctx()
                    .update(MODEL_CONFIGURATION)
                    .set(MODEL_CONFIGURATION.ENABLED, (byte) 0)
                    .where(MODEL_CONFIGURATION.ID.ne(excludeId))
                    .execute();
        } catch (Exception e) {
            log.error("禁用其他LLM配置失败: excludeId={}", excludeId, e);
        }
    }
    

    
    /**
     * 将数据库POJO转换为领域对象
     */
    private ModelConfiguration toDomain(ModelConfigurationPojo pojo) {
        if (pojo == null) {
            return null;
        }
        
        try {
            // 构建ModelProvider值对象
            ModelProvider provider = ModelProvider.builder()
                    .type(ModelProvider.ProviderType.valueOf(pojo.getProviderType().toUpperCase()))
                    .baseUrl(pojo.getBaseUrl())
                    .apiKey(pojo.getApiKey())
                    .modelName(pojo.getModelName())
                    .build();
            
            // 处理stopSequences JSON
            String[] stopSequences = null;
            if (pojo.getStopSequences() != null && !pojo.getStopSequences().data().trim().isEmpty()) {
                try {
                    stopSequences = JsonUtils.parseObject(pojo.getStopSequences().data(), String[].class);
                } catch (Exception e) {
                    log.warn("解析stopSequences失败，使用null: {}", pojo.getStopSequences(), e);
                }
            }
            
            // 构建ModelSettings值对象
            ModelSettings settings = ModelSettings.builder()
                    .maxTokens(pojo.getMaxTokens())
                    .temperature(pojo.getTemperature().doubleValue())
                    .topP(pojo.getTopP().doubleValue())
                    .frequencyPenalty(pojo.getFrequencyPenalty().doubleValue())
                    .presencePenalty(pojo.getPresencePenalty().doubleValue())
                    .stopSequences(stopSequences)
                    .streamEnabled(pojo.getStreamEnabled() != null && pojo.getStreamEnabled() == 1)
                    .timeoutSeconds(pojo.getTimeoutSeconds())
                    .retryCount(pojo.getRetryCount())
                    .build();
            
            // 构建ModelConfiguration聚合根
            return ModelConfiguration.builder()
                    .id(pojo.getId())
                    .provider(provider)
                    .settings(settings)
                    .createdAt(pojo.getCreateTime())
                    .updatedAt(pojo.getUpdateTime())
                    .enabled(pojo.getEnabled() != null && pojo.getEnabled() == 1)
                    .description(pojo.getDescription())
                    .build();
                    
        } catch (Exception e) {
            log.error("转换ModelConfigurationPojo为领域对象失败: id={}", pojo.getId(), e);
            return null;
        }
    }
    
    /**
     * 将领域对象转换为数据库POJO
     */
    private ModelConfigurationPojo fromDomain(ModelConfiguration config) {
        if (config == null) {
            return null;
        }
        
        ModelConfigurationPojo pojo = new ModelConfigurationPojo();
        pojo.setId(config.getId());
        pojo.setProviderType(config.getProvider().getType().name());
        pojo.setBaseUrl(config.getProvider().getBaseUrl());
        pojo.setApiKey(config.getProvider().getApiKey());
        pojo.setModelName(config.getProvider().getModelName());
        pojo.setMaxTokens(config.getSettings().getMaxTokens());
        pojo.setTemperature(BigDecimal.valueOf(config.getSettings().getTemperature()));
        pojo.setTopP(BigDecimal.valueOf(config.getSettings().getTopP()));
        pojo.setFrequencyPenalty(BigDecimal.valueOf(config.getSettings().getFrequencyPenalty()));
        pojo.setPresencePenalty(BigDecimal.valueOf(config.getSettings().getPresencePenalty()));
        
        // 序列化stopSequences为JSON
        if (config.getSettings().getStopSequences() != null) {
            try {
                String[] stopSequences = config.getSettings().getStopSequences();
                String jsonArray = mapper.writeValueAsString(stopSequences);
                pojo.setStopSequences(JSON.json(jsonArray));
            } catch (Exception e) {
                log.warn("序列化stopSequences失败，使用null", e);
                pojo.setStopSequences(null);
            }
        }
        
        pojo.setStreamEnabled(config.getSettings().isStreamEnabled() ? (byte) 1 : (byte) 0);
        pojo.setTimeoutSeconds(config.getSettings().getTimeoutSeconds());
        pojo.setRetryCount(config.getSettings().getRetryCount());
        pojo.setEnabled(config.isEnabled() ? (byte) 1 : (byte) 0);
        pojo.setDescription(config.getDescription());
        pojo.setCreateTime(config.getCreatedAt());
        pojo.setUpdateTime(config.getUpdatedAt() != null ? config.getUpdatedAt() : LocalDateTime.now());
        
        return pojo;
    }
}
