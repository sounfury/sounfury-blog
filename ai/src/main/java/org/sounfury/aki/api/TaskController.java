package org.sounfury.aki.api;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.task.service.TaskApplicationService;
import org.sounfury.aki.domain.task.enums.TaskMode;
import org.sounfury.aki.application.task.dto.ArticleTaskRequest;
import org.sounfury.aki.application.task.dto.TaskResponse;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import org.springframework.http.MediaType;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/action")
@SaIgnore
public class TaskController {

    private final TaskApplicationService taskApplicationService;

    /**
     * 总结指定文章
     * @param articleId 文章ID
     * @return 总结结果
     */
    @GetMapping("/summarize/article/{articleId}")
    public Result<TaskResponse> summarizeArticle(@PathVariable Long articleId) {
        try {
            log.info("总结文章请求，文章ID: {}", articleId);

            // 构建任务请求
            ArticleTaskRequest request = new ArticleTaskRequest();
            request.setArticleId(articleId);
            request.setTaskMode(TaskMode.ARTICLE_SUMMARY);
            // TODO: 从登录上下文获取用户信息，暂时写死
            request.setUserName("系统");
            request.setIsOwner(true);

            // 执行任务
            TaskResponse response = taskApplicationService.executeTask(request);

            return Results.success(response);
        } catch (Exception e) {
            log.error("总结文章异常，文章ID: {}", articleId, e);
            throw new ClientException("服务异常");
        }
    }

    /**
     * 总结指定文章（流式输出）
     * @param articleId 文章ID
     * @return 流式总结结果
     */
    @GetMapping(value = "/summarize/article/{articleId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> summarizeArticleStream(@PathVariable Long articleId) {
        try {
            log.info("流式总结文章请求，文章ID: {}", articleId);

            // 构建任务请求
            ArticleTaskRequest request = new ArticleTaskRequest();
            request.setArticleId(articleId);
            request.setTaskMode(TaskMode.ARTICLE_SUMMARY);
            // TODO: 从登录上下文获取用户信息，暂时写死
            request.setUserName("系统");
            request.setIsOwner(true);

            // 执行流式任务
            return taskApplicationService.executeTaskStream(request);
        } catch (Exception e) {
            log.error("流式总结文章异常，文章ID: {}", articleId, e);
            return Flux.error(new ClientException("服务异常"));
        }
    }
}
