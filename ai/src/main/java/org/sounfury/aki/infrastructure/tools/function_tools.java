package org.sounfury.aki.infrastructure.tools;

import lombok.RequiredArgsConstructor;
import org.sounfury.aki.infrastructure.remote.BlogService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Aki酱的工具箱
 * 使用Spring AI @Tool注解定义工具方法
 */
@Component
@RequiredArgsConstructor
public class function_tools {

    private final BlogService blogService;


    /**
     * 获取当前时间
     */
    @Tool(description = "Get the current time only. Call this when user asks about current time or 'what time is it'.")
    public String getCurrentTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * 根据文章ID获取文章内容进行总结
     */
    @Tool(description = "Get article content by article ID for summarization. Call this when user wants to summarize an article by providing article ID.")
    public String getArticleForSummary(String articleId) {
        try {
            String postContent = blogService.getArticle(Long.parseLong(articleId));
            return "已成功获取文章内容，准备进行总结。文章内容如下：" + postContent;
        } catch (NumberFormatException e) {
            return "错误：文章ID格式不正确，请提供有效的数字ID";
        } catch (Exception e) {
            return "错误：无法获取文章内容，请检查文章ID是否存在";
        }
    }

}