package org.sounfury.portal.dto.rep;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.sounfury.jooq.tables.pojos.Article;

import java.time.LocalDateTime;

@Data
public class PageArticleRep {

    private UInteger id;

    private String title;

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

    public PageArticleRep(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
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
