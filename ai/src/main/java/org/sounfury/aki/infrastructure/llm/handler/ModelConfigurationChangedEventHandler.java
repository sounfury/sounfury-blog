package org.sounfury.aki.infrastructure.llm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.llm.ChatClientCreateApplicationService;
import org.sounfury.aki.domain.llm.event.ModelConfigurationChangedEvent;
import org.sounfury.aki.domain.shared.event.DomainEventHandler;
import org.springframework.stereotype.Component;

/**
 * LLM配置变更事件处理器
 * 当配置发生变更时，重建ChatClient以应用新配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelConfigurationChangedEventHandler implements DomainEventHandler<ModelConfigurationChangedEvent> {


    private final ChatClientCreateApplicationService chatClientCreateApplicationService;

    @Override
    public void handle(ModelConfigurationChangedEvent event) {
        try {
            log.info("处理LLM配置变更事件: configId={}, changeType={}, summary={}", 
                    event.getConfigurationId(), 
                    event.getChangeType(), 
                    event.getChangeSummary());

            // ENABLED_CHANGED 事件优先级最高
            if (event.getChangeType() == ModelConfigurationChangedEvent.ChangeType.ENABLED_CHANGED) {
                chatClientCreateApplicationService.initChatClient();
            } else if (event.requiresChatClientRebuild()) {
                chatClientCreateApplicationService.initChatClient();
            } else {
                handleSettingsChanged(event); //可以只me
            }

        } catch (Exception e) {
            log.error("处理LLM配置变更事件失败: configId={}", event.getConfigurationId(), e);
            // 不抛出异常，避免影响其他事件处理
        }
    }


    /**
     * 处理设置变更（可以使用mutate优化）
     */
    private void handleSettingsChanged(ModelConfigurationChangedEvent event) {
        chatClientCreateApplicationService.rebuildAllBySettingsChange(event.getNewConfiguration());
    }

    @Override
    public Class<ModelConfigurationChangedEvent> getEventType() {
        return ModelConfigurationChangedEvent.class;
    }
}
