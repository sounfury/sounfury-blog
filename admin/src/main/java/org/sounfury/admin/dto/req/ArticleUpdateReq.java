package org.sounfury.admin.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sounfury.jooq.tables.pojos.Article;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = Article.class)
public class ArticleUpdateReq {
    @NotNull(message = "文章ID不能为空")
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;
    private String content;
    private String summary;
    private Long categoryId;
    private String thumbnail;
    @Min(value = 0, message = "置顶状态只能为0或1")
    @Max(value = 1, message = "置顶状态只能为0或1")
    private Byte isTop;

    @Min(value = 0, message = "启用状态只能为0或1")
    @Max(value = 1, message = "启用状态只能为0或1")
    private Byte enableStatus;

    @NotNull(message = "是否允许评论不能为空")
    @Min(value = 0, message = "评论状态只能为0或1")
    @Max(value = 1, message = "评论状态只能为0或1")
    private Byte isComment;
    private LocalDateTime createTime;
    private String updateBy;

    private List<String> tags;
}
