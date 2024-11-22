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
  public List<Role> queryRolesByUsername (String username){

    return ctx().select(ROLE.asterisk())
            .from(ROLE)
            .leftJoin(USER_ROLE_MAP).on(ROLE.ID.eq(USER_ROLE_MAP.ROLE_ID))
            .leftJoin(USER).on(USER_ROLE_MAP.USER_ID.eq(USER.ID))
            .where(USER.USERNAME.eq(username))
            .fetchInto(Role.class);
  }
}
