package org.sounfury.aki.infrastructure.remote;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogService {

    private final WebClient webClient;
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
}
