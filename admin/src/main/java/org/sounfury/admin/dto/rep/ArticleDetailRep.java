package org.sounfury.admin.dto.rep;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectField;
import org.sounfury.jooq.tables.pojos.Article;
import org.sounfury.portal.dto.rep.ArticleCategoryDto;
import org.sounfury.portal.dto.rep.TagPortalDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.sounfury.jooq.tables.Article.ARTICLE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDetailRep {
    public static final RecordMapper<Record, ArticleDetailRep> MAPPER = record -> ArticleDetailRep.builder()
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
            .enableStatus(record.get(ARTICLE.ENABLE_STATUS))
            .content(record.get(ARTICLE.CONTENT))
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
            ARTICLE.UPDATE_TIME,
            ARTICLE.ENABLE_STATUS,
            ARTICLE.CONTENT
    };
    private Long id;
    private String title;
    private String summary;
    private Long categoryId;
    private String thumbnail;
    private Byte isTop;
    private List<TagPortalDto> tags;
    private ArticleCategoryDto category;
    private String createBy;
    private Long viewCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String content;
    /**
     * 0草稿 1发布
     */
    private Byte enableStatus;


    public ArticleDetailRep(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.summary = article.getSummary();
        this.categoryId = article.getCategoryId();
        this.thumbnail = article.getThumbnail();
        this.isTop = article.getIsTop();
        this.createBy = article.getCreateBy();
        this.viewCount = article.getViewCount();
        this.createTime = article.getCreateTime();
        this.updateTime = article.getUpdateTime();
        this.enableStatus = article.getEnableStatus();
        this.content = article.getContent();
    }
}
