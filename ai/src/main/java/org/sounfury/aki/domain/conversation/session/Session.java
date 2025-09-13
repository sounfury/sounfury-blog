package org.sounfury.aki.domain.conversation.session;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话聚合根
 * 管理会话的生命周期和配置，不管理消息历史（交给Spring AI Memory）
 */
@Getter
public class Session {
    
    /**
     * 会话ID
     */
    private final SessionId sessionId;
    
    /**
     * 会话配置
     */
    private final SessionMeta configuration;
    
    /**
     * 会话创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveAt;
    
    /**
     * 是否已归档
     */
    private boolean isArchived;

    /**
     * 会话记忆
     */
    private List<SessionMemory> memories;

    /**
     * 关联的角色id
     */
    private String personaId;

    /**
     * 私有构造函数
     */
    private Session(SessionId sessionId, SessionMeta configuration) {
        if (!configuration.isValid()) {
            throw new IllegalArgumentException("会话配置无效");
        }
        this.personaId = configuration.getPersonaId();
        this.sessionId = sessionId;
        this.configuration = configuration;
        this.createdAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
        this.isArchived = false;
    }

    /**
     * 从数据库重建的私有构造函数
     */
    private Session(SessionId sessionId, SessionMeta configuration,
                   LocalDateTime createdAt, LocalDateTime lastActiveAt,
                   String personaId, boolean isArchived) {
        if (!configuration.isValid()) {
            throw new IllegalArgumentException("会话配置无效");
        }
        this.sessionId = sessionId;
        this.configuration = configuration;
        this.createdAt = createdAt;
        this.lastActiveAt = lastActiveAt;
        this.personaId = personaId;
        this.isArchived = isArchived;
    }
    
    /**
     * 创建新会话
     */
    public static Session create(SessionMeta configuration) {
        SessionId sessionId = SessionId.generate();
        return new Session(sessionId, configuration);
    }

    /**
     * 根据用户身份创建新会话
     * @param configuration 会话配置
     * @param isOwnerSession 是否为站长会话
     */
    public static Session create(SessionMeta configuration, boolean isOwnerSession) {
        SessionId sessionId = isOwnerSession
                ? SessionId.generate()           // 站长会话：普通ID
                : SessionId.generateForGuest();  // 游客会话：带前缀ID
        return new Session(sessionId, configuration);
    }

    /**
     * 从数据库数据重建会话对象（用于持久化层）
     */
    public static Session fromDatabase(SessionId sessionId, SessionMeta configuration,
                                     LocalDateTime createdAt, LocalDateTime lastActiveAt,
                                     String personaId, boolean isArchived) {
        return new Session(sessionId, configuration, createdAt, lastActiveAt, personaId, isArchived);
    }


    
    /**
     * 更新活跃时间
     */
    public void updateActivity() {
        if (!isArchived) {
            this.lastActiveAt = LocalDateTime.now();
        }
    }


    /**
     * 归档会话
     */
    public void archive() {
        this.isArchived = true;
    }

    /**
     * 取消归档会话
     */
    public void unarchive() {
        this.isArchived = false;
    }

    /**
     * 检查会话是否已归档
     */
    public boolean isArchived() {
        return isArchived;
    }

}
