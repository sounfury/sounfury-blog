package org.sounfury.aki.domain.prompt.context;

import lombok.Builder;
import lombok.Getter;
import org.sounfury.aki.domain.prompt.persona.Persona;

/**
 * 角色上下文
 * 用于模板中的 {{char.xxx}} 占位符
 */
@Getter
@Builder
public class CharCtx {

    /**
     * 角色名称
     * 模板占位符: {{char.name}}
     */
    private final String name;

    /**
     * 角色人设
     * 模板占位符: {{char.persona}}
     */
    private final String persona;

    /**
     * 世界设定
     * 模板占位符: {{char.worldScenario}}
     */
    private final String worldScenario;

    /**
     * 角色问候语
     * 模板占位符: {{char.greeting}}
     */
    private final String greeting;

    /**
     * 示例对话
     * 模板占位符: {{char.exampleDialogue}}
     */
    private final String exampleDialogue;

    /**
     * 角色ID（可选）
     * 模板占位符: {{char.id}}
     */
    private final String id;

    /**
     * 从Character领域对象创建上下文
     */
    public static CharCtx fromCharacter(Persona persona) {
        if (persona == null || persona.getCard() == null) {
            return empty();
        }

        return CharCtx.builder()
                .id(persona.getId().getValue())
                .name(persona.getCard().getCharName())
                .persona(persona.getCard().getCharPersona())
                .worldScenario(persona.getCard().getWorldScenario())
                .greeting(persona.getCard().getCharGreeting())
                .exampleDialogue(persona.getCard().getExampleDialogue())
                .build();
    }

    /**
     * 创建空的角色上下文
     */
    public static CharCtx empty() {
        return CharCtx.builder().build();
    }

    /**
     * 检查是否有有效的角色数据
     */
    public boolean hasValidData() {
        return name != null && !name.trim().isEmpty();
    }
}
