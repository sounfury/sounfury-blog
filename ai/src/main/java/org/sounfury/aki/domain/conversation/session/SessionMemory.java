package org.sounfury.aki.domain.conversation.session;


import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SessionMemory {

     //内容
    private final String content;

    //timestamp
    private final LocalDateTime timestamp;

    //类型
    private final SessionMemoryType type;

    public SessionMemory(String content, LocalDateTime timestamp, SessionMemoryType type) {
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }
}
