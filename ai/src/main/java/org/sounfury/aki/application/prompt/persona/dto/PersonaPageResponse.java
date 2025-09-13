package org.sounfury.aki.application.prompt.persona.dto;

import lombok.Builder;
import lombok.Data;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.jooq.page.PageRepDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色分页查询响应DTO
 */
@Data
@Builder
public class PersonaPageResponse {
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 角色列表
     */
    private List<PersonaItem> personas;
    
    /**
     * 角色列表项
     */
    @Data
    @Builder
    public static class PersonaItem {
        
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
         * 角色卡中的角色名称
         */
        private String charName;
        
        /**
         * 角色开场白（预览用）
         */
        private String charGreeting;
        
        /**
         * 从领域对象转换
         */
        public static PersonaItem from(Persona persona) {
            if (persona == null) {
                return null;
            }
            
            String charName = null;
            String charGreeting = null;
            
            if (persona.getCard() != null) {
                charName = persona.getCard().getCharName();
                charGreeting = persona.getCard().getCharGreeting();
            }
            
            return PersonaItem.builder()
                    .personaId(persona.getId().getValue())
                    .name(persona.getName())
                    .description(persona.getDescription())
                    .enabled(persona.isEnabled())
                    .createdAt(persona.getCreatedAt())
                    .updatedAt(persona.getUpdatedAt())
                    .charName(charName)
                    .charGreeting(charGreeting)
                    .cardCover(persona.getCardCover())
                    .build();
        }
    }
    
    /**
     * 从分页结果转换
     */
    public static PersonaPageResponse from(PageRepDto<List<Persona>> pageResult) {
        if (pageResult == null || pageResult.getData() == null) {
            return PersonaPageResponse.builder()
                    .total(0)
                    .personas(List.of())
                    .build();
        }
        
        List<PersonaItem> personas = pageResult.getData().stream()
                .map(PersonaItem::from)
                .collect(Collectors.toList());
        
        return PersonaPageResponse.builder()
                .total(pageResult.getTotal())
                .personas(personas)
                .build();
    }
    
    /**
     * 创建空结果
     */
    public static PersonaPageResponse empty() {
        return PersonaPageResponse.builder()
                .total(0)
                .personas(List.of())
                .build();
    }
}
