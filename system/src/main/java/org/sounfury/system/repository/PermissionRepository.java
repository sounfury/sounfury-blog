package org.sounfury.system.repository;


import org.apache.commons.lang3.StringUtils;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;

import org.jooq.impl.DSL;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.jooq.tables.daos.PermissionDao;
import org.sounfury.jooq.tables.pojos.Permission;
import org.sounfury.system.dto.urp.PermissionQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.noCondition;
import static org.sounfury.jooq.tables.Permission.PERMISSION;

@Repository
public class PermissionRepository extends PermissionDao {

  @Autowired
  public PermissionRepository(Configuration configuration) {
    super(configuration);
  }

  public Result<Record> pageFetchBy(
      PageReqDto pageRequestDto, PermissionQueryDto permissionQueryDto) {
    return ctx()
        .select(asterisk(), DSL.count().over().as("total_permission"))
        .from(PERMISSION)
        .where(
            CollectionUtils.isEmpty(permissionQueryDto.getPermissionIdList())
                ? noCondition()
                : PERMISSION.ID.in(permissionQueryDto.getPermissionIdList()))
        .and(
            permissionQueryDto.getPermissionId() == null
                ? noCondition()
                : PERMISSION.ID.eq(permissionQueryDto.getPermissionId()))
        .and(
            StringUtils.isEmpty(permissionQueryDto.getPermissionName())
                ? noCondition()
                : PERMISSION.NAME.like("%" + permissionQueryDto.getPermissionName() + "%"))
        .and(
            StringUtils.isEmpty(permissionQueryDto.getPermissionName())
                ? noCondition()
                : PERMISSION.CODE.eq(permissionQueryDto.getPermissionCode()))
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  public List<Permission> selectByPermissionIdIn(List<Long> permissionIdList) {
    return ctx()
        .selectFrom(PERMISSION)
        .where(PERMISSION.ID.in(permissionIdList))
        .fetchInto(Permission.class);
  }
}
