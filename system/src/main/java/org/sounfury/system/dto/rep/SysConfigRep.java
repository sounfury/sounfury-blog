package org.sounfury.system.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysConfigRep {
    private Integer configId;
    private String configName;
    private String configKey;
    private String configValue;
    private String description;
}
