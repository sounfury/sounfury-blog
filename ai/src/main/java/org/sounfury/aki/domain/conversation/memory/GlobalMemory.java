package org.sounfury.aki.domain.conversation.memory;

import lombok.Data;
import org.sounfury.aki.domain.conversation.memory.event.GlobalMemoryChangeEvent;
import org.sounfury.aki.domain.conversation.memory.event.GlobalMemoryOperationType;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局记忆领域模型（充血模型）
 * 用于存储跨会话的持久化记忆内容，管理自身的业务逻辑和领域事件
 */
@Data
public class GlobalMemory {

    private Long id;
    private String content;
    private Long timestamp;
    
    /**
     * 领域事件记录容器
     */
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public GlobalMemory() {}

    public GlobalMemory(String content, Long timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public GlobalMemory(Long id, String content, Long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * 创建新的全局记忆
     * 产生GlobalMemoryCreatedEvent领域事件
     */
    public static GlobalMemory create(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("记忆内容不能为空");
        }
        
        GlobalMemory memory = new GlobalMemory(content.trim(), System.currentTimeMillis());
        
        // 产生创建事件
        memory.recordEvent(new GlobalMemoryChangeEvent(GlobalMemoryOperationType.CREATE));
        
        return memory;
    }

    /**
     * 检查记忆是否有效
     */
    public boolean isValid() {
        return content != null && !content.trim().isEmpty() && timestamp != null;
    }

    /**
     * 更新记忆内容
     * 产生GlobalMemoryUpdatedEvent领域事件
     */
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("记忆内容不能为空");
        }
        
        // 保存旧内容用于事件
        String oldContent = this.content;
        
        // 更新内容
        this.content = newContent.trim();
        this.timestamp = System.currentTimeMillis();
        
        // 产生更新事件
        this.recordEvent(new GlobalMemoryChangeEvent(GlobalMemoryOperationType.UPDATE));
    }

    /**
     * 记录领域事件
     */
    private void recordEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 获取所有领域事件
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    /**
     * 清除领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @Override
    public String toString() {
        return "GlobalMemory{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", domainEvents.size=" + domainEvents.size() +
                '}';
    }
}