package org.sounfury.portal.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.tables.pojos.Article;
import org.sounfury.jooq.tables.pojos.Category;
import org.sounfury.portal.dto.rep.ArticleCategoryDto;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.rep.SingleArticleRep;
import org.sounfury.portal.dto.rep.TagPortalDto;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.HistoryPageArticlesReq;
import org.sounfury.portal.dto.req.TagPageReq;
import org.sounfury.portal.repository.ArticlePortalRepository;
import org.sounfury.portal.repository.CategoryPortalRepository;
import org.sounfury.portal.repository.TagPortalRepository;
import org.sounfury.portal.service.ArticlePortalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticlePortalServiceImpl implements ArticlePortalService {
    private final ArticlePortalRepository articleRepository;
    private final TagPortalRepository tagRepository;
    private final CategoryPortalRepository categoryRepository;

    @Override
    public SingleArticleRep getArticleById(Long id) {
        UInteger articleId = UInteger.valueOf(id);
        Article article = articleRepository.fetchOneById(articleId);
        List<TagPortalDto> tagPortalDtoList = tagRepository.fetchByArticleId(articleId)
                .stream()
                .map(tag -> new TagPortalDto(tag.getId(), tag.getName()))
                .toList();
        SingleArticleRep singleArticleRep = new SingleArticleRep(article);
        singleArticleRep.setTags(tagPortalDtoList);
        Category category = categoryRepository.fetchOneById(article.getCategoryId());
        singleArticleRep.setCategory(new ArticleCategoryDto(category.getId(), category.getName()));
        return singleArticleRep;
    }

    @Override
    public PageRepDto<List<PageArticleRep>> pageQueryArticle(PageReqDto pageReqDto) {
        PageRepDto<List<PageArticleRep>> listPageRepDto = articleRepository.pageQueryArticle(pageReqDto);
        return getPageTagsAndCategory(listPageRepDto);
    }

    @Override
    public PageRepDto<List<PageArticleRep>> historyArticle(HistoryPageArticlesReq historyPageArticlesReq) {

        PageRepDto<List<PageArticleRep>> listPageRepDto = articleRepository.pageQueryHistoryArticle(
                historyPageArticlesReq);

        return getPageTagsAndCategory(listPageRepDto);
    }

    @Override
    public PageRepDto<List<PageArticleRep>> pageQueryArticleByCategoryId(CategoryPageReq pageReqDto) {
        PageRepDto<List<PageArticleRep>> listPageRepDto = articleRepository.pageQueryArticleByCategoryId(pageReqDto);
        return getPageTagsAndCategory(listPageRepDto);
    }

    @Override
    public PageRepDto<List<PageArticleRep>> pageQueryArticleByTagId(TagPageReq pageReqDto) {
        PageRepDto<List<PageArticleRep>> listPageRepDto = articleRepository.pageQueryArticleByTagId(pageReqDto);
        return getPageTagsAndCategory(listPageRepDto);
    }

    @NotNull
    private PageRepDto<List<PageArticleRep>> getPageTagsAndCategory(PageRepDto<List<PageArticleRep>> listPageRepDto) {
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
        return listPageRepDto;
    }


}
