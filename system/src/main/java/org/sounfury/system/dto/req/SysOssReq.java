package org.sounfury.system.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysOssReq {
    /**
     * 对象存储主键
     */
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 服务商
     */
    private String service;


}
