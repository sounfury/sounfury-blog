package org.sounfury.aki.domain.conversation.session.repository;

import org.sounfury.aki.domain.conversation.session.Session;
import org.sounfury.aki.domain.conversation.session.SessionId;
import org.sounfury.aki.domain.conversation.session.SessionMemory;

import java.util.Optional;
import java.util.List;

/**
 * 会话仓储接口
 * 负责会话的持久化操作
 */
public interface SessionRepository {
    
    /**
     * 保存会话
     */
    void save(Session session);
    
    /**
     * 根据ID查找会话
     */
    Optional<Session> findById(SessionId sessionId);


    /**
     * 分页查询会话记忆（游标分页）
     * @param sessionId 会话ID
     * @param cursor 游标时间戳，null表示查询最新记录
     * @param limit 查询数量限制
     * @return 会话记忆列表，按时间倒序
     */
    List<SessionMemory> findSessionMemories(SessionId sessionId, java.time.LocalDateTime cursor, int limit);


    /**
     * 检查会话是否存在
     */
    boolean exists(SessionId sessionId);
    
    /**
     * 删除会话
     */
    void delete(SessionId sessionId);

    /**
     * 查找用户的未归档会话
     * @param isOwnerSession 是否是站长会话
     * @return 未归档会话列表
     */
    List<Session> findActiveSessions(boolean isOwnerSession);

    /**
     * 根据角色ID和用户类型查询会话列表
     * @param characterId 角色ID
     * @param isOwnerSession 是否是站长会话
     * @return 会话列表
     */
    List<Session> findByCharacterIdAndOwnerType(String characterId, boolean isOwnerSession);
}
