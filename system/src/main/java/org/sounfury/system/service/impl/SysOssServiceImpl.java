package org.sounfury.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.core.convention.exception.ServiceException;
import org.sounfury.core.utils.StringUtils;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.tables.pojos.SysOss;
import org.sounfury.oss.core.OssClient;
import org.sounfury.oss.entity.UploadResult;
import org.sounfury.oss.enums.AccessPolicyType;
import org.sounfury.system.dto.SysOssDto;
import org.sounfury.system.dto.req.SysOssReq;
import org.sounfury.system.repository.SysOssRepository;
import org.sounfury.system.service.SysOssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sounfury.oss.factory.OssFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl implements SysOssService {
    private final SysOssRepository sysOssRepository;


    @Override
    public PageRepDto<SysOssDto> queryPageList(SysOssReq sysOss, PageReqDto pageQuery) {
        return null;
    }

    @Override
    public List<SysOssDto> listByIds(Collection<Long> ossIds) {
        return List.of();
    }

    @Override
    public SysOssDto getById(Long ossId) {
        SysOss sysOss = sysOssRepository.fetchOneByOssId(UInteger.valueOf(ossId));
        return BeanUtil.copyProperties(sysOss, SysOssDto.class);
    }

    @Override
    public SysOssDto upload(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalFileName, originalFileName.lastIndexOf("."), originalFileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        // 保存文件信息
        return buildResultEntity(originalFileName, suffix, storage.getConfigKey(), uploadResult);
    }

    @NotNull
    private SysOssDto buildResultEntity(String originalFileName, String suffix, String configKey, UploadResult uploadResult) {
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalFileName);
        oss.setService(configKey);
        sysOssRepository.insert(oss);
        SysOssDto sysOssVo = BeanUtil.copyProperties(oss, SysOssDto.class);
        return this.matchingUrl(sysOssVo);
    }

    /**
     * 桶类型为 private 的URL 修改为临时URL时长为120s
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private SysOssDto matchingUrl(SysOssDto oss) {
        OssClient storage = OssFactory.instance();
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
        }
        return oss;
    }

    @Override
    public SysOssDto upload(File file) {
        return null;
    }

    @Override
    public void download(Long ossId, HttpServletResponse response) throws IOException {

    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return null;
    }
}
