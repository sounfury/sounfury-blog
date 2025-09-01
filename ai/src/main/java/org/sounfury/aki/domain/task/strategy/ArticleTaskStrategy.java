package org.sounfury.aki.domain.task.strategy;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.task.dto.ArticleTaskRequest;
import org.sounfury.aki.application.task.dto.BaseTaskRequest;
import org.sounfury.aki.domain.llm.service.CallLlmService;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import org.sounfury.aki.infrastructure.remote.BlogService;
import org.springframework.stereotype.Component;

/**
 * 文章任务策略
 * 处理文章总结、文章摘录等任务
 */
@Slf4j
@Component("articleTaskStrategy")
public class ArticleTaskStrategy extends AbstractTaskStrategy {

    private final BlogService blogService;

    public ArticleTaskStrategy(CallLlmService callLlmService,
                              PromptAssemblyService promptAssemblyService,
                              CharacterRepository characterRepository,
                              BlogService blogService) {
        super(callLlmService, promptAssemblyService,characterRepository);
        this.blogService = blogService;
    }

    @Override
    protected boolean isValidRequest(BaseTaskRequest request) {
        return request instanceof ArticleTaskRequest;
    }

    @Override
    protected String getExpectedRequestType() {
        return "ArticleTaskRequest";
    }

    @Override
    protected String getUserInput(BaseTaskRequest request) {
        ArticleTaskRequest articleRequest = (ArticleTaskRequest) request;
        try {
            return blogService.getArticle(articleRequest.getArticleId());
        } catch (Exception e) {
            log.error("获取文章内容失败，文章ID: {}", articleRequest.getArticleId(), e);
            return null;
        }
    }

    @Override
    protected String getStrategyName() {
        return "ArticleTaskStrategy";
    }
}
