package org.sounfury.portal.dto.rep;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectField;
import org.sounfury.jooq.tables.pojos.Article;

import java.time.LocalDateTime;
import java.util.List;

import static org.sounfury.jooq.tables.Article.ARTICLE;

@AllArgsConstructor
@Builder
@Data
public class PageArticleRep {

    private Long id;

    private String title;

    private String summary;

    private Long categoryId;

    private String thumbnail;

    private Byte isTop;

    private Long viewCount;

    private List<TagPortalDto> tags;

    private ArticleCategoryDto category;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

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
        this.updateTime = article.getUpdateTime();
    }

    public static final RecordMapper<Record, PageArticleRep> MAPPER = record -> PageArticleRep.builder()
            .id(record.get(ARTICLE.ID))
            .title(record.get(ARTICLE.TITLE))
            .summary(record.get(ARTICLE.SUMMARY))
            .categoryId(record.get(ARTICLE.CATEGORY_ID))
            .thumbnail(record.get(ARTICLE.THUMBNAIL))
            .isTop(record.get(ARTICLE.IS_TOP))
            .viewCount(record.get(ARTICLE.VIEW_COUNT))
            .createBy(record.get(ARTICLE.CREATE_BY))
            .createTime(record.get(ARTICLE.CREATE_TIME))
            .updateTime(record.get(ARTICLE.UPDATE_TIME))
            .build();


    public static final SelectField<?>[] ARTICLE_FIELDS = {
            ARTICLE.ID,
            ARTICLE.TITLE,
            ARTICLE.SUMMARY,
            ARTICLE.CATEGORY_ID,
            ARTICLE.THUMBNAIL,
            ARTICLE.IS_TOP,
            ARTICLE.VIEW_COUNT,
            ARTICLE.CREATE_BY,
            ARTICLE.CREATE_TIME,
            ARTICLE.UPDATE_TIME
    };
}
