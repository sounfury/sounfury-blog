package org.sounfury.system.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysConfigReq {
    private String configKey;
    private String configValue;
}
