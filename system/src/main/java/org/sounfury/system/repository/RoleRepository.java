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

@Repository
public class RoleRepository extends RoleDao {

  @Autowired
  public RoleRepository(Configuration configuration) {
    super(configuration);
  }

  public List<Role> selectByRoleCodeIn(List<String> roleCodeList) {
    return ctx().selectFrom(ROLE).where(ROLE.CODE.in(roleCodeList)).fetchInto(Role.class);
  }

  public List<Role> selectByRoleIdIn(List<Long> roleIdList) {
    return ctx().selectFrom(ROLE).where(ROLE.ID.in(roleIdList)).fetchInto(Role.class);
  }

  public Result<Record> pageFetchBy(PageReqDto pageRequestDto, RoleQueryDto roleQueryDto) {
    return ctx()
        .select(asterisk(), DSL.count(ROLE.ID).over().as("total_role"))
        .from(ROLE)
        .where(
            CollectionUtils.isEmpty(roleQueryDto.getRoleIdList())
                ? noCondition()
                : ROLE.ID.in(roleQueryDto.getRoleIdList()))
        .and(
            roleQueryDto.getRoleId() == null ? noCondition() : ROLE.ID.eq(roleQueryDto.getRoleId()))
        .and(
            StringUtils.isEmpty(roleQueryDto.getRoleName())
                ? noCondition()
                : ROLE.NAME.like("%" + roleQueryDto.getRoleName() + "%"))
        .and(
            StringUtils.isEmpty(roleQueryDto.getRoleCode())
                ? noCondition()
                : ROLE.CODE.eq(roleQueryDto.getRoleCode()))
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  public Result<Record> fetchUniqueRoleWithPermission(UInteger roleId) {
    return ctx()
        .select(asterisk())
        .from(ROLE)
        .leftJoin(ROLE_PERMISSION_MAP)
        .on(ROLE.ID.eq(ROLE_PERMISSION_MAP.ROLE_ID))
        .leftJoin(PERMISSION)
        .on(ROLE_PERMISSION_MAP.PERMISSION_ID.eq(PERMISSION.ID))
        .where(ROLE.ID.eq(roleId))
        .orderBy(ROLE.ID)
        .fetch();
  }
}
