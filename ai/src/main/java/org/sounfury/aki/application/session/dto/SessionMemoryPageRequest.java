package org.sounfury.aki.application.session.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Session记忆分页查询请求DTO
 */
@Data
public class SessionMemoryPageRequest {
    
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    /**
     * 游标时间戳（用于分页，null表示查询最新记录）
     */
    private LocalDateTime cursor;
    
    /**
     * 查询数量限制
     */
    @Min(value = 1, message = "查询数量不能小于1")
    @Max(value = 50, message = "查询数量不能大于50")
    private Integer limit = 10;
}
