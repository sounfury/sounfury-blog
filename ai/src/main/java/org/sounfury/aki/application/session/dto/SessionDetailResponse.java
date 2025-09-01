package org.sounfury.aki.application.session.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Session详情响应DTO
 */
@Data
@Builder
public class SessionDetailResponse {
    
    /**
     * 会话基础信息
     */
    private SessionInfo sessionInfo;
    
    /**
     * 最新的记忆列表（最多10条）
     */
    private List<MemoryItem> memories;
    
    /**
     * 是否还有更多记忆
     */
    private Boolean hasMore;
    
    /**
     * 会话基础信息
     */
    @Data
    @Builder
    public static class SessionInfo {
        
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
        private LocalDateTime createdAt;
        
        /**
         * 最后活跃时间
         */
        private LocalDateTime lastActiveAt;
        
        /**
         * 是否已归档
         */
        private Boolean isArchived;
        
        /**
         * 是否启用工具调用
         */
        private Boolean toolsEnabled;
        
        /**
         * 是否启用记忆
         */
        private Boolean memoryEnabled;
    }
    
    /**
     * 记忆项
     */
    @Data
    @Builder
    public static class MemoryItem {
        
        /**
         * 内容
         */
        private String content;
        
        /**
         * 类型（USER/ASSISTANT/SYSTEM）
         */
        private String type;
        
        /**
         * 时间戳
         */
        private LocalDateTime timestamp;
    }
}
