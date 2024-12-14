package org.sounfury.admin.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sounfury.admin.model.ThemeSetting;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThemeRep implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String themeKey;
    private String themeName;
    private ThemeSetting settings;
    private String description;
    private Byte mode;
    private Byte enableStatus;


}
