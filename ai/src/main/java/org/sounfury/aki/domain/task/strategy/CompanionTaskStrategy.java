package org.sounfury.aki.domain.task.strategy;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.task.dto.BaseTaskRequest;
import org.sounfury.aki.application.task.dto.CompanionTaskRequest;
import org.sounfury.aki.domain.llm.service.CallLlmService;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import org.springframework.stereotype.Component;

/**
 * 陪伴任务策略
 * 处理发布祝贺、登录欢迎等任务
 */
@Slf4j
@Component("companionTaskStrategy")
public class CompanionTaskStrategy extends AbstractTaskStrategy {

    private CompanionTaskStrategy(CallLlmService callLlmService,
                                 PromptAssemblyService promptAssemblyService, CharacterRepository characterRepository) {
        super(callLlmService, promptAssemblyService,characterRepository);
    }

    @Override
    protected boolean isValidRequest(BaseTaskRequest request) {
        return request instanceof CompanionTaskRequest;
    }

    @Override
    protected String getExpectedRequestType() {
        return "CompanionTaskRequest";
    }

    @Override
    protected String getUserInput(BaseTaskRequest request) {
        CompanionTaskRequest companionRequest = (CompanionTaskRequest) request;
        return companionRequest.getContextInfo();
    }

    @Override
    protected String getStrategyName() {
        return "CompanionTaskStrategy";
    }
}
