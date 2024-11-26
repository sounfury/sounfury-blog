package org.sounfury.system.dto.rep;

import lombok.Data;

@Data
public class SysOssUploadRep {

    /**
     * URL地址
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;
}
