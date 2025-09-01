package org.sounfury.aki.application.task.dto;

import lombok.Data;
import org.sounfury.aki.domain.task.enums.TaskMode;

/**
 * 任务请求基类
 * 包含所有任务的公共字段
 */
@Data
public abstract class BaseTaskRequest {
    
    /**
     * 用户名（从登录上下文获取）
     */
    private String userName;
    
    /**
     * 是否为站长（从登录上下文获取）
     */
    private Boolean isOwner;
    
    /**
     * 任务模式
     */
    private TaskMode taskMode;

    /**
     * 上下文信息（前端传递的固定信息）
     */
    private String contextInfo;
}
