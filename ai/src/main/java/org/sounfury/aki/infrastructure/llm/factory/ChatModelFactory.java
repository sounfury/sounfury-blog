package org.sounfury.aki.infrastructure.llm.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.ModelProvider;
import org.sounfury.aki.domain.llm.ModelSettings;
import org.sounfury.core.convention.exception.ServiceException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

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
            
            // 解析完整的URL
            String fullUrl = config.getProvider().getBaseUrl();
            URI uri = new URI(fullUrl);
            String baseUrl = uri.getScheme() + "://" + uri.getAuthority();
            String path = uri.getPath();
            //path不存在报错
            if (!StringUtils.hasText(path)) {
                throw new ServiceException("BaseUrl缺少路径部分: " + fullUrl);
            }

            // 构建OpenAI API实例
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(baseUrl)
                    .apiKey(config.getProvider().getApiKey())
                    .completionsPath(path)
                    .build();

            // 构建ChatOptions
            OpenAiChatOptions chatOptions = buildChatOptions(config.getProvider(), config.getSettings());

            // 创建ChatModel
            OpenAiChatModel.Builder chatModelBuilder = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(chatOptions);

            OpenAiChatModel chatModel = chatModelBuilder.build();

            log.info("ChatModel创建成功: Provider={}, BaseUrl={}, Model={}",
                      config.getProvider().getType().getDisplayName(),
                      config.getProvider().getBaseUrl(),
                      config.getProvider().getModelName());

            return chatModel;

        } catch (URISyntaxException e) {
            log.error("创建ChatModel失败: 无效的BaseUrl={}, Error={}",
                    config.getProvider().getBaseUrl(), e.getMessage(), e);
            throw new RuntimeException("创建ChatModel失败: 无效的BaseUrl " + config.getProvider().getBaseUrl(), e);
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
    public OpenAiChatOptions buildChatOptions(ModelProvider provider, ModelSettings settings) {
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

}
