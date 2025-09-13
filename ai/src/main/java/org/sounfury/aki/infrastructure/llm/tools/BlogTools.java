package org.sounfury.aki.infrastructure.llm.tools;

import lombok.RequiredArgsConstructor;
import org.sounfury.aki.infrastructure.remote.BlogService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 博客相关工具集
 * 包含所有与博客文章相关的AI工具
 */
@Component("blog_tools")
@RequiredArgsConstructor
public class BlogTools {

    private final BlogService blogService;

    /**
     * 根据文章ID获取文章内容进行总结
     */
    @Tool(name = "summary_article_by_id", description = "Get Blog article content by article ID for summarization. Call this when user wants to summarize an article by providing article ID.")
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

    /**
     * 根据文章标题模糊查询文章内容
     */
    @Tool(name = "search_articles_by_name", description = "Search Blog articles by title name using fuzzy matching. Call this when user wants to find articles by title keywords.")
    public String searchArticlesByName(String titleKeyword) {
        try {
            String searchResults = blogService.searchArticlesByName(titleKeyword);
            return "根据关键词'" + titleKeyword + "'的搜索结果：" + searchResults;
        } catch (Exception e) {
            return "错误：搜索文章时发生异常，关键词：" + titleKeyword;
        }
    }
}
