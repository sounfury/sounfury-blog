package org.sounfury.aki.application.llm.llmconfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 创建LLM配置命令
 */
@Data
public class CreateLlmConfigurationCommand {
    
    /**
     * 提供商类型
     */
    @NotBlank(message = "提供商类型不能为空")
    private String providerType;
    
    /**
     * API基础URL
     */
    @NotBlank(message = "API基础URL不能为空")
    private String baseUrl;
    
    /**
     * API密钥
     */
    @NotBlank(message = "API密钥不能为空")
    private String apiKey;
    
    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空")
    private String modelName;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 最大Token数
     */
    @Min(value = 1, message = "最大Token数必须大于0")
    @Max(value = 32000, message = "最大Token数不能超过32000")
    private Integer maxTokens;
    
    /**
     * 温度参数
     */
    @DecimalMin(value = "0.0", message = "温度参数不能小于0.0")
    @DecimalMax(value = "2.0", message = "温度参数不能大于2.0")
    private Double temperature;
    
    /**
     * Top-P参数
     */
    @DecimalMin(value = "0.0", message = "Top-P参数不能小于0.0")
    @DecimalMax(value = "1.0", message = "Top-P参数不能大于1.0")
    private Double topP;
    
    /**
     * 频率惩罚
     */
    @DecimalMin(value = "-2.0", message = "频率惩罚不能小于-2.0")
    @DecimalMax(value = "2.0", message = "频率惩罚不能大于2.0")
    private Double frequencyPenalty;
    
    /**
     * 存在惩罚
     */
    @DecimalMin(value = "-2.0", message = "存在惩罚不能小于-2.0")
    @DecimalMax(value = "2.0", message = "存在惩罚不能大于2.0")
    private Double presencePenalty;
    
    /**
     * 超时时间（秒）
     */
    @Min(value = 1, message = "超时时间必须大于0秒")
    @Max(value = 300, message = "超时时间不能超过300秒")
    private Integer timeoutSeconds;
    
    /**
     * 重试次数
     */
    @Min(value = 0, message = "重试次数不能小于0")
    @Max(value = 10, message = "重试次数不能超过10")
    private Integer retryCount;
    
    /**
     * 停止序列
     */
    private String[] stopSequences;
    
    /**
     * 是否启用流式输出
     */
    private Boolean streamEnabled;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    @Override
    public String toString() {
        return "CreateLlmConfigurationCommand{" +
               "providerType='" + providerType + '\'' +
               ", baseUrl='" + baseUrl + '\'' +
               ", apiKey='****'" + // 隐藏敏感信息
               ", modelName='" + modelName + '\'' +
               ", description='" + description + '\'' +
               ", maxTokens=" + maxTokens +
               ", temperature=" + temperature +
               ", topP=" + topP +
               ", frequencyPenalty=" + frequencyPenalty +
               ", presencePenalty=" + presencePenalty +
               ", timeoutSeconds=" + timeoutSeconds +
               ", retryCount=" + retryCount +
               '}';
    }
}
