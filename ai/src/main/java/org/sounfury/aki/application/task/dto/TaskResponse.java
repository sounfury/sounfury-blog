package org.sounfury.aki.application.task.dto;

import lombok.Builder;
import lombok.Data;
import org.sounfury.aki.domain.task.enums.TaskMode;

/**
 * 任务响应DTO
 */
@Data
@Builder
public class TaskResponse {
    
    /**
     * 响应内容
     */
    private String response;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 任务模式
     */
    private TaskMode taskMode;
    
    /**
     * 策略名称
     */
    private String strategyName;
    
    /**
     * 成功响应
     */
    public static TaskResponse success(String response, TaskMode taskMode, String strategyName) {
        return TaskResponse.builder()
                .response(response)
                .success(true)
                .taskMode(taskMode)
                .strategyName(strategyName)
                .build();
    }
    
    /**
     * 失败响应
     */
    public static TaskResponse failure(String errorMessage, TaskMode taskMode) {
        return TaskResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .taskMode(taskMode)
                .build();
    }
}
