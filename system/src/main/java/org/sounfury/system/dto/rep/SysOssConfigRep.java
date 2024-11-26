package org.sounfury.system.dto.rep;

import org.jooq.types.UInteger;

import java.time.LocalDateTime;

public class SysOssConfigRep {
    private UInteger ossConfigId;
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
    private UInteger createBy;
    private LocalDateTime createTime;
    private UInteger updateBy;
    private LocalDateTime updateTime;
    private String remark;
    private Byte enableStatus;
}
