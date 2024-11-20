package org.sounfury.system.service;

import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;

import java.util.List;

public interface UserService {

    /**
     * 分页查询用户
     *
     * @param requestParam
     * @return
     */
    PageRepDto<List<UserPageQueryRepDTO>> pageQueryUser(UserPageQueryReqDTO requestParam);

}
