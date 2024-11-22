package org.sounfury.portal.service.impl;

import lombok.AllArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.pojos.Article;
import org.sounfury.jooq.tables.pojos.Tag;
import org.sounfury.portal.dto.rep.SingleArticleRep;
import org.sounfury.portal.dto.rep.TagPortalDto;
import org.sounfury.portal.repository.ArticleRepository;
import org.sounfury.portal.repository.TagRepository;
import org.sounfury.portal.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;

    @Override
    public SingleArticleRep getArticleById(Long id) {
        Article articles = articleRepository.fetchOneById(UInteger.valueOf(id));
        List<TagPortalDto> tagPortalDtoList = tagRepository.fetchByArticleId(articleId)
                .stream()
                .map(tag -> new TagPortalDto(tag.getId(), tag.getName()))
                .toList();
        SingleArticleRep singleArticleRep = new SingleArticleRep(articles);
        singleArticleRep.setTags(tagPortalDtoList);
        return singleArticleRep;
    }

    private TagPortalDto convertToTagPortalDto(Tag tag) {
        return new TagPortalDto(tag.getId(), tag.getName());
    }
}
