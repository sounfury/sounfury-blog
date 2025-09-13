package org.sounfury.aki.application.session.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Session列表响应DTO
 */
@Data
@Builder
public class SessionListResponse {
    
    /**
     * Session列表
     */
    private List<SessionItem> sessions;
    
    /**
     * 总数量
     */
    private Integer total;
    
    /**
     * Session项
     */
    @Data
    @Builder
    public static class SessionItem {
        
        /**
         * 会话ID
         */
        private String sessionId;
        
        /**
         * 角色ID
         */
        private String characterId;
        
        /**
         * 角色名称
         */
        private String characterName;
        
        /**
         * 对话模式
         */
        private String mode;
        
        /**
         * 是否为站长会话
         */
        private Boolean isOwnerSession;
        
        /**
         * 创建时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        
        /**
         * 最后活跃时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastActiveAt;
        
        /**
         * 是否已归档
         */
        private Boolean isArchived;
        
        /**
         * 最后一条消息内容（预览用）
         */
        private String lastMessageContent;
        
        /**
         * 消息总数
         */
        private Integer messageCount;
    }
}
