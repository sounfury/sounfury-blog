package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.ObjectUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.system.dto.SysOssDto;
import org.sounfury.system.dto.rep.SysOssConfigRep;
import org.sounfury.system.dto.rep.SysOssUploadRep;
import org.sounfury.system.dto.req.SysOssConfigReq;
import org.sounfury.system.dto.req.SysOssReq;
import org.sounfury.system.service.SysOssConfigService;
import org.sounfury.system.service.SysOssService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class OssController {
    private final SysOssService ossService;
    private final SysOssConfigService ossConfigService;

    /**
     * 通用上传
     * @param file
     * @return
     */
    @PostMapping(value = "/common/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    /**
     * 获取oss配置，简单业务默认获取第一个
     */
    @GetMapping("/oss/config")
    public Result<SysOssConfigRep> getOssConfig() {
        return Results.success(ossConfigService.queryById(1L));
    }

    /**
     * 修改Oss配置
     */
    @PutMapping("/oss/config")
    public Result<Boolean> updateOssConfig(@RequestBody @Valid SysOssConfigReq req) {
        return Results.success(ossConfigService.update(req));
    }




}
