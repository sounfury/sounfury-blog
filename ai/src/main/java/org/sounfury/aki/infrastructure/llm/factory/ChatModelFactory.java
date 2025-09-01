package org.sounfury.aki.infrastructure.llm.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.ModelProvider;
import org.sounfury.aki.domain.llm.ModelSettings;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

/**
 * ChatModel工厂类
 * 专门负责根据配置创建ChatModel实例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatModelFactory {

    /**
     * 根据LLM配置创建ChatModel
     * 整合ConfigurableChatModelFactory的逻辑，统一使用OpenAI规范
     * 
     * @param config LLM配置
     * @return ChatModel实例
     */
    public ChatModel createChatModel(ModelConfiguration config) {
        try {
            log.debug("创建ChatModel: Provider={}, Model={}", 
                    config.getProvider().getType().getDisplayName(),
                    config.getProvider().getModelName());

            // 构建OpenAI API实例
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(config.getProvider().getBaseUrl())
                    .apiKey(config.getProvider().getApiKey())
                    .build();

            // 构建ChatOptions
            OpenAiChatOptions chatOptions = buildChatOptions(config.getProvider(), config.getSettings());

            // 创建ChatModel
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(chatOptions)
                    .build();

            log.info("ChatModel创建成功: Provider={}, BaseUrl={}, Model={}",
                      config.getProvider().getType().getDisplayName(),
                      config.getProvider().getBaseUrl(),
                      config.getProvider().getModelName());

            return chatModel;

        } catch (Exception e) {
            log.error("创建ChatModel失败: Provider={}, Error={}",
                      config.getProvider().getType().getDisplayName(), e.getMessage(), e);
            throw new RuntimeException("创建ChatModel失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建OpenAI ChatOptions
     * 将ModelSettings映射到OpenAI规范参数
     */
    private OpenAiChatOptions buildChatOptions(ModelProvider provider, ModelSettings settings) {
        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
                .model(provider.getModelName());

        // 映射通用参数到OpenAI规范
        if (settings.getTemperature() != null) {
            optionsBuilder.temperature(settings.getTemperature());
        }

        if (settings.getMaxTokens() != null) {
            optionsBuilder.maxTokens(settings.getMaxTokens());
        }

        if (settings.getTopP() != null) {
            optionsBuilder.topP(settings.getTopP());
        }

        if (settings.getFrequencyPenalty() != null) {
            optionsBuilder.frequencyPenalty(settings.getFrequencyPenalty());
        }

        if (settings.getPresencePenalty() != null) {
            optionsBuilder.presencePenalty(settings.getPresencePenalty());
        }

        OpenAiChatOptions options = optionsBuilder.build();
        
        log.debug("ChatOptions构建完成: model={}, temperature={}, maxTokens={}", 
                options.getModel(), options.getTemperature(), options.getMaxTokens());
        
        return options;
    }

    /**
     * 创建默认的ChatModel
     * 当配置不可用时使用
     */
    public ChatModel createDefaultChatModel() {
        log.warn("使用默认ChatModel配置");
        
        try {
            // 使用默认配置创建
            ModelConfiguration defaultConfig = ModelConfiguration.createDefault();
            return createChatModel(defaultConfig);
        } catch (Exception e) {
            log.error("创建默认ChatModel失败", e);
            throw new RuntimeException("无法创建默认ChatModel", e);
        }
    }
}
