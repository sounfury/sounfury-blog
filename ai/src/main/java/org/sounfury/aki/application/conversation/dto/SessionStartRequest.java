package org.sounfury.aki.application.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 会话开始请求DTO
 */
@Data
public class SessionStartRequest {
    
    /**
     * 角色ID
     */
    @NotBlank(message = "角色ID不能为空")
    @Size(max = 100, message = "角色ID不能超过100字符")
    private String characterId;
    
    /**
     * 聊天模式（conversation/agent）
     */
    @Size(max = 20, message = "聊天模式不能超过20字符")
    private String mode = "conversation";
    
    /**
     * 是否为站长用户
     */
    private Boolean isOwner = false;
}
