package org.sounfury.admin.service;

import org.sounfury.admin.dto.req.ArticleAddReq;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.rep.SingleArticleRep;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.HistoryPageArticlesReq;
import org.sounfury.portal.dto.req.TagPageReq;

import java.util.List;

public interface ArticleService {

    /**
     * 添加文章
     */
    void addArticle(ArticleAddReq articleAddReq);

}
