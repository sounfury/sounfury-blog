package org.sounfury.system.dto.rep;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysOssConfigRep {
    private Long ossConfigId;
    private String configKey;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String prefix;
    private String endpoint;
    private String domain;
    private Byte isHttps;
    private String region;
    private Byte accessPolicy;
    //上次修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String remark;
}
