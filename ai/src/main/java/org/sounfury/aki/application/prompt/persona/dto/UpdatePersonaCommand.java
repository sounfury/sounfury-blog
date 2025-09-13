package org.sounfury.aki.application.prompt.persona.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新角色命令
 */
@Data
public class UpdatePersonaCommand {
    
    /**
     * 角色ID
     */
    @NotBlank(message = "角色ID不能为空")
    private String personaId;
    
    /**
     * 角色名称
     */
    @Size(max = 100, message = "角色名称长度不能超过100字符")
    private String name;
    
    /**
     * 角色描述
     */
    @Size(max = 500, message = "角色描述长度不能超过500字符")
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
     * 角色卡信息
     */
    @Valid
    private PersonaCardDto card;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 角色卡DTO
     */
    @Data
    public static class PersonaCardDto {
        
        /**
         * 角色名称
         */
        @Size(max = 100, message = "角色卡中的角色名称长度不能超过100字符")
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
    }
    
    /**
     * 检查是否有名称变更
     */
    public boolean hasNameChange() {
        return name != null && !name.trim().isEmpty();
    }
    
    /**
     * 检查是否有描述变更
     */
    public boolean hasDescriptionChange() {
        return description != null;
    }
    
    /**
     * 检查是否有角色卡变更
     */
    public boolean hasCardChange() {
        return card != null;
    }
    
    /**
     * 检查是否有启用状态变更
     */
    public boolean hasEnabledChange() {
        return enabled != null;
    }
    
    /**
     * 检查是否有世界书变更
     */
    public boolean hasWorldBookChange() {
        return worldBookId != null;
    }
    
    /**
     * 检查是否有封面变更
     */
    public boolean hasCardCoverChange() {
        return cardCover != null;
    }
}
