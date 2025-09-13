package org.sounfury.aki.application.globalmemory.dto;

import lombok.Data;
import org.sounfury.aki.domain.conversation.memory.GlobalMemory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 全局记忆响应
 */
@Data
public class GlobalMemoryResponse {

    private Long id;
    private String content;
    private Long timestamp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    public static GlobalMemoryResponse from(GlobalMemory globalMemory) {
        if (globalMemory == null) {
            return null;
        }

        GlobalMemoryResponse response = new GlobalMemoryResponse();
        response.setId(globalMemory.getId());
        response.setContent(globalMemory.getContent());
        response.setTimestamp(globalMemory.getTimestamp());
        
        // 将 timestamp 转换为本地时间
        if (globalMemory.getTimestamp() != null) {
            response.setCreateTime(
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(globalMemory.getTimestamp()),
                    ZoneId.systemDefault()
                )
            );
        }
        
        return response;
    }
}
