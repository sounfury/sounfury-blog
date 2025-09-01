package org.sounfury.aki.domain.prompt.persona;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 角色聚合根
 * 管理角色的完整生命周期和业务规则
 */
@Getter
@Builder
public class Persona {
    
    /**
     * 角色唯一标识
     */
    private final PersonaId id;
    
    /**
     * 角色名称
     */
    private final String name;
    
    /**
     * 角色卡信息
     */
    private final PersonaCard card;
    
    /**
     * 是否启用
     */
    private final boolean enabled;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private final LocalDateTime updatedAt;
    
    /**
     * 角色描述
     */
    private final String description;

    /**
     * 关联的世界书id
     */
    private final String worldBookId;

    /**
     * 创建新角色
     */
    public static Persona create(PersonaId id, String name, PersonaCard card, String description) {
        if (id == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
        if (card == null || !card.isValid()) {
            throw new IllegalArgumentException("角色卡信息无效");
        }
        
        LocalDateTime now = LocalDateTime.now();
        return Persona
                .builder()
                .id(id)
                .name(name.trim())
                .card(card)
                .description(description)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    /**
     * 更新角色卡
     */
    public Persona updateCard(PersonaCard newCard) {
        if (newCard == null || !newCard.isValid()) {
            throw new IllegalArgumentException("新角色卡信息无效");
        }
        
        return Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(newCard)
                .description(this.description)
                .enabled(this.enabled)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 启用角色
     */
    public Persona enable() {
        if (this.enabled) {
            return this;
        }
        
        return Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(this.card)
                .description(this.description)
                .enabled(true)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 禁用角色
     */
    public Persona disable() {
        if (!this.enabled) {
            return this;
        }
        
        return Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(this.card)
                .description(this.description)
                .enabled(false)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 检查角色是否可用
     */
    public boolean isAvailable() {
        return enabled && card != null && card.isValid();
    }

    public String getGreeting() {
        return card != null ? card.getCharGreeting() : "你好！我是你的AI助手，有什么可以帮助你的吗？";
    }
}
