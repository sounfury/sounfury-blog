package org.sounfury.aki.application.conversation.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 会话开始响应DTO
 */
@Data
@Builder
public class SessionStartResponse {
    
    /**
     * 开场白
     */
    private String greeting;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 角色ID
     */
    private String characterId;
    
    /**
     * 聊天模式
     */
    private String mode;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
    
    /**
     * 创建成功响应
     */
    public static SessionStartResponse success(String greeting, String sessionId, 
                                             String characterId, String mode) {
        return SessionStartResponse.builder()
                .greeting(greeting)
                .sessionId(sessionId)
                .characterId(characterId)
                .mode(mode)
                .success(true)
                .build();
    }
    
    /**
     * 创建失败响应
     */
    public static SessionStartResponse failure(String errorMessage) {
        return SessionStartResponse.builder()
                .errorMessage(errorMessage)
                .success(false)
                .build();
    }
}
