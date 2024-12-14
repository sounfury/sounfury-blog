package org.sounfury.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sounfury.admin.model.ThemeSetting;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ThemeReq {
    private Integer themeId;

    @NotBlank(message = "主题key不能为空")
    private String themeKey;
    private String themeName;
    private ThemeSetting settings;
    private String description;
    private Byte mode; //是否启用头图
    private Byte enableStatus;
}
