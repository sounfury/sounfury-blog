package org.sounfury.aki.infrastructure.remote;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Slf4j
@Service
public class BlogService {

    private final WebClient webClient;

    public BlogService(WebClient.Builder builder) {
        // 注意：不要在这里读 token！
        ExchangeFilterFunction tokenFilter = (request, next) -> {
            String token = UserContextHolder.UserContext.getToken();
            ClientRequest newReq = ClientRequest.from(request)
                                                .headers(h -> {
                                                    if (org.springframework.util.StringUtils.hasText(token)) {
                                                        h.set("token", token);
                                                    }
                                                })
                                                .cookies(c -> {
                                                    if (org.springframework.util.StringUtils.hasText(token)) {
                                                        c.set("token", token);
                                                    }
                                                })
                                                .build();
            return next.exchange(newReq);
        };

        this.webClient = builder
                .baseUrl("http://localhost:8080") 
                .filter(tokenFilter)
                .build();
    }

    /**
     * 获取文章内容
     */
    public String getArticle(Long articleId) {
        try {
            // 调用blog-portal模块的API获取文章内容
            return webClient
                    .get()
                    .uri("/portal/article/{id}", articleId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .mapNotNull(response -> {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) response.get("data");
                        if (data != null) {
                            return (String) data.get("content");
                        }
                        return null;
                    })
                    .block();
        } catch (Exception e) {
            log.error("获取文章内容失败，文章ID: {}", articleId, e);
            return null;
        }
    }

    /**
     * 根据标题关键词搜索文章
     */
    public String searchArticlesByName(String titleKeyword) {
        try {
            // 调用blog-portal模块的API搜索文章
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/portal/article/search")
                            .queryParam("titleKeyword", titleKeyword)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .mapNotNull(response -> {
                        @SuppressWarnings("unchecked")
                        java.util.List<Map<String, Object>> data = (java.util.List<Map<String, Object>>) response.get("data");
                        if (data != null && !data.isEmpty()) {
                            StringBuilder result = new StringBuilder();
                            for (Map<String, Object> article : data) {
                                result.append("标题: ").append(article.get("title"))
                                      .append(", 摘要: ").append(article.get("summary"))
                                      .append(", ID: ").append(article.get("id"))
                                      .append("\n");
                            }
                            return result.toString();
                        }
                        return "未找到相关文章";
                    })
                    .block();
        } catch (Exception e) {
            log.error("搜索文章失败，关键词: {}", titleKeyword, e);
            return "搜索失败: " + e.getMessage();
        }
    }
}
