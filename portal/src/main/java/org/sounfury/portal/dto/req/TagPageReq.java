package org.sounfury.portal.dto.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sounfury.jooq.page.PageReqDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagPageReq extends PageReqDto {
    private Long tagId;
}
