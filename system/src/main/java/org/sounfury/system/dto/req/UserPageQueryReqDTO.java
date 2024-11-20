package org.sounfury.system.dto.req;

import org.sounfury.jooq.page.PageReqDto;

public class UserPageQueryReqDTO extends PageReqDto {

    public UserPageQueryReqDTO(int page, int size) {
        super(page, size);
    }
}
