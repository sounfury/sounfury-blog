package org.sounfury.system.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.pojos.SysOssConfig;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = SysOssConfig.class)
public class SysOssConfigReq {
    @NotNull(message = "主键不能为空")
    private Long ossConfigId;
    @NotBlank(message = "配置key不能为空")
    @Size(min = 2, max = 100, message = "configKey长度必须介于{min}和{max} 之间")
    private String configKey;
    @NotBlank(message = "accessKey不能为空")
    private String accessKey;
    @NotBlank(message = "secretKey不能为空")
    private String secretKey;
    @NotBlank(message = "bucketName不能为空")
    private String bucketName;
    /**
     * 前缀，区分bucket里面的文件夹
     */
    private String prefix;
    @NotBlank(message = "endpoint不能为空")
    private String endpoint;
    private String domain;
    private Byte isHttps;
    @NotBlank(message = "地域不能为空")
    private String region;
    private Byte accessPolicy;

    /**
     * 是否启用
     */
    private Byte enableStatus;

}
