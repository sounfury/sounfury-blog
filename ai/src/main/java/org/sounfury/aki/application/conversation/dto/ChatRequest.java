package org.sounfury.aki.application.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequest {
    
    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000字符")
    private String message;
    
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    /**
     * 用户名称
     */
    @Size(max = 50, message = "用户名称不能超过50字符")
    private String userName = "旅行者";
    
    /**
     * 角色ID
     */
    @Size(max = 100, message = "角色ID不能超过100字符")
    private String characterId = "bartender";
    
    /**
     * 是否为站长用户
     */
    private Boolean isOwner = false;
    
    /**
     * 是否启用Agent模式（工具调用）
     * 默认为false，需要非游客用户且显式启用才生效
     */
    private Boolean enableAgent = false;
}
