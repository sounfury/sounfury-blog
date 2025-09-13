package org.sounfury.aki.domain.prompt.persona;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.prompt.persona.event.CharacterCreated;
import org.sounfury.aki.domain.prompt.persona.event.CharacterUpdated;
import org.sounfury.aki.domain.prompt.persona.event.CharacterDeleted;
import org.sounfury.aki.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * 角色卡封面
     */
    private final String cardCover;
    
    /**
     * 领域事件记录容器
     */
    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 创建新角色
     */
    public static Persona create(PersonaId id, String name, PersonaCard card, String description, String cardCover) {
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
        Persona newPersona = Persona
                .builder()
                .id(id)
                .name(name.trim())
                .card(card)
                .description(description)
                .cardCover(cardCover)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        // 记录创建事件
        CharacterCreated createEvent = CharacterCreated.builder()
                .characterId(id.getValue())
                .characterName(name.trim())
                .description(description)
                .timestamp(now)
                .build();
        newPersona.recordEvent(createEvent);
        
        return newPersona;
    }
    
    /**
     * 更新角色卡
     */
    public Persona updateCard(PersonaCard newCard) {
        if (newCard == null || !newCard.isValid()) {
            throw new IllegalArgumentException("新角色卡信息无效");
        }
        
        LocalDateTime now = LocalDateTime.now();
        Persona updatedPersona = Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(newCard)
                .description(this.description)
                .cardCover(this.cardCover)
                .enabled(this.enabled)
                .createdAt(this.createdAt)
                .updatedAt(now)
                .worldBookId(this.worldBookId)
                .build();
        
        // 记录角色卡更新事件
        CharacterUpdated updateEvent = CharacterUpdated.builder()
                .characterId(this.id.getValue())
                .characterName(this.name)
                .updateType("card_updated")
                .timestamp(now)
                .build();
        updatedPersona.recordEvent(updateEvent);
        
        return updatedPersona;
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
                .cardCover(this.cardCover)
                .enabled(true)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .worldBookId(this.worldBookId)
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
                .cardCover(this.cardCover)
                .enabled(false)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .worldBookId(this.worldBookId)
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
    
    /**
     * 更新角色名称
     */
    public Persona updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
        
        return Persona
                .builder()
                .id(this.id)
                .name(newName.trim())
                .card(this.card)
                .description(this.description)
                .cardCover(this.cardCover)
                .enabled(this.enabled)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .worldBookId(this.worldBookId)
                .build();
    }
    
    /**
     * 更新角色描述
     */
    public Persona updateDescription(String newDescription) {
        return Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(this.card)
                .description(newDescription)
                .cardCover(this.cardCover)
                .enabled(this.enabled)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .worldBookId(this.worldBookId)
                .build();
    }
    
    /**
     * 更新世界书
     */
    public Persona updateWorldBook(String newWorldBookId) {
        return Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(this.card)
                .description(this.description)
                .cardCover(this.cardCover)
                .enabled(this.enabled)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .worldBookId(newWorldBookId)
                .build();
    }
    
    /**
     * 更新角色卡封面
     */
    public Persona updateCardCover(String newCardCover) {
        return Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(this.card)
                .description(this.description)
                .cardCover(newCardCover)
                .enabled(this.enabled)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .worldBookId(this.worldBookId)
                .build();
    }
    
    /**
     * 标记禁用
     */
    public Persona markDeleted() {
        LocalDateTime now = LocalDateTime.now();
        Persona deletedPersona = Persona
                .builder()
                .id(this.id)
                .name(this.name)
                .card(this.card)
                .description(this.description)
                .cardCover(this.cardCover)
                .enabled(false)
                .createdAt(this.createdAt)
                .updatedAt(now)
                .worldBookId(this.worldBookId)
                .build();
        
        // 记录删除事件
        CharacterDeleted deleteEvent = CharacterDeleted.builder()
                .characterId(this.id.getValue())
                .characterName(this.name)
                .timestamp(now)
                .build();
        deletedPersona.recordEvent(deleteEvent);
        
        return deletedPersona;
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
}
