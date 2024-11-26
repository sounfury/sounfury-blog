package org.sounfury.admin.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@AutoMapper(target = ArticleAddReq.class)
public class ArticleAddReq {

    private String title;
    private String content;
    private String summary;
    private UInteger categoryId;
    private String thumbnail;
    private UByte isTop;
    private Byte enableStatus;
    private UByte isComment;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;

    private List<String> tags;
}
