package org.sounfury.aki.infrastructure.cache;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.conversation.session.Session;
import org.sounfury.aki.domain.conversation.session.SessionId;
import org.sounfury.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis会话存储服务
 * 专门处理游客会话的临时存储，固定30分钟TTL
 */
@Slf4j
@Service
public class RedisSessionService {

    /**
     * 游客会话TTL：30分钟
     */
    private static final Duration GUEST_SESSION_TTL = Duration.ofMinutes(30);

    /**
     * 游客会话Key前缀
     */
    private static final String GUEST_SESSION_PREFIX = "guest_session:";

    /**
     * 站长会话Key前缀
     */
    private static final String OWNER_SESSION_PREFIX = "owner_session:";

    /**
     * 保存会话到Redis
     * @param session 会话对象
     */
    public void saveSession(Session session) {
        try {
            String sessionKey = buildSessionKey(session.getSessionId());

            if (session.getSessionId().isGuestSession()) {
                // 游客会话：30分钟TTL
                RedisUtils.setCacheObject(sessionKey, session, GUEST_SESSION_TTL);
                log.debug("游客会话已保存到Redis: sessionId={}, ttl={}分钟",
                        session.getSessionId().getValue(), GUEST_SESSION_TTL.toMinutes());
            } else {
                // 站长会话：永不过期
                RedisUtils.setCacheObject(sessionKey, session);
                log.debug("站长会话已保存到Redis: sessionId={}, 永不过期",
                        session.getSessionId().getValue());
            }

        } catch (Exception e) {
            log.error("保存会话失败: sessionId={}", session.getSessionId().getValue(), e);
            throw new RuntimeException("保存会话失败", e);
        }
    }

    /**
     * 保存游客会话到Redis（兼容方法）
     * @param session 会话对象
     */
    public void saveGuestSession(Session session) {
        saveSession(session);
    }

    /**
     * 从Redis获取会话
     * @param sessionId 会话ID
     * @return 会话对象（如果存在）
     */
    public Optional<Session> findSession(SessionId sessionId) {
        try {
            String sessionKey = buildSessionKey(sessionId);
            System.out.println("Redis Key: " + sessionKey);
            Session session = RedisUtils.getCacheObject(sessionKey);

            if (session == null) {
                log.info("Redis中未找到会话: sessionId={}", sessionId.getValue());
                return Optional.empty();
            }

            String sessionType = sessionId.isGuestSession() ? "游客" : "站长";
            log.debug("从Redis获取{}会话成功: sessionId={}", sessionType, sessionId.getValue());
            return Optional.of(session);

        } catch (Exception e) {
            log.error("获取会话失败: sessionId={}", sessionId.getValue(), e);
            return Optional.empty();
        }
    }

    /**
     * 从Redis获取游客会话（兼容方法）
     * @param sessionId 会话ID
     * @return 会话对象（如果存在）
     */
    public Optional<Session> findGuestSession(SessionId sessionId) {
        return findSession(sessionId);
    }

    /**
     * 检查会话是否存在
     * @param sessionId 会话ID
     * @return 是否存在
     */
    public boolean existsSession(SessionId sessionId) {
        String sessionKey = buildSessionKey(sessionId);
        return RedisUtils.hasKey(sessionKey);
    }

    /**
     * 删除会话
     * @param sessionId 会话ID
     */
    public void deleteSession(SessionId sessionId) {
        String sessionKey = buildSessionKey(sessionId);
        boolean deleted = RedisUtils.deleteObject(sessionKey);

        String sessionType = sessionId.isGuestSession() ? "游客" : "站长";
        if (deleted) {
            log.debug("{}会话已从Redis删除: sessionId={}", sessionType, sessionId.getValue());
        } else {
            log.debug("Redis中未找到要删除的{}会话: sessionId={}", sessionType, sessionId.getValue());
        }
    }

    /**
     * 获取会话剩余TTL
     * @param sessionId 会话ID
     * @return 剩余时间（秒），-1表示不存在或无过期时间，-2表示永不过期
     */
    public long getSessionTTL(SessionId sessionId) {
        String sessionKey = buildSessionKey(sessionId);
        return RedisUtils.getTimeToLive(sessionKey);
    }

    /**
     * 检查游客会话是否存在（兼容方法）
     */
    public boolean existsGuestSession(SessionId sessionId) {
        return existsSession(sessionId);
    }

    /**
     * 删除游客会话（兼容方法）
     */
    public void deleteGuestSession(SessionId sessionId) {
        deleteSession(sessionId);
    }

    /**
     * 获取游客会话剩余TTL（兼容方法）
     */
    public long getGuestSessionTTL(SessionId sessionId) {
        return getSessionTTL(sessionId);
    }

    /**
     * 判断是否为游客会话ID
     * @param sessionId 会话ID
     * @return 是否为游客会话
     */
    public static boolean isGuestSessionId(String sessionId) {
        return sessionId != null && sessionId.startsWith("guest_");
    }

    /**
     * 构建Redis Key
     */
    private String buildSessionKey(SessionId sessionId) {
        if (sessionId.isGuestSession()) {
            return GUEST_SESSION_PREFIX + sessionId.getValue();
        } else {
            return OWNER_SESSION_PREFIX + sessionId.getValue();
        }
    }
}
