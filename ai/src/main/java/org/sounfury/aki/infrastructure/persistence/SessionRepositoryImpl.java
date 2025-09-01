package org.sounfury.aki.infrastructure.persistence;


import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.JSON;
import org.sounfury.aki.domain.conversation.session.*;
import org.sounfury.aki.domain.conversation.session.repository.SessionRepository;
import org.sounfury.aki.infrastructure.cache.RedisSessionService;
import org.sounfury.aki.jooq.enums.SpringAiChatMemoryType;
import org.sounfury.aki.jooq.tables.daos.SessionDao;
import org.sounfury.aki.jooq.tables.pojos.SessionPojo;
import org.sounfury.aki.jooq.tables.pojos.SpringAiChatMemoryPojo;
import org.sounfury.core.utils.JsonUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.sounfury.aki.jooq.tables.Session.SESSION;
import static org.sounfury.aki.jooq.tables.SpringAiChatMemory.SPRING_AI_CHAT_MEMORY;

/**
 * 会话仓储实现
 * 游客会话：仅Redis存储，30分钟TTL
 * 站长会话：Redis+数据库双重存储，Redis永不过期用于快速访问，数据库用于持久化
 */
@Slf4j
@Repository
public class SessionRepositoryImpl extends SessionDao implements SessionRepository {

    private final RedisSessionService redisSessionService;

    public SessionRepositoryImpl(Configuration configuration, RedisSessionService redisSessionService) {
        super(configuration);
        this.redisSessionService = redisSessionService;
    }


    @Override
    public void save(Session session) {
        if (session.getSessionId().isGuestSession()) {
            // 游客会话：仅存储到Redis，30分钟TTL
            redisSessionService.saveSession(session);
            log.debug("游客会话已保存到Redis: sessionId={}", session.getSessionId().getValue());
        } else {
            // 站长会话：双重存储
            // 1. 存储到Redis（永不过期，用于快速访问）
            redisSessionService.saveSession(session);
            log.debug("站长会话已保存到Redis: sessionId={}", session.getSessionId().getValue());

            // 2. 存储到数据库（持久化）
            saveToDatabase(session);
            log.debug("站长会话已保存到数据库: sessionId={}", session.getSessionId().getValue());
        }
    }

    @Override
    public Optional<Session> findById(SessionId sessionId) {
        if (sessionId.isGuestSession()) {
            // 游客会话：仅从Redis查询
            return redisSessionService.findSession(sessionId);
        } else {
            // 站长会话：优先从Redis查询，Redis没有则从数据库查询并回写Redis
            Optional<Session> redisSession = redisSessionService.findSession(sessionId);
            if (redisSession.isPresent()) {
                log.debug("从Redis获取站长会话: sessionId={}", sessionId.getValue());
                return redisSession;
            }

            // Redis中没有，从数据库查询
            Optional<Session> dbSession = findFromDatabase(sessionId);
            if (dbSession.isPresent()) {
                // 回写到Redis
                redisSessionService.saveSession(dbSession.get());
                log.debug("从数据库获取站长会话并回写Redis: sessionId={}", sessionId.getValue());
                return dbSession;
            }

            log.debug("未找到站长会话: sessionId={}", sessionId.getValue());
            return Optional.empty();
        }
    }

    @Override
    public List<SessionMemory> findSessionMemories(SessionId sessionId, java.time.LocalDateTime cursor, int limit) {
        try {
            var query = ctx()
                    .selectFrom(SPRING_AI_CHAT_MEMORY)
                    .where(SPRING_AI_CHAT_MEMORY.CONVERSATION_ID.eq(sessionId.getValue()));

            // 添加游标条件
            if (cursor != null) {
                query = query.and(SPRING_AI_CHAT_MEMORY.TIMESTAMP.lt(cursor));
            }

            List<SpringAiChatMemoryPojo> memoryPojos = query
                    .orderBy(SPRING_AI_CHAT_MEMORY.TIMESTAMP.desc())
                    .limit(limit)
                    .fetchInto(SpringAiChatMemoryPojo.class);

            return memoryPojos.stream()
                    .map(this::toSessionMemory)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("分页查询会话记忆失败: sessionId={}, cursor={}, limit={}",
                    sessionId.getValue(), cursor, limit, e);
            return List.of();
        }
    }

    @Override
    public boolean exists(SessionId sessionId) {
        if (sessionId.isGuestSession()) {
            // 游客会话：仅检查Redis
            return redisSessionService.existsSession(sessionId);
        } else {
            // 站长会话：优先检查Redis，Redis没有则检查数据库
            if (redisSessionService.existsSession(sessionId)) {
                return true;
            }
            return existsInDatabase(sessionId);
        }
    }

    @Override
    public void delete(SessionId sessionId) {
        if (sessionId.isGuestSession()) {
            // 游客会话：仅从Redis删除
            redisSessionService.deleteSession(sessionId);
        } else {
            // 站长会话：同时从Redis和数据库删除
            redisSessionService.deleteSession(sessionId);
            deleteFromDatabase(sessionId);
            log.debug("站长会话已从Redis和数据库删除: sessionId={}", sessionId.getValue());
        }
    }



    @Override
    public List<Session> findActiveSessions(boolean isOwnerSession) {
        try {
            return ctx()
                    .selectFrom(SESSION)
                    .where(SESSION.IS_ARCHIVED.isNull().or(SESSION.IS_ARCHIVED.eq((byte) 0)))
                    .and(isOwnerSession
                        ? SESSION.SESSION_ID.notLike("guest_%")
                        : SESSION.SESSION_ID.like("guest_%"))
                    .fetchInto(SessionPojo.class)
                    .stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找未归档会话失败: isOwnerSession={}", isOwnerSession, e);
            return List.of();
        }
    }

    @Override
    public List<Session> findByCharacterIdAndOwnerType(String characterId, boolean isOwnerSession) {
        try {
            return ctx()
                    .selectFrom(SESSION)
                    .where(SESSION.PERSONAID.eq(characterId))
                    .and(SESSION.IS_ARCHIVED.isNull().or(SESSION.IS_ARCHIVED.eq((byte) 0)))
                    .and(isOwnerSession
                        ? SESSION.SESSION_ID.notLike("guest_%")
                        : SESSION.SESSION_ID.like("guest_%"))
                    .orderBy(SESSION.LAST_ACTIVE_TIME.desc())
                    .fetchInto(SessionPojo.class)
                    .stream()
                    .map(this::toDomain)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据角色ID查询会话列表失败: characterId={}, isOwnerSession={}",
                    characterId, isOwnerSession, e);
            return List.of();
        }
    }

    // ========== 数据库操作私有方法 ==========

    /**
     * 保存会话到数据库
     */
    private void saveToDatabase(Session session) {
        try {
            SessionPojo pojo = fromDomain(session);

            if (existsById(session.getSessionId().getValue())) {
                update(pojo);
                log.debug("更新会话到数据库: sessionId={}", session.getSessionId().getValue());
            } else {
                insert(pojo);
                log.debug("插入会话到数据库: sessionId={}", session.getSessionId().getValue());
            }
        } catch (Exception e) {
            log.error("保存会话到数据库失败: sessionId={}", session.getSessionId().getValue(), e);
            throw new RuntimeException("保存会话到数据库失败", e);
        }
    }

    /**
     * 从数据库查询会话
     */
    private Optional<Session> findFromDatabase(SessionId sessionId) {
        try {

            SessionPojo pojo = findById(sessionId.getValue());
            return Optional.ofNullable(pojo).map(this::toDomain);
        } catch (Exception e) {
            log.error("从数据库查询会话失败: sessionId={}", sessionId.getValue(), e);
            return Optional.empty();
        }
    }

    /**
     * 检查会话在数据库中是否存在
     */
    private boolean existsInDatabase(SessionId sessionId) {
        try {
            return ctx()
                    .fetchExists(
                            ctx().selectOne()
                                    .from(SESSION)
                                    .where(SESSION.SESSION_ID.eq(sessionId.getValue()))
                    );
        } catch (Exception e) {
            log.error("检查数据库中会话存在性失败: sessionId={}", sessionId.getValue(), e);
            return false;
        }
    }

    /**
     * 从数据库删除会话
     */
    private void deleteFromDatabase(SessionId sessionId) {
        try {
            deleteById(sessionId.getValue());
            log.debug("从数据库删除会话: sessionId={}", sessionId.getValue());
        } catch (Exception e) {
            log.error("从数据库删除会话失败: sessionId={}", sessionId.getValue(), e);
            throw new RuntimeException("从数据库删除会话失败", e);
        }
    }

    // ========== 领域对象转换方法 ==========

    /**
     * 将数据库POJO转换为领域对象
     */
    private Session toDomain(SessionPojo pojo) {
        if (pojo == null) {
            return null;
        }

        try {
            // 解析SessionMeta JSON
            SessionMeta sessionMeta = parseSessionMeta(pojo.getSessionMeta());

            // 创建SessionId
            SessionId sessionId = SessionId.of(pojo.getSessionId());

            // 使用fromDatabase方法重建Session对象
            boolean isArchived = pojo.getIsArchived() != null && pojo.getIsArchived() == 1;
            Session session = Session.fromDatabase(sessionId, sessionMeta,
                    pojo.getCreateTime(), pojo.getLastActiveTime(),
                    pojo.getPersonaid(), isArchived);

            return session;
        } catch (Exception e) {
            log.error("转换SessionPojo到领域对象失败: sessionId={}", pojo.getSessionId(), e);
            return null;
        }
    }

    /**
     * 将领域对象转换为数据库POJO
     */
    private SessionPojo fromDomain(Session session) {
        if (session == null) {
            return null;
        }

        SessionPojo pojo = new SessionPojo();
        pojo.setSessionId(session.getSessionId().getValue());
        pojo.setSessionMeta(serializeSessionMeta(session.getConfiguration()));
        pojo.setCreateTime(session.getCreatedAt());
        pojo.setLastActiveTime(session.getLastActiveAt());
        pojo.setPersonaid(session.getPersonaId());
        pojo.setIsArchived(session.isArchived() ? (byte) 1 : (byte) 0);

        return pojo;
    }

    /**
     * 解析SessionMeta JSON
     */
    private SessionMeta parseSessionMeta(JSON json) {
        try {
            String jsonString = json.data();
            return JsonUtils.parseObject(jsonString, SessionMeta.class);
        } catch (Exception e) {
            log.error("解析SessionMeta JSON失败: {}", json, e);
            throw new RuntimeException("解析SessionMeta JSON失败", e);
        }
    }

    /**
     * 序列化SessionMeta为JSON
     */
    private JSON serializeSessionMeta(SessionMeta sessionMeta) {
        try {
            String jsonString = JsonUtils.toJsonString(sessionMeta);
            return JSON.valueOf(jsonString);
        } catch (Exception e) {
            log.error("序列化SessionMeta为JSON失败: {}", sessionMeta, e);
            throw new RuntimeException("序列化SessionMeta为JSON失败", e);
        }
    }

    /**
     * 将SpringAiChatMemoryPojo转换为SessionMemory
     */
    private SessionMemory toSessionMemory(SpringAiChatMemoryPojo pojo) {
        if (pojo == null) {
            return null;
        }

        try {
            // 转换类型枚举
            SessionMemoryType memoryType = convertToSessionMemoryType(pojo.getType());

            return new SessionMemory(
                    pojo.getContent(),
                    pojo.getTimestamp(),
                    memoryType
            );
        } catch (Exception e) {
            log.error("转换SpringAiChatMemoryPojo到SessionMemory失败: conversationId={}",
                    pojo.getConversationId(), e);
            return null;
        }
    }

    /**
     * 转换SpringAiChatMemoryType到SessionMemoryType
     */
    private SessionMemoryType convertToSessionMemoryType(SpringAiChatMemoryType springType) {
        if (springType == null) {
            return SessionMemoryType.SYSTEM;
        }

        return switch (springType) {
            case USER -> SessionMemoryType.USER;
            case ASSISTANT -> SessionMemoryType.ASSISTANT;
            case SYSTEM, TOOL -> SessionMemoryType.SYSTEM;
        };
    }
}