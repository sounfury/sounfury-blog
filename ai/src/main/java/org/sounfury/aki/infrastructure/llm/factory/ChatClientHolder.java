package org.sounfury.aki.infrastructure.llm.factory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * ChatClient持有器
 * 管理按角色卡缓存的对话ChatClient和任务ChatClient
 */
@Slf4j
@Component
public class ChatClientHolder {

    private final ConcurrentMap<String, ChatClient> conversationClients = new ConcurrentHashMap<>();
    private final AtomicReference<ChatClient> taskClient = new AtomicReference<>();
    private final ChatClientProvider chatClientFactory;

    public ChatClientHolder(@Lazy ChatClientProvider chatClientFactory) {
        this.chatClientFactory = chatClientFactory;
    }

    /**
     * 获取指定角色的对话ChatClient（懒加载）
     */
    public ChatClient getChatClient(String characterId) {
        if (characterId == null || characterId.trim().isEmpty()) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        return conversationClients.computeIfAbsent(characterId, id -> {;
            log.debug("创建新的ChatClient for character: {}", id);
            return chatClientFactory.createChatClientForCharacter(id);
        });
    }

    /**
     * 获取任务场景的ChatClient
     */
    public ChatClient getTaskClient() {
        return taskClient.get();
    }

    /**
     * 更新指定角色的对话ChatClient
     */
    public void updateChatClient(String characterId, ChatClient newClient) {
        if (characterId != null && !characterId.trim().isEmpty()) {
            conversationClients.put(characterId, newClient);
            log.debug("更新角色ChatClient: {}", characterId);
        }
    }

    /**
     * 更新任务场景的ChatClient
     */
    public void updateTaskClient(ChatClient newClient) {
        taskClient.set(newClient);
        log.debug("更新任务ChatClient");
    }

    /**
     * 移除指定角色的ChatClient缓存
     */
    public void removeConversationClient(String characterId) {
        ChatClient removed = conversationClients.remove(characterId);
        if (removed != null) {
            log.info("移除角色ChatClient缓存: {}", characterId);
        }
    }

    /**
     * 清除所有对话ChatClient缓存
     */
    public void clearAllConversationClients() {
        int count = conversationClients.size();
        conversationClients.clear();
        log.info("清除所有对话ChatClient缓存，共{}个", count);
    }

    /**
     * 获取已缓存的角色数量
     */
    public int getCachedCharacterCount() {
        return conversationClients.size();
    }

    /**
     * 检查指定角色是否已缓存
     */
    public boolean isCharacterCached(String characterId) {
        return conversationClients.containsKey(characterId);
    }



    /**
     * 更新所有ChatClient
     * @param chatClientWithAdvisors 新的ChatClient实例
     */
    public void updateAll(ChatClient chatClientWithAdvisors) {
        // 清除所有对话ChatClient缓存，强制重新创建
        clearAllConversationClients();

        // 更新任务ChatClient
        updateTaskClient(chatClientWithAdvisors);

        log.info("所有ChatClient已更新");
    }
}
