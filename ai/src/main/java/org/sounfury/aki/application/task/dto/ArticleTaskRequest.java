package org.sounfury.aki.application.task.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章任务请求
 * 用于文章总结、文章摘录等任务
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleTaskRequest extends BaseTaskRequest {
    
    /**
     * 文章ID
     */
    private Long articleId;
}
