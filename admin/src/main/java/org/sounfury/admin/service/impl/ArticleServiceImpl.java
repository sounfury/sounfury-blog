package org.sounfury.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.admin.dto.req.ArticleAddReq;
import org.sounfury.admin.repository.ArticleAdminRepository;
import org.sounfury.admin.repository.TagAdminRepository;
import org.sounfury.admin.service.ArticleService;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.jooq.tables.pojos.Article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private ArticleAdminRepository articleRepository;
    private TagAdminRepository tagRepository;


    @Override
    @Transactional
    public void addArticle(ArticleAddReq articleAddReq) {
        Article convert = MapstructUtils.convert(articleAddReq, Article.class);
        UInteger articleId = articleRepository.insertArticle(convert);
        tagRepository.insertTags(articleId, articleAddReq.getTags());
    }
}
