package org.sounfury.aki.infrastructure.llm.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通用工具集
 * 包含所有通用的AI工具，如时间查询等
 */
@Component("common_tools")
public class CommonTools {

    /**
     * 获取当前时间
     */
    @Tool(name="current_time", description = "Get the current time only. Call this when user asks about current time or 'what time is it'.")
    public String getCurrentTime() {
        return LocalDateTime
                .now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
