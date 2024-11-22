package org.sounfury.system.repository;


import io.github.linpeilie.Converter;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.page.utils.JooqPageHelper;
import org.sounfury.jooq.tables.daos.UserDao;
import org.sounfury.jooq.tables.pojos.User;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.dto.urp.PermissionDto;
import org.sounfury.system.dto.urp.RoleDto;
import org.sounfury.system.dto.urp.UserRolePermissionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.jooq.tables.Permission.PERMISSION;
import static org.sounfury.jooq.tables.Role.ROLE;
import static org.sounfury.jooq.tables.RolePermissionMap.ROLE_PERMISSION_MAP;
import static org.sounfury.jooq.tables.User.USER;
import static org.sounfury.jooq.tables.UserRoleMap.USER_ROLE_MAP;

@Repository
public class UserRepository extends UserDao {


    private final Converter converter;
    @Value("${userManger.resetPassword}")
    private String resetPassword;

    @Autowired
    public UserRepository(Configuration configuration, Converter converter) {
        super(configuration);
        this.converter = converter;
    }

    public UInteger insertUser(User user) {
        return ctx().insertInto(USER)
                .set(USER.USERNAME, user.getUsername())
                .set(USER.PASSWORD, user.getPassword())
                .set(USER.NICKNAME, user.getNickname())
                .set(USER.MAIL, user.getMail())
                .returningResult(USER.ID)
                .fetchOne()
                .getValue(USER.ID);
    }

    public PageRepDto<List<UserPageQueryRepDTO>> pageQueryUser(UserPageQueryReqDTO requestParam) {
        DSLContext dsl = configuration().dsl();

        RecordMapper<Record, UserPageQueryRepDTO> mapper = record -> {
            UserPageQueryRepDTO dto = UserPageQueryRepDTO.builder()
                    .username(record.get(USER.USERNAME))
                    .nickname(record.get(USER.NICKNAME))
                    .mail(record.get(USER.MAIL))
                    .createTime(record.get(USER.CREATE_TIME))
                    .enableStatus(record.get(USER.ENABLE_STATUS))
                    .build();

            // 获取聚合后的角色字段 (roleNames)
            String roleNames = record.get("roleCodes", String.class);
            if (roleNames != null) {
                // 将逗号分隔的角色字符串转换为 List<String>
                List<String> roles = List.of(roleNames.split(","));
                dto.setRoles(roles);
            } else {
                // 如果没有角色，设置为空列表
                dto.setRoles(new ArrayList<>());
            }
            return dto;
        };

        SelectConditionStep<Record> roleCodes = ctx().select(
                        USER.asterisk(),
                        DSL.field("GROUP_CONCAT({0})", String.class, ROLE.CODE)
                                .as("roleCodes")
                )
                .from(USER)
                .leftJoin(USER_ROLE_MAP)
                .on(USER.ID.eq(USER_ROLE_MAP.USER_ID))
                .leftJoin(ROLE)
                .on(USER_ROLE_MAP.ROLE_ID.eq(ROLE.ID))
                .where(USER.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(USER.ENABLE_STATUS.eq(STATUS_ENABLE));


        return JooqPageHelper.getPage(
                ctx().select(
                                USER.asterisk(),
                                DSL.field("GROUP_CONCAT({0})", String.class, ROLE.CODE).as("roleCodes")
                        )
                        .from(USER)
                        .leftJoin(USER_ROLE_MAP)
                        .on(USER.ID.eq(USER_ROLE_MAP.USER_ID))
                        .leftJoin(ROLE)
                        .on(USER_ROLE_MAP.ROLE_ID.eq(ROLE.ID))
                        .where(USER.DEL_FLAG.eq(NOT_DEL_FLAG))
                        .and(USER.ENABLE_STATUS.eq(STATUS_ENABLE))
                        .groupBy(USER.ID),
                requestParam,
                dsl,
                mapper
        );
    }



    public UserRolePermissionDto fetchUniqueUserDtoWithNestedRolePermissionBy(UInteger userId) {
        return ctx()
                .select(
                        USER.asterisk(),
                        multiset(
                                select(
                                        ROLE.asterisk(),
                                        multiset(
                                                select(PERMISSION.asterisk())
                                                        .from(ROLE_PERMISSION_MAP)
                                                        .leftJoin(PERMISSION)
                                                        .on(ROLE_PERMISSION_MAP.PERMISSION_ID.eq(PERMISSION.ID))
                                                        .where(ROLE_PERMISSION_MAP.ROLE_ID.eq(ROLE.ID))
                                        )
                                                .convertFrom(
                                                        r -> r.map((record) -> record.into(PermissionDto.class)))
                                                .as("permissions"))
                                        .from(USER_ROLE_MAP)
                                        .leftJoin(ROLE)
                                        .on(USER_ROLE_MAP.ROLE_ID.eq(ROLE.ID))
                                        .where(USER.ID.eq(USER_ROLE_MAP.USER_ID))
                        )
                                .convertFrom(r -> r.map((record) -> record.into(RoleDto.class)))
                                .as("roles"))
                .from(USER)
                .where(USER.ID.eq(userId))
                .fetchOneInto(UserRolePermissionDto.class);
    }

    @Transactional
    public void deleteByUsername(String username) {
        ctx().delete(USER)
                .where(USER.USERNAME.eq(username))
                .execute();
    }

    public User getByUsernameAndPassword(String username, String password) {
        return ctx()
                .selectFrom(USER)
                .where(USER.USERNAME.eq(username))
                .and(USER.PASSWORD.eq(password))
                .and(USER.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(USER.ENABLE_STATUS.eq(STATUS_ENABLE))
                .fetchOneInto(User.class);
    }

    public void updatePassword(ChangePwdReqDTO requestParam) {
        ctx()
                .update(USER)
                .set(USER.PASSWORD, requestParam.getNewPassword())
                .where(USER.USERNAME.eq(requestParam.getUsername()))
                .execute();
    }

    public void resetPassword(String username) {
        ctx()
                .update(USER)
                .set(USER.PASSWORD, resetPassword)
                .where(USER.USERNAME.eq(username))
                .execute();
    }
}
