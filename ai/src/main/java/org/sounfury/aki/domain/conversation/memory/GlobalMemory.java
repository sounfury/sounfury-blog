package org.sounfury.aki.domain.conversation.memory;

import lombok.Data;

@Data
public class GlobalMemory {

    // 全局记忆存储，包括id，内容和时间戳
    private String id;

    private String content;

    private long timestamp;

}
