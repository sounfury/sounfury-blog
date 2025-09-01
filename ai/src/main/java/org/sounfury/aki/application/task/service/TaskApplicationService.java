package org.sounfury.aki.application.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.task.TaskOrchestrationFactory;
import org.sounfury.aki.application.task.dto.BaseTaskRequest;
import org.sounfury.aki.application.task.dto.TaskResponse;
import org.sounfury.aki.domain.task.strategy.TaskStrategy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 任务应用服务
 * 统一处理各种任务功能，被API层直接调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskApplicationService {
    
    private final TaskOrchestrationFactory orchestrationFactory;
    
    /**
     * 执行任务
     * @param request 任务请求
     * @return 任务响应
     */
    public TaskResponse executeTask(BaseTaskRequest request) {
        try {
            log.info("处理任务请求，任务模式: {}, 用户: {}", 
                    request.getTaskMode(), request.getUserName());
            
            // 1. 获取任务策略
            TaskStrategy strategy = orchestrationFactory.getStrategy(request.getTaskMode());
            
            // 2. 执行策略
            TaskStrategy.TaskResult result = strategy.execute(request);
            
            if (result.success()) {
                log.info("任务处理成功，任务模式: {}", request.getTaskMode());
                
                return TaskResponse.success(
                        result.response(),
                        request.getTaskMode(),
                        result.strategyName()
                );
            } else {
                log.error("任务处理失败，任务模式: {}, 错误: {}", 
                        request.getTaskMode(), result.errorMessage());
                return TaskResponse.failure(result.errorMessage(), request.getTaskMode());
            }
            
        } catch (Exception e) {
            log.error("任务处理异常，任务模式: {}", request.getTaskMode(), e);
            return TaskResponse.failure("服务异常: " + e.getMessage(), request.getTaskMode());
        }
    }
    
    /**
     * 执行流式任务
     * @param request 任务请求
     * @return 流式任务响应
     */
    public Flux<String> executeTaskStream(BaseTaskRequest request) {
        try {
            log.info("处理流式任务请求，任务模式: {}, 用户: {}", 
                    request.getTaskMode(), request.getUserName());
            
            // 1. 获取任务策略
            TaskStrategy strategy = orchestrationFactory.getStrategy(request.getTaskMode());
            
            // 2. 执行流式策略
            return strategy.executeStream(request)
                    .doOnComplete(() -> {
                        log.info("流式任务完成，任务模式: {}", request.getTaskMode());
                    })
                    .doOnError(error -> {
                        log.error("流式任务处理失败，任务模式: {}", request.getTaskMode(), error);
                    });
            
        } catch (Exception e) {
            log.error("流式任务处理异常，任务模式: {}", request.getTaskMode(), e);
            return Flux.error(e);
        }
    }
}
