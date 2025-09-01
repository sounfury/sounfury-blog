package org.sounfury.aki.infrastructure.llm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.llm.event.ModelConfigurationChangedEvent;
import org.sounfury.aki.domain.llm.repository.LlmConfigurationRepository;
import org.sounfury.aki.infrastructure.llm.factory.ChatModelFactory;
import org.sounfury.aki.infrastructure.llm.factory.ChatClientProvider;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ChatClient事件处理器
 * 专门处理各种变更事件，协调ChatClient重建
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatClientEventHandler {

    private final ChatModelFactory chatModelFactory;
    private final ChatClientProvider chatClientProvider;
    private final LlmConfigurationRepository configurationRepository;

    /**
     * 监听配置变更事件，动态重建ChatClient
     * 
     * @param event 配置变更事件
     */
    @EventListener
    public void handleConfigurationChanged(ModelConfigurationChangedEvent event) {
        log.info("收到LLM配置变更事件: {}", event.getChangeSummary());
        
        Integer configId = event.getConfigurationId();
        
        // 如果需要重建ChatClient
        if (event.requiresChatClientRebuild()) {
            log.info("配置变更需要重建ChatClient: {}", configId);
            rebuildChatClient(configId);
        } else {
            log.debug("配置变更不需要重建ChatClient: {}", configId);
        }
    }

    /**
     * 重建ChatClient
     * 
     * @param configurationId 配置ID
     */
    private void rebuildChatClient(Integer configurationId) {
        try {
            log.info("开始重建ChatClient: {}", configurationId);
            
            // 1. 获取新配置
            ModelConfiguration config = getConfigurationById(configurationId);
            
            // 2. 创建新的ChatModel
            ChatModel newChatModel = chatModelFactory.createChatModel(config);
            
            // 3. 通过编排服务重新构建ChatClient（包含advisor）
            chatClientProvider.rebuildChatClientWithModel(newChatModel);
            
            log.info("ChatClient重建完成: {}", configurationId);
            
        } catch (Exception e) {
            log.error("重建ChatClient失败: {}", configurationId, e);
            // 可以考虑发送失败事件或使用fallback策略
        }
    }

    /**
     * 根据配置ID获取配置
     *
     * @param configurationId 配置ID
     * @return 配置对象
     */
    private ModelConfiguration getConfigurationById(Integer configurationId) {
        return configurationRepository.findById(configurationId)
                .orElseGet(() -> {
                    log.warn("配置不存在，使用默认配置: {}", configurationId);
                    return configurationRepository.findGlobalConfiguration()
                            .orElse(ModelConfiguration.createDefault());
                });
    }

    /**
     * 手动触发ChatClient重建
     * 用于测试或手动刷新
     */
    public void manualRebuild() {
        log.info("手动触发ChatClient重建");
        try {
            // 使用全局配置重建
            ModelConfiguration globalConfig = configurationRepository.findGlobalConfiguration()
                    .orElse(ModelConfiguration.createDefault());
            
            ChatModel newChatModel = chatModelFactory.createChatModel(globalConfig);
            chatClientProvider.rebuildChatClientWithModel(newChatModel);
            
            log.info("手动重建ChatClient完成");
        } catch (Exception e) {
            log.error("手动重建ChatClient失败", e);
        }
    }
}
