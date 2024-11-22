package org.sounfury.portal.dto.rep;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.sounfury.jooq.tables.pojos.Article;
import org.sounfury.jooq.tables.pojos.Tag;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class SingleArticleRep {
    private UInteger id;

    private String title;

    private String content;

    private String summary;

    private UInteger categoryId;

    private String thumbnail;

    private UByte isTop;

    private ULong viewCount;

    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    List<TagPortalDto> tags;

    public SingleArticleRep(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.summary = article.getSummary();
        this.categoryId = article.getCategoryId();
        this.thumbnail = article.getThumbnail();
        this.isTop = article.getIsTop();
        this.viewCount = article.getViewCount();
        this.createBy = article.getCreateBy();
        this.createTime = article.getCreateTime();
        this.updateBy = article.getUpdateBy();
        this.updateTime = article.getUpdateTime();
    }
}