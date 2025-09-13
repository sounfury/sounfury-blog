package org.sounfury.aki.infrastructure.llm;

import io.micrometer.common.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.messages.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class MyJdbcChatMemoryRepository implements ChatMemoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final JdbcChatMemoryRepositoryDialect dialect;
    private static final Logger logger = LoggerFactory.getLogger(MyJdbcChatMemoryRepository.class);

    private MyJdbcChatMemoryRepository(JdbcTemplate jdbcTemplate, JdbcChatMemoryRepositoryDialect dialect, PlatformTransactionManager txManager) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");
        Assert.notNull(dialect, "dialect cannot be null");
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.transactionTemplate = new TransactionTemplate(txManager != null ? txManager : new DataSourceTransactionManager(jdbcTemplate.getDataSource()));
    }

    public List<String> findConversationIds() {
        return this.jdbcTemplate.queryForList(this.dialect.getSelectConversationIdsSql(), String.class);
    }

    public List<Message> findByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        return this.jdbcTemplate.query(this.dialect.getSelectMessagesSql(), new MyJdbcChatMemoryRepository.MessageRowMapper(), new Object[]{conversationId});
    }

    public void saveAll(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        Assert.notNull(messages, "messages cannot be null");
        Assert.noNullElements(messages, "messages cannot contain null elements");

        List<Message> newMessages=List.of(messages.get(messages.size()-1));
        this.transactionTemplate.execute((status) -> {
//            this.deleteByConversationId(conversationId);
            this.jdbcTemplate.batchUpdate(this.dialect.getInsertMessageSql(), new MyJdbcChatMemoryRepository.AddBatchPreparedStatement(conversationId, newMessages));
            return null;
        });
    }

    public void deleteByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        this.jdbcTemplate.update(this.dialect.getDeleteMessagesSql(), new Object[]{conversationId});
    }

    public static MyJdbcChatMemoryRepository.Builder builder() {
        return new MyJdbcChatMemoryRepository.Builder();
    }

    private static class MessageRowMapper implements RowMapper<Message> {
        private MessageRowMapper() {
        }

        @Nullable
        public Message mapRow(ResultSet rs, int i) throws SQLException {
            String content = rs.getString(1);
            MessageType type = MessageType.valueOf(rs.getString(2));
            Object var10000;
            switch (type) {
                case USER -> var10000 = new UserMessage(content);
                case ASSISTANT -> var10000 = new AssistantMessage(content);
                case SYSTEM -> var10000 = new SystemMessage(content);
                case TOOL -> var10000 = new ToolResponseMessage(List.of());
                default -> throw new IncompatibleClassChangeError();
            }

            return (Message)var10000;
        }
    }

    public static final class Builder {
        private JdbcTemplate jdbcTemplate;
        private JdbcChatMemoryRepositoryDialect dialect;
        private DataSource dataSource;
        private PlatformTransactionManager platformTransactionManager;
        private static final Logger logger = LoggerFactory.getLogger(MyJdbcChatMemoryRepository.Builder.class);

        private Builder() {
        }

        public MyJdbcChatMemoryRepository.Builder jdbcTemplate(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
            return this;
        }

        public MyJdbcChatMemoryRepository.Builder dialect(JdbcChatMemoryRepositoryDialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public MyJdbcChatMemoryRepository.Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public MyJdbcChatMemoryRepository.Builder transactionManager(PlatformTransactionManager txManager) {
            this.platformTransactionManager = txManager;
            return this;
        }

        public MyJdbcChatMemoryRepository build() {
            DataSource effectiveDataSource = this.resolveDataSource();
            JdbcChatMemoryRepositoryDialect effectiveDialect = this.resolveDialect(effectiveDataSource);
            return new MyJdbcChatMemoryRepository(this.resolveJdbcTemplate(), effectiveDialect, this.platformTransactionManager);
        }

        private JdbcTemplate resolveJdbcTemplate() {
            if (this.jdbcTemplate != null) {
                return this.jdbcTemplate;
            } else if (this.dataSource != null) {
                return new JdbcTemplate(this.dataSource);
            } else {
                throw new IllegalArgumentException("DataSource must be set (either via dataSource() or jdbcTemplate())");
            }
        }

        private DataSource resolveDataSource() {
            if (this.dataSource != null) {
                return this.dataSource;
            } else if (this.jdbcTemplate != null && this.jdbcTemplate.getDataSource() != null) {
                return this.jdbcTemplate.getDataSource();
            } else {
                throw new IllegalArgumentException("DataSource must be set (either via dataSource() or jdbcTemplate())");
            }
        }

        private JdbcChatMemoryRepositoryDialect resolveDialect(DataSource dataSource) {
            if (this.dialect == null) {
                try {
                    return JdbcChatMemoryRepositoryDialect.from(dataSource);
                } catch (Exception var3) {
                    Exception ex = var3;
                    throw new IllegalStateException("Could not detect dialect from datasource", ex);
                }
            } else {
                this.warnIfDialectMismatch(dataSource, this.dialect);
                return this.dialect;
            }
        }

        private void warnIfDialectMismatch(DataSource dataSource, JdbcChatMemoryRepositoryDialect explicitDialect) {
            try {
                JdbcChatMemoryRepositoryDialect detected = JdbcChatMemoryRepositoryDialect.from(dataSource);
                if (!detected.getClass().equals(explicitDialect.getClass())) {
                    logger.warn("Explicitly set dialect {} will be used instead of detected dialect {} from datasource", explicitDialect.getClass().getSimpleName(), detected.getClass().getSimpleName());
                }
            } catch (Exception var4) {
                Exception ex = var4;
                logger.debug("Could not detect dialect from datasource", ex);
            }

        }
    }

    private static record AddBatchPreparedStatement(String conversationId, List<Message> messages, AtomicLong instantSeq) implements BatchPreparedStatementSetter {
        private AddBatchPreparedStatement(String conversationId, List<Message> messages) {
            this(conversationId, messages, new AtomicLong(Instant
                                                                  .now().toEpochMilli()));
        }

        private AddBatchPreparedStatement(String conversationId, List<Message> messages, AtomicLong instantSeq) {
            this.conversationId = conversationId;
            this.messages = messages;
            this.instantSeq = instantSeq;
        }

        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Message message = messages.get(i);
            ps.setString(1, conversationId);
            ps.setString(2, message.getText());
            ps.setString(3, message.getMessageType().name());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        }

        public int getBatchSize() {
            return this.messages.size();
        }

        public String conversationId() {
            return this.conversationId;
        }

        public List<Message> messages() {
            return this.messages;
        }

        public AtomicLong instantSeq() {
            return this.instantSeq;
        }
    }
}