package org.sounfury.aki.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.task.enums.TaskMode;
import org.sounfury.aki.domain.task.strategy.TaskStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务编排工厂
 * 根据任务模式返回对应的策略实例
 * 使用Spring依赖注入机制自动装配策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskOrchestrationFactory {

    /**
     * key为Bean名称，value为策略实例
     */
    private final Map<String, TaskStrategy> taskStrategies;

    /**
     * 根据任务模式获取对应的策略
     * @param taskMode 任务模式
     * @return 对应的策略实例
     */
    public TaskStrategy getStrategy(TaskMode taskMode) {
        log.debug("获取任务策略，任务模式: {}", taskMode);

        if (taskMode == null) {
            throw new IllegalArgumentException("任务模式不能为空");
        }

        // 根据任务模式代码获取对应的策略Bean
        String strategyBeanName = getStrategyBeanName(taskMode);
        TaskStrategy strategy = taskStrategies.get(strategyBeanName);

        if (strategy == null) {
            log.error("未找到任务模式对应的策略: taskMode={}, beanName={}", taskMode, strategyBeanName);
            throw new IllegalArgumentException("不支持的任务模式: " + taskMode);
        }

        log.debug("找到任务策略: taskMode={}, strategyClass={}", taskMode, strategy.getClass().getSimpleName());
        return strategy;
    }

    /**
     * 根据任务模式获取策略Bean名称
     * @param taskMode 任务模式
     * @return 策略Bean名称
     */
    private String getStrategyBeanName(TaskMode taskMode) {
        return switch (taskMode) {
            case ARTICLE_SUMMARY, ARTICLE_EXCERPT -> "articleTaskStrategy";
            case PUBLISH_CONGRATULATION, LOGIN_WELCOME -> "companionTaskStrategy";
        };
    }

    /**
     * 获取所有可用的策略
     * @return 策略映射
     */
    public Map<String, TaskStrategy> getAllStrategies() {
        return Map.copyOf(taskStrategies);
    }

    /**
     * 检查是否支持指定的任务模式
     * @param taskMode 任务模式
     * @return 如果支持返回true，否则返回false
     */
    public boolean isSupported(TaskMode taskMode) {
        if (taskMode == null) {
            return false;
        }

        try {
            String strategyBeanName = getStrategyBeanName(taskMode);
            return taskStrategies.containsKey(strategyBeanName);
        } catch (Exception e) {
            return false;
        }
    }
}
