package org.sounfury.portal.service;

import org.sounfury.portal.dto.rep.SingleArticleRep;

public interface ArticleService {
    SingleArticleRep getArticleById(Long id);
}
