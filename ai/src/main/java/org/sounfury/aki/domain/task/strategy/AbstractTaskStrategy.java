package org.sounfury.aki.domain.task.strategy;

import org.sounfury.aki.domain.prompt.persona.Persona;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.task.dto.BaseTaskRequest;
import org.sounfury.aki.domain.llm.service.CallLlmService;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import reactor.core.publisher.Flux;

/**
 * 任务策略抽象基类
 * 提供模板方法实现，子类只需实现具体的业务逻辑
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractTaskStrategy implements TaskStrategy {

    private final CallLlmService callLlmService;
    private final PromptAssemblyService promptAssemblyService;
    private final CharacterRepository characterRepository;

    @Override
    public TaskResult execute(BaseTaskRequest request) {
        try {
            log.debug("执行任务策略: {}, 任务模式: {}", getStrategyName(), request.getTaskMode());

            // 1. 验证请求
            if (!isValidRequest(request)) {
                String errorMsg = String.format("请求类型错误，期望: %s", getExpectedRequestType());
                log.error(errorMsg);
                return TaskResult.failure(errorMsg, getStrategyName());
            }

            // 2. 获取用户输入
            String userInput = getUserInput(request);
            if (userInput == null || userInput.trim().isEmpty()) {
                String errorMsg = "用户输入内容为空";
                log.error(errorMsg);
                return TaskResult.failure(errorMsg, getStrategyName());
            }

            // 3. 获取任务特定提示词
            String taskSpecificPrompt = getTaskSpecificPrompt(request);

            // 4. 调用LLM服务
            String response = callLlmService.sendTaskMessage(taskSpecificPrompt, userInput);

            log.debug("任务执行成功: {}", getStrategyName());
            return TaskResult.success(response, getStrategyName());

        } catch (Exception e) {
            log.error("任务执行异常: {}", getStrategyName(), e);
            return TaskResult.failure("任务执行异常: " + e.getMessage(), getStrategyName());
        }
    }

    @Override
    public Flux<String> executeStream(BaseTaskRequest request) {
        try {
            log.debug("执行流式任务策略: {}, 任务模式: {}", getStrategyName(), request.getTaskMode());

            // 1. 验证请求
            if (!isValidRequest(request)) {
                String errorMsg = String.format("请求类型错误，期望: %s", getExpectedRequestType());
                log.error(errorMsg);
                return Flux.error(new IllegalArgumentException(errorMsg));
            }

            // 2. 获取用户输入
            String userInput = getUserInput(request);
            if (userInput == null || userInput.trim().isEmpty()) {
                String errorMsg = "用户输入内容为空";
                log.error(errorMsg);
                return Flux.error(new IllegalArgumentException(errorMsg));
            }

            // 3. 获取任务特定提示词（如果需要的话）
            String taskSpecificPrompt = getTaskSpecificPrompt(request);

            // 4. 调用流式LLM服务（TaskClient已包含基础Advisor）
            return callLlmService.sendTaskMessageStream(taskSpecificPrompt, userInput)
                    .doOnComplete(() -> log.debug("流式任务执行完成: {}", getStrategyName()))
                    .doOnError(error -> log.error("流式任务执行异常: {}", getStrategyName(), error));

        } catch (Exception e) {
            log.error("流式任务执行异常: {}", getStrategyName(), e);
            return Flux.error(e);
        }
    }

    /**
     * 获取任务特定提示词
     * 子类可以重写此方法来提供特定的任务提示词
     */
    protected String getTaskSpecificPrompt(BaseTaskRequest request) {
        try {
            String taskCode = request.getTaskMode().getCode();
            String userInput = getUserInput(request);

            // 使用新的组装服务渲染任务提示词
            return promptAssemblyService.assembleTaskPrompt(userInput, taskCode, getDefaultCharacterCard());
        } catch (Exception e) {
            log.error("获取任务特定提示词失败: taskMode={}", request.getTaskMode(), e);
            return "";
        }
    }

    /**
     * 获取系统默认角色卡ID
     */
    protected Persona getDefaultCharacterCard() {
        // 这里可以返回一个默认的角色卡，或者从配置中读取
        return characterRepository.findPersonaById(PersonaId.of("bartender"))
                .orElseThrow(() -> new IllegalStateException("未找到默认角色卡"));
    }

    // ========== 抽象方法，由子类实现 ==========

    /**
     * 验证请求类型是否正确
     * @param request 请求对象
     * @return 是否为有效请求
     */
    protected abstract boolean isValidRequest(BaseTaskRequest request);

    /**
     * 获取期望的请求类型名称（用于错误提示）
     * @return 请求类型名称
     */
    protected abstract String getExpectedRequestType();

    /**
     * 获取用户输入内容
     * @param request 请求对象
     * @return 用户输入内容
     */
    protected abstract String getUserInput(BaseTaskRequest request);

    /**
     * 获取策略名称
     * @return 策略名称
     */
    protected abstract String getStrategyName();
    }


