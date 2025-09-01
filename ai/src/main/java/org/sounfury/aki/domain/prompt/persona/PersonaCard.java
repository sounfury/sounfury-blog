package org.sounfury.aki.domain.prompt.persona;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * 角色卡值对象
 * 包含角色的所有设定信息
 */
@Value
public class PersonaCard {

    /**
     * 角色名称
     */
    @JsonProperty("char_name")
    String charName;

    /**
     * 角色人设描述，核心部分
     */
    @JsonProperty("char_persona")
    String charPersona;

    /**
     * 世界设定/场景描述
     */
    @JsonProperty("world_scenario")
    String worldScenario;

    /**
     * 角色开场白
     */
    @JsonProperty("char_greeting")
    String charGreeting;

    /**
     * 示例对话
     */
    @JsonProperty("example_dialogue")
    String exampleDialogue;

    /**
     * 创建角色卡
     */
    public static PersonaCard of(String charName, String charPersona, String worldScenario,
                                 String charGreeting, String exampleDialogue) {
        return new PersonaCard(charName, charPersona, worldScenario, charGreeting, exampleDialogue);
    }

    /**
     * 检查角色卡是否有效
     */
    public boolean isValid() {
        return charName != null && !charName.trim().isEmpty() &&
               charPersona != null && !charPersona.trim().isEmpty();
    }
}
