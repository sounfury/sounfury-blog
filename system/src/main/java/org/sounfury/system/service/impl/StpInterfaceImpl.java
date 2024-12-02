package org.sounfury.system.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.sounfury.system.dto.urp.PermissionDto;
import org.sounfury.system.dto.urp.RoleDto;
import org.sounfury.system.repository.urp.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    private final UserRepository userRepository;


    @Value("${sa-token.timeout}")
    private Long timeout;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 声明权限码集合
        List<String> permissionList = new ArrayList<>();

        // 遍历角色 ID，查询每个角色的权限列表
        getRoleList(loginId, loginType).forEach(roleCode -> {
            // 构建缓存键
            String cacheKey = "token:roleCode-find-permission:" + roleCode;

            // 从缓存中获取权限列表
            @SuppressWarnings("unchecked")
            List<String> cachedPermissions = (List<String>) SaManager.getSaTokenDao().getObject(cacheKey);


            if(cachedPermissions == null) {
                cachedPermissions = new ArrayList<>();
                List<String> finalCachedPermissions = cachedPermissions;
                userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy(StpUtil.getLoginIdAsLong())
                       .getRoles()
                       .forEach(roleDto -> {
                           if (roleDto.getCode().equals(roleCode)) {
                               roleDto.getPermissions()
                                       .forEach(permissionDto -> finalCachedPermissions.add(permissionDto.getCode()));
                           }
                       });
                SaTokenDao saTokenDao = SaManager.getSaTokenDao();
                saTokenDao.setObject(cacheKey, cachedPermissions, timeout);
            }

            // 将权限列表加入结果集
            permissionList.addAll(cachedPermissions);
        });

        // 返回权限码集合
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String ROLE_CACHE_KEY = "token:loginId-find-role:" + loginId;
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) SaManager.getSaTokenDao().getObject(ROLE_CACHE_KEY);
        if (roles == null) {
            roles = new ArrayList<>();
            List<String> finalRoles = roles;
            userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy(StpUtil.getLoginIdAsLong())
                    .getRoles()
                    .forEach(roleDto -> finalRoles.add(roleDto.getCode()));
            SaTokenDao saTokenDao = SaManager.getSaTokenDao();
            saTokenDao.setObject(ROLE_CACHE_KEY, roles, timeout);
        }
        return roles;
    }

}