package org.sounfury.aki.infrastructure.remote.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

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
}
