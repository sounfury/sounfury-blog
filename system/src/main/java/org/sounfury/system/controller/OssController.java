package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.system.dto.SysOssDto;
import org.sounfury.system.dto.rep.SysOssUploadRep;
import org.sounfury.system.service.SysOssService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource/oss")
public class OssController {
    private final SysOssService ossService;

    //    @SaCheckPermission("system:oss:upload")
    @SaCheckRole("ADMIN")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<SysOssUploadRep> upload(@RequestPart("file") MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
           throw new ClientException("上传文件不能为空");
        }
        SysOssDto oss = ossService.upload(file);
        System.out.println(oss);
        SysOssUploadRep uploadVo = new SysOssUploadRep();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        return Results.success(uploadVo);
    }
}
