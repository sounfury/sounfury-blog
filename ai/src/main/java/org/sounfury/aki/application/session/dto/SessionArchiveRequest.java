package org.sounfury.aki.application.session.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Session归档请求DTO
 */
@Data
public class SessionArchiveRequest {
    
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    /**
     * 归档原因（可选）
     */
    private String reason;
}
