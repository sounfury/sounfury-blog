package org.sounfury.aki.application.task.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 陪伴任务请求
 * 用于发布祝贺、登录欢迎等任务
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanionTaskRequest extends BaseTaskRequest {

}
