package org.sounfury.aki.domain.llm;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * 模型设置值对象
 * 封装LLM模型的参数配置
 */
@Getter
@Builder
public class ModelSettings {
    
    /**
     * 默认最大Token数
     */
    private static final Integer DEFAULT_MAX_TOKENS = 4000;
    
    /**
     * 默认温度参数
     */
    private static final Double DEFAULT_TEMPERATURE = 0.7;
    
    /**
     * 默认Top-P参数
     */
    private static final Double DEFAULT_TOP_P = 0.9;
    
    /**
     * 默认频率惩罚
     */
    private static final Double DEFAULT_FREQUENCY_PENALTY = 0.0;
    
    /**
     * 默认存在惩罚
     */
    private static final Double DEFAULT_PRESENCE_PENALTY = 0.0;
    
    /**
     * 最大Token数
     */
    @Builder.Default
    private final Integer maxTokens = DEFAULT_MAX_TOKENS;
    
    /**
     * 温度参数 (0.0-2.0)
     * 控制输出的随机性，值越高越随机
     */
    @Builder.Default
    private final Double temperature = DEFAULT_TEMPERATURE;
    
    /**
     * Top-P参数 (0.0-1.0)
     * 核采样参数，控制输出的多样性
     */
    @Builder.Default
    private final Double topP = DEFAULT_TOP_P;
    
    /**
     * 频率惩罚 (-2.0-2.0)
     * 降低重复内容的概率
     */
    @Builder.Default
    private final Double frequencyPenalty = DEFAULT_FREQUENCY_PENALTY;
    
    /**
     * 存在惩罚 (-2.0-2.0)
     * 鼓励谈论新话题
     */
    @Builder.Default
    private final Double presencePenalty = DEFAULT_PRESENCE_PENALTY;
    
    /**
     * 停止序列
     * 遇到这些序列时停止生成
     */
    private final String[] stopSequences;
    
    /**
     * 是否启用流式输出
     */
    @Builder.Default
    private final boolean streamEnabled = true;
    
    /**
     * 超时时间（秒）
     */
    @Builder.Default
    private final Integer timeoutSeconds = 60;
    
    /**
     * 重试次数
     */
    @Builder.Default
    private final Integer retryCount = 3;
    
    /**
     * 创建默认设置
     */
    public static ModelSettings createDefault() {
        return ModelSettings.builder().build();
    }
    
    /**
     * 创建对话优化设置
     */
    public static ModelSettings createForConversation() {
        return ModelSettings.builder()
                .maxTokens(2000)
                .temperature(0.8)
                .topP(0.9)
                .frequencyPenalty(0.1)
                .presencePenalty(0.1)
                .build();
    }
    
    /**
     * 创建任务执行优化设置
     */
    public static ModelSettings createForAction() {
        return ModelSettings.builder()
                .maxTokens(3000)
                .temperature(0.3)
                .topP(0.8)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();
    }
    
    /**
     * 创建代码生成优化设置
     */
    public static ModelSettings createForCodeGeneration() {
        return ModelSettings.builder()
                .maxTokens(4000)
                .temperature(0.2)
                .topP(0.7)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .stopSequences(new String[]{"```", "---"})
                .build();
    }
    
    /**
     * 验证设置是否有效
     */
    public boolean isValid() {
        return maxTokens != null && maxTokens > 0 && maxTokens <= 32000 &&
               temperature != null && temperature >= 0.0 && temperature <= 2.0 &&
               topP != null && topP >= 0.0 && topP <= 1.0 &&
               frequencyPenalty != null && frequencyPenalty >= -2.0 && frequencyPenalty <= 2.0 &&
               presencePenalty != null && presencePenalty >= -2.0 && presencePenalty <= 2.0 &&
               timeoutSeconds != null && timeoutSeconds > 0 &&
               retryCount != null && retryCount >= 0;
    }
    
    /**
     * 创建设置的副本，允许部分修改
     */
    public ModelSettings withMaxTokens(Integer maxTokens) {
        return ModelSettings.builder()
                .maxTokens(maxTokens)
                .temperature(this.temperature)
                .topP(this.topP)
                .frequencyPenalty(this.frequencyPenalty)
                .presencePenalty(this.presencePenalty)
                .stopSequences(this.stopSequences)
                .streamEnabled(this.streamEnabled)
                .timeoutSeconds(this.timeoutSeconds)
                .retryCount(this.retryCount)
                .build();
    }
    
    public ModelSettings withTemperature(Double temperature) {
        return ModelSettings.builder()
                .maxTokens(this.maxTokens)
                .temperature(temperature)
                .topP(this.topP)
                .frequencyPenalty(this.frequencyPenalty)
                .presencePenalty(this.presencePenalty)
                .stopSequences(this.stopSequences)
                .streamEnabled(this.streamEnabled)
                .timeoutSeconds(this.timeoutSeconds)
                .retryCount(this.retryCount)
                .build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelSettings that = (ModelSettings) o;
        return streamEnabled == that.streamEnabled &&
               Objects.equals(maxTokens, that.maxTokens) &&
               Objects.equals(temperature, that.temperature) &&
               Objects.equals(topP, that.topP) &&
               Objects.equals(frequencyPenalty, that.frequencyPenalty) &&
               Objects.equals(presencePenalty, that.presencePenalty) &&
               Objects.deepEquals(stopSequences, that.stopSequences) &&
               Objects.equals(timeoutSeconds, that.timeoutSeconds) &&
               Objects.equals(retryCount, that.retryCount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maxTokens, temperature, topP, frequencyPenalty, 
                          presencePenalty, stopSequences, streamEnabled, 
                          timeoutSeconds, retryCount);
    }
}
