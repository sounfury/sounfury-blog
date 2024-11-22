package org.sounfury.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.redisson.api.RBloomFilter;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.tables.pojos.Role;
import org.sounfury.jooq.tables.pojos.UserRoleMap;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.dto.req.UserRoleEditReq;
import org.sounfury.system.repository.RoleRepository;
import org.sounfury.system.repository.UserRepository;
import org.sounfury.system.repository.UserRoleMapRepository;
import org.sounfury.system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleMapRepository userRoleMapRepository;
    private final RoleRepository roleRepository;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;


    @Override
    public PageRepDto<List<UserPageQueryRepDTO>> pageQueryUser(UserPageQueryReqDTO requestParam) {
       return  userRepository.pageQueryUser(requestParam);
    }

    @Override
    @Transactional
    public void editUserRole(UserRoleEditReq requestParam) {
        UInteger userId = requestParam.getUserId();
        List<Long> roleIds = requestParam.getRoleIds();
        if(CollectionUtils.isEmpty(roleIds)){

           throw new ClientException("用户不能没有角色");
        }
        roleRepository.editUserRole(userId, roleIds);
    }

    @Override
    @Transactional
    public void deleteUser(UInteger uId) {
        userRepository.deleteById(uId);
        userRoleMapRepository.deleteByUserId(uId);
    }
}
