package org.sounfury.aki.application.conversation.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 聊天响应DTO
 */
@Data
@Builder
public class ChatResponse {
    
    /**
     * AI回复内容
     */
    private String reply;
    
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
     * 策略名称
     */
    private String strategyName;
    
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
    public static ChatResponse success(String reply, String sessionId, String characterId, 
                                     String mode, String strategyName) {
        return ChatResponse.builder()
                .reply(reply)
                .sessionId(sessionId)
                .characterId(characterId)
                .mode(mode)
                .strategyName(strategyName)
                .success(true)
                .build();
    }
    
    /**
     * 创建失败响应
     */
    public static ChatResponse failure(String errorMessage, String sessionId) {
        return ChatResponse.builder()
                .errorMessage(errorMessage)
                .sessionId(sessionId)
                .success(false)
                .build();
    }
}
