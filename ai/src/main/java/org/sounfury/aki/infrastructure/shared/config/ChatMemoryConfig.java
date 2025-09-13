package org.sounfury.aki.infrastructure.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.infrastructure.llm.MyJdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Spring AI Chat Memory 配置类
 * 配置聊天记忆相关的Bean，支持JDBC和内存两种存储方式
 */
@Slf4j
@Configuration
public class ChatMemoryConfig {


    /**
     * 配置默认的ChatMemoryRepository
     * 默认使用内存存储，主要用于游客用户
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultChatMemoryRepository")
    public ChatMemoryRepository defaultChatMemoryRepository() {
        log.info("配置默认ChatMemoryRepository: InMemoryChatMemoryRepository");
        return new InMemoryChatMemoryRepository();
    }


    @Bean
    public JdbcChatMemoryRepositoryDialect chatDialect(DataSource ds) {
        return JdbcChatMemoryRepositoryDialect.from(ds);
    }

    @Bean("MyJdbcChatMemoryRepository")
    public ChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate,JdbcChatMemoryRepositoryDialect chatDialect){
        return MyJdbcChatMemoryRepository
                .builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(chatDialect)
                .build();
    }

    /**
     * 配置站长用户的ChatMemory
     * 使用JDBC存储，保持更多历史消息
     */
    @Bean
    public ChatMemory jdbcChatMemory( @Qualifier("MyJdbcChatMemoryRepository")ChatMemoryRepository jdbcChatMemoryRepository) {
        log.info("配置JDBC ChatMemory，最大消息数: 50");
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(50) // 站长用户保持更多历史消息
                .build();
    }

    /**
     * 配置站长用户的MessageChatMemoryAdvisor
     * 用于自动注入历史消息到对话中
     */
    @Bean
    public MessageChatMemoryAdvisor jdbcMessageChatMemoryAdvisor(ChatMemory jdbcChatMemory) {
        log.info("配置JDBC MessageChatMemoryAdvisor");
        return MessageChatMemoryAdvisor.builder(jdbcChatMemory).build();
    }
}
