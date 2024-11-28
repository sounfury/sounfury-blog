package org.sounfury.system.dto.rep;


import java.time.LocalDateTime;

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
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
    private String remark;
    private Byte enableStatus;
}
