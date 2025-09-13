package org.sounfury.aki.application.prompt.persona.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 删除角色命令
 */
@Data
public class DeletePersonaCommand {
    
    /**
     * 角色ID
     */
    @NotBlank(message = "角色ID不能为空")
    private String personaId;
    
    /**
     * 创建删除命令
     */
    public static DeletePersonaCommand of(String personaId) {
        DeletePersonaCommand command = new DeletePersonaCommand();
        command.setPersonaId(personaId);
        return command;
    }
}
