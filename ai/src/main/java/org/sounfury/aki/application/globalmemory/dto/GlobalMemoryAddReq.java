package org.sounfury.aki.application.globalmemory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增全局记忆请求
 */
@Data
public class GlobalMemoryAddReq {

    @NotBlank(message = "记忆内容不能为空")
    @Size(max = 2000, message = "记忆内容长度不能超过2000个字符")
    private String content;
}
