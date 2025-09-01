package org.sounfury.aki.domain.task.strategy;

import org.sounfury.aki.application.task.dto.BaseTaskRequest;
import reactor.core.publisher.Flux;

/**
 * 任务策略接口
 * 定义任务执行的统一接口
 */
public interface TaskStrategy {
    
    /**
     * 执行任务
     * @param request 任务请求
     * @return 任务结果
     */
    TaskResult execute(BaseTaskRequest request);
    
    /**
     * 执行流式任务
     * @param request 任务请求
     * @return 流式任务结果
     */
    Flux<String> executeStream(BaseTaskRequest request);
    
    /**
     * 任务结果
     */
    record TaskResult(
            String response,
            boolean success,
            String errorMessage,
            String strategyName
    ) {
        public static TaskResult success(String response, String strategyName) {
            return new TaskResult(response, true, null, strategyName);
        }
        
        public static TaskResult failure(String errorMessage, String strategyName) {
            return new TaskResult(null, false, errorMessage, strategyName);
        }
    }
}
