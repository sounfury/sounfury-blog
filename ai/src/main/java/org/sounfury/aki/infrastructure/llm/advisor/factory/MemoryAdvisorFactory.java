package org.sounfury.aki.infrastructure.llm.advisor.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.conversation.session.SessionMemoryPolicy;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 记忆Advisor工厂
 * 根据存储类型创建不同的记忆Advisor实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemoryAdvisorFactory {

    private final ChatMemory jdbcChatMemory;

    /**
     * 根据存储类型创建记忆Advisor
     * @param sessionMemoryPolicy 记忆策略
     * @return 对应的记忆Advisor
     */
    public Advisor createMemoryAdvisor(SessionMemoryPolicy sessionMemoryPolicy) {
        if (sessionMemoryPolicy == null) {
            log.warn("记忆策略为空，返回null");
            return null;
        }

        return switch (sessionMemoryPolicy.getStorageType()) {
            case SESSION_ONLY -> createSessionMemoryAdvisor();
            case PERSISTENT -> createDatabaseMemoryAdvisor();
        };
    }
    
    /**
     * 创建会话内存Advisor（游客使用）
     * 使用InMemoryChatMemoryRepository，页面刷新即清除
     */
    private Advisor createSessionMemoryAdvisor() {

        try {
            // 为每个会话创建独立的内存存储
            ChatMemoryRepository inMemoryRepository = new InMemoryChatMemoryRepository();

            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(inMemoryRepository)
                    .build();

            // 创建Memory Advisor，conversationId由应用层传递
            Advisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                    .build();

            log.debug("会话内存Advisor创建成功");
            return advisor;

        } catch (Exception e) {
            log.error("创建会话内存Advisor失败: error={}", e.getMessage(), e);
            return createFallbackSessionMemoryAdvisor();
        }
    }

    /**
     * 创建数据库记忆Advisor（站长使用）
     */
    private Advisor createDatabaseMemoryAdvisor() {

        try {
            // 使用配置好的JDBC ChatMemory，conversationId由应用层传递
            return MessageChatMemoryAdvisor.builder(jdbcChatMemory)
                    .build();

        } catch (Exception e) {
            log.error("创建数据库记忆Advisor失败: error={}", e.getMessage(), e);
            // 降级处理：返回null，让系统不使用记忆
            return null;
        }
    }

    
    /**
     * 创建降级的会话内存Advisor
     * 当主要创建方法失败时使用
     */
    private Advisor createFallbackSessionMemoryAdvisor() {
        log.warn("使用降级的会话内存Advisor");
        
        try {
            ChatMemoryRepository inMemoryRepository = new InMemoryChatMemoryRepository();
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(inMemoryRepository)
                    .maxMessages(10) // 默认最大消息数
                    .build();
            
            return MessageChatMemoryAdvisor.builder(chatMemory).build();
            
        } catch (Exception e) {
            log.error("创建降级会话内存Advisor也失败: {}", e.getMessage(), e);
            return null; // 最终降级：不使用记忆
        }
    }
}
