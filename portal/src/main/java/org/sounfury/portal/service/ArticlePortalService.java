package org.sounfury.portal.service;

import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.portal.dto.rep.HistoryCount;
import org.sounfury.portal.dto.rep.PageArticleRep;
import org.sounfury.portal.dto.rep.SingleArticleRep;
import org.sounfury.portal.dto.req.CategoryPageReq;
import org.sounfury.portal.dto.req.HistoryPageArticlesReq;
import org.sounfury.portal.dto.req.TagPageReq;

import java.util.List;

public interface ArticlePortalService {
    /**
     * 查询单个文章的详细内容
     * @param id
     * @return SingleArticleRep
     */
    SingleArticleRep getArticleById(Long id);

    /**
     * 分页查询文章
     *
     * @param pageReqDto
     * @return
     */
    PageRepDto<List<PageArticleRep>> pageQueryArticle(PageReqDto pageReqDto);

    /**
     * 分页查询历史文章
     * @param historyPageArticlesReq
     * @return
     */
    PageRepDto<List<PageArticleRep>> historyArticle(HistoryPageArticlesReq historyPageArticlesReq);

    /**
     * 分页查询某个分类id下的文章
     *
     * @param pageReqDto
     * @return
     */
    PageRepDto<List<PageArticleRep>> pageQueryArticleByCategoryId(CategoryPageReq pageReqDto);

    /**
     * 分页查询某个标签id下的文章
     * @param pageReqDto
     * @return
     */
    PageRepDto<List<PageArticleRep>> pageQueryArticleByTagId(TagPageReq pageReqDto);

    List<PageArticleRep> pageQueryArticleTest(PageReqDto pageReqDto);

    List<HistoryCount> historyArticleCount();

    /**
     * 根据标题模糊查询文章
     * @param titleKeyword 标题关键词
     * @return 文章列表
     */
    List<PageArticleRep> searchArticlesByTitle(String titleKeyword);
}
