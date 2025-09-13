package org.sounfury.aki.domain.llm;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * 模型提供商值对象
 * 封装LLM模型提供商的相关信息
 */
@Getter
@Builder
public class ModelProvider {
    
    /**
     * 提供商类型
     */
    private final ProviderType type;
    
    /**
     * API基础URL
     */
    private final String baseUrl;
    
    /**
     * API密钥
     */
    private final String apiKey;
    
    /**
     * 模型名称
     */
    private final String modelName;
    

    
    /**
     * 提供商类型枚举
     */
    public enum ProviderType {
        DEEPSEEK("DeepSeek", "DeepSeek AI模型"),
        OPENAI("OpenAI", "OpenAI GPT模型"),
        CLAUDE("Claude", "Anthropic Claude模型"),
        GEMINI("Gemini", "Google Gemini模型"),
        QWEN("Qwen", "阿里通义千问模型"),
        CHATGLM("ChatGLM", "ChatGLM系列模型"),
        CUSTOM("Custom", "自定义模型");
        
        private final String displayName;
        private final String description;
        
        ProviderType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 创建模型提供商
     *
     * @param providerType 提供商类型
     * @param baseUrl API基础URL
     * @param apiKey API密钥
     * @param modelName 模型名称
     * @return ModelProvider实例
     */
    public static ModelProvider create(ProviderType providerType, String baseUrl, String apiKey, String modelName) {
        // 设置默认baseUrl
        String finalBaseUrl = baseUrl;
        if (finalBaseUrl == null || finalBaseUrl.trim().isEmpty()) {
            finalBaseUrl = getDefaultBaseUrl(providerType);
        }

        return ModelProvider.builder()
                .type(providerType)
                .baseUrl(finalBaseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    /**
     * 获取提供商的默认baseUrl
     */
    private static String getDefaultBaseUrl(ProviderType providerType) {
        return switch (providerType) {
            case OPENAI -> "https://api.openai.com/v1";
            case CLAUDE -> "https://api.anthropic.com";
            case DEEPSEEK -> "https://api.siliconflow.cn/v1";
            case QWEN -> "https://dashscope.aliyuncs.com/api/v1";
            case GEMINI -> "https://generativelanguage.googleapis.com/v1";
            case CHATGLM -> "https://open.bigmodel.cn/api/paas/v4/";
            case CUSTOM -> null; // 自定义类型必须提供baseUrl
        };
    }
    
    /**
     * 验证提供商配置是否有效
     */
    public boolean isValid() {
        return type != null &&
               baseUrl != null && !baseUrl.trim().isEmpty() &&
               apiKey != null && !apiKey.trim().isEmpty() &&
               modelName != null && !modelName.trim().isEmpty();
    }

    /**
     * 检查是否支持Function Call
     * 简化实现：大部分现代模型都支持工具调用
     */
    public boolean supportsFunctionCall() {
        // 简化逻辑：除了一些特殊情况，大部分模型都支持工具调用
        return !isLegacyModel();
    }

    /**
     * 检查是否支持多模态
     * 简化实现：只有特定模型支持多模态
     */
    public boolean supportsMultimodal() {
        // 简化逻辑：只有包含vision、multimodal等关键词的模型支持多模态
        String lowerModelName = modelName.toLowerCase();
        return lowerModelName.contains("vision") ||
               lowerModelName.contains("multimodal") ||
               lowerModelName.contains("gpt-4") ||
               lowerModelName.contains("claude-3");
    }

    /**
     * 检查是否适合Agent模式
     */
    public boolean isSuitableForAgent() {
        return supportsFunctionCall();
    }

    /**
     * 检查是否为旧版模型（不支持工具调用）
     */
    private boolean isLegacyModel() {
        String lowerModelName = modelName.toLowerCase();
        return lowerModelName.contains("text-davinci") ||
               lowerModelName.contains("gpt-3.5-turbo-instruct") ||
               lowerModelName.contains("claude-1") ||
               lowerModelName.contains("claude-2.0");
    }
    
    /**
     * 获取安全的API密钥显示（隐藏敏感信息）
     */
    public String getMaskedApiKey() {
        if (apiKey == null || apiKey.length() < 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelProvider that = (ModelProvider) o;
        return type == that.type &&
               Objects.equals(baseUrl, that.baseUrl) &&
               Objects.equals(apiKey, that.apiKey) &&
               Objects.equals(modelName, that.modelName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, baseUrl, apiKey, modelName);
    }
    
    /**
     * 获取显示名称（从枚举获取）
     */
    public String getDisplayName() {
        return type.getDisplayName();
    }

    /**
     * 获取描述（从枚举获取）
     */
    public String getDescription() {
        return type.getDescription();
    }

    @Override
    public String toString() {
        return "ModelProvider{" +
               "type=" + type +
               ", baseUrl='" + baseUrl + '\'' +
               ", apiKey='" + getMaskedApiKey() + '\'' +
               ", modelName='" + modelName + '\'' +
               '}';
    }
}
