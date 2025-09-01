package org.sounfury.aki.domain.prompt.context;

import lombok.Builder;
import lombok.Getter;

/**
 * 用户上下文
 * 用于模板中的 {{user.xxx}} 占位符
 */
@Getter
@Builder
public class UserCtx {
    
    /**
     * 用户名称
     * 模板占位符: {{user.name}}
     */
    private final String name;

    
    /**
     * 是否为站长
     * 模板占位符: {{user.isOwner}}
     */
    private final Boolean isOwner;
    
    /**
     * 创建用户上下文
     */
    public static UserCtx of(String name) {
        return UserCtx.builder()
                .name(name)
                .build();
    }
    
    /**
     * 创建完整的用户上下文
     */
    public static UserCtx of(String id, String name, boolean isOwner) {
        return UserCtx.builder()
                .name(name)
                .isOwner(isOwner)
                .build();
    }
    
    /**
     * 创建空的用户上下文
     */
    public static UserCtx empty() {
        return UserCtx.builder().build();
    }
}
