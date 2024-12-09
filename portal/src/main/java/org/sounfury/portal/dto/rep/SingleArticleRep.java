package org.sounfury.portal.dto.rep;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.sounfury.jooq.tables.pojos.Article;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class SingleArticleRep {
    private Long id;

    private String title;

    private String content;

    private String summary;

    private String thumbnail;

    private ArticleCategoryDto category;

    private Byte isTop;

    private Long viewCount;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    List<TagPortalDto> tags;

    public SingleArticleRep(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.summary = article.getSummary();
        this.thumbnail = article.getThumbnail();
        this.isTop = article.getIsTop();
        this.viewCount = article.getViewCount();
        this.createBy = article.getCreateBy();
        this.createTime = article.getCreateTime();
        this.updateTime = article.getUpdateTime();
    }
}
