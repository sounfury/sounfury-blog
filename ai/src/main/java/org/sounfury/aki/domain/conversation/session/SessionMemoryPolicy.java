package org.sounfury.aki.domain.conversation.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 会话记忆策略值对象
 * 用于区分游客（短期记忆）和站长（持久记忆）
 */
@Getter
public class SessionMemoryPolicy {

    private final StorageType storageType;

    private SessionMemoryPolicy(StorageType storageType) {
        this.storageType = storageType;
    }

    // === 新增注解 ===
    @JsonCreator
    public static SessionMemoryPolicy fromJson(
            @JsonProperty("storageType") StorageType storageType
    ) {
        return new SessionMemoryPolicy(storageType);
    }
    /**
     * 存储类型枚举
     */
    public enum StorageType {
        SESSION_ONLY, // 仅会话内存储（游客）
        PERSISTENT    // 永久存储（站长）
    }

    /**
     * 为游客创建记忆策略
     */
    public static SessionMemoryPolicy forGuest() {
        return new SessionMemoryPolicy(StorageType.SESSION_ONLY);
    }

    /**
     * 为站长创建记忆策略
     */
    public static SessionMemoryPolicy forOwner() {
        return new SessionMemoryPolicy(StorageType.PERSISTENT);
    }

    /**
     * 是否需要持久化
     */
    public boolean needsPersistentStorage() {
        return storageType == StorageType.PERSISTENT;
    }

    /**
     * 是否仅为会话存储
     */
    public boolean isSessionOnly() {
        return storageType == StorageType.SESSION_ONLY;
    }

    /**
     * 获取策略类型字符串
     */
    public String getType() {
        return storageType.name();
    }

    /**
     * 从类型字符串创建策略
     */
    public static SessionMemoryPolicy fromType(String type) {
        StorageType storageType = StorageType.valueOf(type);
        return new SessionMemoryPolicy(storageType);
    }
}
