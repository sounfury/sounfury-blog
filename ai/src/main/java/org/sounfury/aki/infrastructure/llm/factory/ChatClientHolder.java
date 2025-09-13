package org.sounfury.aki.infrastructure.llm.factory;

import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * ChatClient持有器
 * 重构后管理单一对话ChatClient和任务ChatClient
 * advisor通过AdvisorCacheManager管理，运行时组装
 */
@Slf4j
@Component
public class ChatClientHolder {

    // 单一对话ChatClient（基础配置，不包含角色特定advisor）
    private final AtomicReference<ChatClient> conversationClient = new AtomicReference<>();
    // 任务ChatClient
    private final AtomicReference<ChatClient> taskClient = new AtomicReference<>();

    /**
     * 获取对话ChatClient（统一的基础ChatClient）
     * 角色切换通过运行时advisor实现
     */
    public ChatClient getConversationClient() {
        ChatClient client = conversationClient.get();
        if (client == null) {
            throw new IllegalStateException("对话ChatClient未初始化");
        }
        return client;
    }

    /**
     * 获取任务场景的ChatClient
     */
    public ChatClient getTaskClient() {
        return taskClient.get();
    }

    /**
     * 更新对话ChatClient
     */
    public void updateConversationClient(ChatClient newClient) {
        conversationClient.set(newClient);
        log.debug("更新对话ChatClient");
    }

    /**
     * 更新任务场景的ChatClient
     */
    public void updateTaskClient(ChatClient newClient) {
        taskClient.set(newClient);
        log.debug("更新任务ChatClient");
    }
    public void clearAllClients() {
        // 1. 清除对话ChatClient
        conversationClient.set(null);
        
        // 2. 清除任务ChatClient缓存
        taskClient.set(null);
        
        log.info("清除所有ChatClient");
    }


    /**
     * 检查对话ChatClient是否已初始化
     */
    public boolean isConversationClientInitialized() {
        return conversationClient.get() != null;
    }

    /**
     * 检查任务ChatClient是否已初始化  
     */
    public boolean isTaskClientInitialized() {
        return taskClient.get() != null;
    }


}
