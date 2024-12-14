package org.sounfury.admin.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.rep.ArticleDetailRep;
import org.sounfury.admin.dto.rep.ArticlePageRep;
import org.sounfury.admin.dto.req.ArticleAddReq;
import org.sounfury.admin.dto.req.ArticleUpdateReq;
import org.sounfury.admin.repository.ArticleAdminRepository;
import org.sounfury.admin.repository.CategoryAdminRepository;
import org.sounfury.admin.repository.TagAdminRepository;
import org.sounfury.admin.service.ArticleService;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.tables.pojos.Article;
import org.sounfury.jooq.tables.pojos.Category;
import org.sounfury.portal.dto.rep.ArticleCategoryDto;
import org.sounfury.portal.dto.rep.TagPortalDto;
import org.sounfury.portal.repository.SiteInfoPortalRepository;
import org.sounfury.utils.CacheUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.sounfury.core.constant.CacheNames.SITE_INFO;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleAdminRepository articleRepository;
    private final TagAdminRepository tagRepository;
    private final CategoryAdminRepository categoryRepository;
    private final SiteInfoPortalRepository siteInfoPortalRepository;

// @Cacheable(value = "siteInfoCache", key = "'siteInfo'")


    @Override
    @Transactional
    public void addArticle(ArticleAddReq articleAddReq) {

        Article convert = MapstructUtils.convert(articleAddReq, Article.class);
        long articleId = articleRepository.insertArticle(convert);
        tagRepository.insertTags(articleId, articleAddReq.getTags());
        //拿到字数
        Long totalWords = (long) articleAddReq.getContent()
                .length();
        //拿到当前文章数量
        Long articleCount = (long) articleRepository.countEnabledArticle();

        siteInfoPortalRepository.statusArticleInfo(totalWords, articleCount);
        CacheUtils.clear(SITE_INFO);

    }

    @Transactional
    @Override
    public void updateArticle(ArticleUpdateReq articleUpdateReq) {

        Article article = checkArticleExist(articleUpdateReq.getId());
        Article convert = MapstructUtils.convert(articleUpdateReq, Article.class);
        articleRepository.updateArticle(convert);
        tagRepository.insertTags(articleUpdateReq.getId(), articleUpdateReq.getTags());
        //计算字数差
        Long newTotalWords = (long) articleUpdateReq.getContent()
                .length() - article.getContent()
                .length();
        siteInfoPortalRepository.statusUpdateArticleInfo(newTotalWords);
        CacheUtils.clear(SITE_INFO);
    }

    @Transactional
    @Override
    public void deleteArticle(Long id) {
        Article article = checkArticleExist(id);
        //计算字数，去负数
        long totalWords = article.getContent()
                .length();
        totalWords = -totalWords;

        articleRepository.deleteArticleById(id);
        //拿到当前文章数量
        int count = articleRepository.countEnabledArticle();
        siteInfoPortalRepository.statusArticleInfo(totalWords, (long) count);
    }

    @Override
    public PageRepDto<List<ArticlePageRep>> pageArticle(PageReqDto articlePageReq) {
        PageRepDto<List<ArticlePageRep>> listPageRepDto = articleRepository.pageArticle(articlePageReq);
        return getPageTagsAndCategory(listPageRepDto);
    }

    @Override
    public ArticleDetailRep getArticle(Long id) {
        Article article = articleRepository.fetchOneById(id);
        List<TagPortalDto> tagPortalDtoList = tagRepository.fetchByArticleId(id)
                .stream()
                .map(tag -> new TagPortalDto(tag.getId(), tag.getName()))//去重
                .distinct()
                .toList();
        ArticleDetailRep adminArticleRep = new ArticleDetailRep(article);
        adminArticleRep.setTags(tagPortalDtoList);
        Category category = categoryRepository.fetchOneById(article.getCategoryId());
        adminArticleRep.setCategory(new ArticleCategoryDto(category.getId(), category.getName()));
        return adminArticleRep;
    }


    @NotNull
    private PageRepDto<List<ArticlePageRep>> getPageTagsAndCategory(PageRepDto<List<ArticlePageRep>> listPageRepDto) {
        if (listPageRepDto.getData() != null) {
            listPageRepDto.getData()
                    .forEach(pageArticleRep -> {
                        Category category = categoryRepository.fetchOneById(pageArticleRep.getCategoryId());
                        List<TagPortalDto> tagPortalDtoList = tagRepository.fetchByArticleId(pageArticleRep.getId())
                                .stream()
                                .map(tag -> new TagPortalDto(tag.getId(), tag.getName()))
                                .toList();
                        pageArticleRep.setTags(tagPortalDtoList);
                        pageArticleRep.setCategory(new ArticleCategoryDto(category.getId(), category.getName()));
                    });
        }
        return listPageRepDto;
    }

    //检查文章是否存在
    private Article checkArticleExist(Long id) {
        Article article = articleRepository.fetchOneById(id);
        if (article == null) {
            throw new ClientException("文章不存在");
        }
        return article;
    }


}
