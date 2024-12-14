package org.sounfury.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemeSetting implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String themeKey;

    @JsonProperty("fontFamily")
    private String fontFamily;

    @JsonProperty("HeroImageDay")
    private String HeroImageDay;

    @JsonProperty("HeroImageNight")
    private String HeroImageNight;

    @JsonProperty("TypingEffectFirstLine")
    private String TypingEffectFirstLine;

    @JsonProperty("TypingEffectSecondLine")
    private String TypingEffectSecondLine;

}