package org.sounfury.system.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.sounfury.system.repository.urp.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface{
    private final UserRepository userRepository;
    /**
     * 返回一个账号所拥有的权限码集合 
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> list = new ArrayList<String>();
        userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy((Long) StpUtil.getLoginId())
                .getPermissions()
                .forEach(permissionDto -> list.add(permissionDto.getCode()));
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<String>();

        userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy(Long.valueOf((String) loginId))
                .getRoles()
                .forEach(roleDto -> list.add(roleDto.getCode()));
        return list;
    }

}