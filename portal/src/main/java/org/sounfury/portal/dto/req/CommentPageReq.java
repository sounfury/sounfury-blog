package org.sounfury.portal.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.types.UInteger;
import org.sounfury.jooq.page.PageReqDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentPageReq extends PageReqDto {
    @NotBlank(message = "传入文章id不为空")
    private UInteger articleId;
}
