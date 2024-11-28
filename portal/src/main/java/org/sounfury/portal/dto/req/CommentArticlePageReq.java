package org.sounfury.portal.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sounfury.jooq.page.PageReqDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentArticlePageReq extends PageReqDto {
    @NotBlank(message = "传入文章id不为空")
    private Long articleId;
}
