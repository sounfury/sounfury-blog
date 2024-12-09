package org.sounfury.admin.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sounfury.admin.model.ThemeSetting;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemeRep {
    private Integer themeId;
    private String themeKey;
    private String themeName;
    private ThemeSetting settings;
    private String description;
    private Byte mode;
}
