package org.sounfury.aki.application.prompt.persona.dto;

import lombok.Builder;
import lombok.Data;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaCard;

import java.time.LocalDateTime;

/**
 * 角色详细信息响应DTO
 */
@Data
@Builder
public class PersonaDetailResponse {
    
    /**
     * 角色ID
     */
    private String personaId;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 关联的世界书ID
     */
    private String worldBookId;
    
    /**
     * 角色卡封面URL
     */
    private String cardCover;
    
    /**
     * 是否启用
     */
    private boolean enabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 角色卡信息
     */
    private PersonaCardDetail card;
    
    /**
     * 角色卡详细信息
     */
    @Data
    @Builder
    public static class PersonaCardDetail {
        
        /**
         * 角色名称
         */
        private String charName;
        
        /**
         * 角色人设描述
         */
        private String charPersona;
        
        /**
         * 世界设定/场景描述
         */
        private String worldScenario;
        
        /**
         * 角色开场白
         */
        private String charGreeting;
        
        /**
         * 示例对话
         */
        private String exampleDialogue;
        
        /**
         * 从领域对象转换
         */
        public static PersonaCardDetail from(PersonaCard card) {
            if (card == null) {
                return null;
            }
            
            return PersonaCardDetail.builder()
                    .charName(card.getCharName())
                    .charPersona(card.getCharPersona())
                    .worldScenario(card.getWorldScenario())
                    .charGreeting(card.getCharGreeting())
                    .exampleDialogue(card.getExampleDialogue())
                    .build();
        }
    }
    
    /**
     * 从领域对象转换
     */
    public static PersonaDetailResponse from(Persona persona) {
        if (persona == null) {
            return null;
        }
        
        return PersonaDetailResponse.builder()
                .personaId(persona.getId().getValue())
                .name(persona.getName())
                .description(persona.getDescription())
                .worldBookId(persona.getWorldBookId())
                .cardCover(persona.getCardCover())
                .enabled(persona.isEnabled())
                .createdAt(persona.getCreatedAt())
                .updatedAt(persona.getUpdatedAt())
                .card(PersonaCardDetail.from(persona.getCard()))
                .build();
    }
}
