package org.sounfury.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.admin.dto.req.ArticleAddReq;
import org.sounfury.admin.dto.req.ArticleUpdateReq;
import org.sounfury.admin.repository.ArticleAdminRepository;
import org.sounfury.admin.repository.CategoryAdminRepository;
import org.sounfury.admin.repository.TagAdminRepository;
import org.sounfury.admin.service.ArticleService;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.jooq.tables.pojos.Article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.sounfury.admin.common.constant.Constants.DEFAULT_CATEGORY_ID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleAdminRepository articleRepository;
    private final TagAdminRepository tagRepository;
    private final CategoryAdminRepository categoryRepository;


    @Override
    @Transactional
    public void addArticle(ArticleAddReq articleAddReq) {

        Article convert = MapstructUtils.convert(articleAddReq, Article.class);
        long articleId = articleRepository.insertArticle(convert);
        tagRepository.insertTags(articleId, articleAddReq.getTags());
    }

    @Transactional
    @Override
    public void updateArticle(ArticleUpdateReq articleUpdateReq) {
        Article convert = MapstructUtils.convert(articleUpdateReq, Article.class);
        Long articleId = articleRepository.updateArticle(convert);
        tagRepository.insertTags(articleId, articleUpdateReq.getTags());
    }

    @Transactional
    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteArticleById(id);
    }


}
