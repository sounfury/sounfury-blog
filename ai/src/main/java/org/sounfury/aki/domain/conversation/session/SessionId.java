package org.sounfury.aki.domain.conversation.session;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 会话ID值对象
 * 重构自ConversationId，语义更清晰
 */
@Getter
@EqualsAndHashCode
public class SessionId {
    
    private final String value;
    
    private SessionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
        this.value = value;
    }
    
    /**
     * 从字符串创建
     */
    public static SessionId of(String value) {
        return new SessionId(value);
    }
    
    /**
     * 生成新的会话ID
     */
    public static SessionId generate() {
        return new SessionId(UuidCreator.getTimeOrdered().toString());
    }

    /**
     * 生成游客会话ID（带前缀）
     */
    public static SessionId generateForGuest() {
        return new SessionId("guest_" + UuidCreator.getTimeOrdered().toString());
    }

    /**
     * 判断是否为游客会话ID
     */
    public boolean isGuestSession() {
        return value.startsWith("guest_");
    }
    
    @Override
    public String toString() {
        return value;
    }
}
