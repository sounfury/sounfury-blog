package org.sounfury.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sounfury.admin.model.ThemeSetting;

@Data
@AllArgsConstructor
public class ThemeReq {
    private String themeKey;
    private String themeName;
    private ThemeSetting settings;
    private String description;
    private Byte mode;
}
