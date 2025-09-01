package org.sounfury.aki.domain.prompt.context;

import lombok.Builder;
import lombok.Getter;

/**
 * 会话上下文
 * 用于模板中的 {{session.xxx}} 占位符
 */
@Getter
@Builder
public class SessionCtx {
    
    /**
     * 会话ID
     * 模板占位符: {{session.id}}
     */
    private final String id;
    
    /**
     * 会话类型
     * 模板占位符: {{session.type}}
     */
    private final String type;
    
    /**
     * 是否为站长会话
     * 模板占位符: {{session.isOwner}}
     */
    private final Boolean isOwner;
    
    /**
     * 创建会话上下文
     */
    public static SessionCtx of(String id) {
        return SessionCtx.builder()
                .id(id)
                .build();
    }
    
    /**
     * 创建完整的会话上下文
     */
    public static SessionCtx of(String id, String type, boolean isOwner) {
        return SessionCtx.builder()
                .id(id)
                .type(type)
                .isOwner(isOwner)
                .build();
    }
    
    /**
     * 创建空的会话上下文
     */
    public static SessionCtx empty() {
        return SessionCtx.builder().build();
    }
}
