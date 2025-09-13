package org.sounfury.aki.application.llm.llmconfig.dto;

import lombok.Builder;
import lombok.Data;
import org.sounfury.aki.domain.llm.ModelConfiguration;

import java.time.LocalDateTime;

/**
 * LLM配置响应DTO
 * 用于API返回，隐藏敏感信息如API密钥
 */
@Data
@Builder
public class LlmConfigurationResponse {
    
    /**
     * 配置ID
     */
    private Integer id;
    
    /**
     * 提供商信息
     */
    private ProviderInfo provider;
    
    /**
     * 模型设置
     */
    private SettingsInfo settings;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 提供商信息内嵌类
     */
    @Data
    @Builder
    public static class ProviderInfo {
        /**
         * 提供商类型
         */
        private String type;
        
        /**
         * 提供商显示名称
         */
        private String displayName;
        
        /**
         * 提供商描述
         */
        private String description;
        
        /**
         * API基础URL
         */
        private String baseUrl;
        
        /**
         * 脱敏的API密钥
         */
        private String maskedApiKey;
        
        /**
         * 模型名称
         */
        private String modelName;
        
        /**
         * 是否支持工具调用
         */
        private Boolean supportsFunctionCall;
        
        /**
         * 是否支持多模态
         */
        private Boolean supportsMultimodal;
        
        /**
         * 是否适合Agent模式
         */
        private Boolean suitableForAgent;
    }
    
    /**
     * 设置信息内嵌类
     */
    @Data
    @Builder
    public static class SettingsInfo {
        /**
         * 最大Token数
         */
        private Integer maxTokens;
        
        /**
         * 温度参数
         */
        private Double temperature;
        
        /**
         * Top-P参数
         */
        private Double topP;
        
        /**
         * 频率惩罚
         */
        private Double frequencyPenalty;
        
        /**
         * 存在惩罚
         */
        private Double presencePenalty;
        
        /**
         * 停止序列
         */
        private String[] stopSequences;
        
        /**
         * 是否启用流式输出
         */
        private Boolean streamEnabled;
        
        /**
         * 超时时间（秒）
         */
        private Integer timeoutSeconds;
        
        /**
         * 重试次数
         */
        private Integer retryCount;
    }
    
    /**
     * 从领域对象转换为响应DTO
     * @param config 领域配置对象
     * @return 响应DTO
     */
    public static LlmConfigurationResponse fromDomain(ModelConfiguration config) {
        if (config == null) {
            return null;
        }
        
        // 构建提供商信息
        ProviderInfo providerInfo = ProviderInfo.builder()
                .type(config.getProvider().getType().name())
                .displayName(config.getProvider().getDisplayName())
                .description(config.getProvider().getDescription())
                .baseUrl(config.getProvider().getBaseUrl())
                .maskedApiKey(config.getProvider().getMaskedApiKey())
                .modelName(config.getProvider().getModelName())
                .supportsFunctionCall(config.getProvider().supportsFunctionCall())
                .supportsMultimodal(config.getProvider().supportsMultimodal())
                .suitableForAgent(config.getProvider().isSuitableForAgent())
                .build();
        
        // 构建设置信息
        SettingsInfo settingsInfo = SettingsInfo.builder()
                .maxTokens(config.getSettings().getMaxTokens())
                .temperature(config.getSettings().getTemperature())
                .topP(config.getSettings().getTopP())
                .frequencyPenalty(config.getSettings().getFrequencyPenalty())
                .presencePenalty(config.getSettings().getPresencePenalty())
                .stopSequences(config.getSettings().getStopSequences())
                .streamEnabled(config.getSettings().isStreamEnabled())
                .timeoutSeconds(config.getSettings().getTimeoutSeconds())
                .retryCount(config.getSettings().getRetryCount())
                .build();
        
        return LlmConfigurationResponse.builder()
                .id(config.getId())
                .provider(providerInfo)
                .settings(settingsInfo)
                .enabled(config.isEnabled())
                .description(config.getDescription())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
