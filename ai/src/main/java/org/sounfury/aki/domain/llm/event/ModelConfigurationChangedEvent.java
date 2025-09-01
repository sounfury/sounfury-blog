package org.sounfury.aki.domain.llm.event;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;

/**
 * LLM配置变更事件
 * 当LLM配置发生变更时发布此事件，触发ChatClient重建
 */
@Getter
@Builder
public class ModelConfigurationChangedEvent extends DomainEvent {
    
    /**
     * 配置ID
     */
    private final Integer configurationId;
    
    /**
     * 旧配置
     */
    private final ModelConfiguration oldConfiguration;
    
    /**
     * 新配置
     */
    private final ModelConfiguration newConfiguration;
    
    /**
     * 变更时间
     */
    private final LocalDateTime changeTime;
    
    /**
     * 变更类型
     */
    private final ChangeType changeType;
    
    /**
     * 变更原因
     */
    private final String changeReason;
    
    public ModelConfigurationChangedEvent(Integer configurationId,
                                        ModelConfiguration oldConfiguration,
                                        ModelConfiguration newConfiguration,
                                        LocalDateTime changeTime,
                                        ChangeType changeType,
                                        String changeReason) {
        super("ModelConfigurationChanged");
        this.configurationId = configurationId;
        this.oldConfiguration = oldConfiguration;
        this.newConfiguration = newConfiguration;
        this.changeTime = changeTime;
        this.changeType = changeType;
        this.changeReason = changeReason;
    }
    
    /**
     * 配置变更类型
     */
    public enum ChangeType {
        PROVIDER_CHANGED("提供商变更"),
        SETTINGS_CHANGED("设置变更"),
        ENABLED_CHANGED("启用状态变更"),
        FULL_UPDATE("完整更新");
        
        private final String description;
        
        ChangeType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 检查是否需要重建ChatClient
     * @return 如果需要重建返回true
     */
    public boolean requiresChatClientRebuild() {
        return changeType == ChangeType.PROVIDER_CHANGED || 
               changeType == ChangeType.FULL_UPDATE ||
               (changeType == ChangeType.ENABLED_CHANGED && newConfiguration.isEnabled());
    }
    
    /**
     * 获取变更摘要
     * @return 变更摘要信息
     */
    public String getChangeSummary() {
        if (oldConfiguration == null) {
            return String.format("新建配置: %s", newConfiguration.getProvider().getDisplayName());
        }
        
        return String.format("配置变更: %s -> %s (%s)", 
                oldConfiguration.getProvider().getDisplayName(),
                newConfiguration.getProvider().getDisplayName(),
                changeType.getDescription());
    }
}
