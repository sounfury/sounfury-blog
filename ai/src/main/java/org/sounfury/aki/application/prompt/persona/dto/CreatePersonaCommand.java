package org.sounfury.aki.application.prompt.persona.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建角色命令
 */
@Data
public class CreatePersonaCommand {
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称长度不能超过100字符")
    private String name;
    
    /**
     * 角色描述
     */
    @Size(max = 500, message = "角色描述长度不能超过500字符")
    private String description;
    
    /**
     * 关联的世界书ID，选填
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
     * 角色卡DTO
     */
    @Data
    public static class PersonaCardDto {
        
        /**
         * 角色名称
         */
        @NotBlank(message = "角色卡中的角色名称不能为空")
        @Size(max = 100, message = "角色卡中的角色名称长度不能超过100字符")
        private String charName;
        
        /**
         * 角色人设描述
         */
        @NotBlank(message = "角色人设描述不能为空")
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
}
