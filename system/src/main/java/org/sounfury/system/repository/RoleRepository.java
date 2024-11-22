package org.sounfury.system.repository;


import org.apache.commons.lang3.StringUtils;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.tables.daos.RoleDao;
import org.sounfury.jooq.tables.pojos.Role;
import org.sounfury.system.dto.urp.RoleQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.noCondition;
import static org.sounfury.jooq.tables.Permission.PERMISSION;
import static org.sounfury.jooq.tables.Role.ROLE;
import static org.sounfury.jooq.tables.RolePermissionMap.ROLE_PERMISSION_MAP;
import static org.sounfury.jooq.tables.User.USER;
import static org.sounfury.jooq.tables.UserRoleMap.USER_ROLE_MAP;

@Repository
public class RoleRepository extends RoleDao {

  @Autowired
  public RoleRepository(Configuration configuration) {
    super(configuration);
  }

  /**
   * 查询某个用户的角色
   * @param username
   * @return
   */
  public List<Role> queryRolesByUserId (UInteger userId){

    return ctx().select(ROLE.asterisk())
            .from(ROLE)
            .leftJoin(USER_ROLE_MAP).on(ROLE.ID.eq(USER_ROLE_MAP.ROLE_ID))
            .where(USER_ROLE_MAP.USER_ID.eq(userId))
            .fetchInto(Role.class);
  }

  public void editUserRole(UInteger userId, List<Long> roleIds) {
    //先删除用户的所有角色
    ctx().deleteFrom(USER_ROLE_MAP)
            .where(USER_ROLE_MAP.USER_ID.eq(userId))
            .execute();
    //再插入新的角色
    if (!CollectionUtils.isEmpty(roleIds)) {
      roleIds.forEach(roleId -> {
        ctx().insertInto(USER_ROLE_MAP)
                .set(USER_ROLE_MAP.USER_ID, userId)
                .set(USER_ROLE_MAP.ROLE_ID, UInteger.valueOf(roleId))
                .execute();
      });
    }
  }
}
