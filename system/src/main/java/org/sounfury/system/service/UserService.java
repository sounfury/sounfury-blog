package org.sounfury.system.service;

import org.jooq.types.UInteger;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.dto.req.UserRoleEditReq;
import org.sounfury.system.dto.urp.UserRolePermissionDto;

import java.util.List;

public interface UserService {

    /**
     * 分页查询用户
     *
     * @param requestParam
     * @return
     */
    PageRepDto<List<UserPageQueryRepDTO>> pageQueryUser(UserPageQueryReqDTO requestParam);


    /**
     * 编辑用户角色
     *
     * @param userId  用户id
     * @param roleIds 变更后的id
     */
    void editUserRole(UserRoleEditReq requestParam);

    /**
     * 删除用户
     * @param uId
     */
    void deleteUser(Long uId);


    /**
     * 获取用户info
     */
    UserRolePermissionDto getUserInfo(Long userId);
}
