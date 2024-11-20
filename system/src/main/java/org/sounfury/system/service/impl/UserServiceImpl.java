package org.sounfury.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.system.dto.rep.UserPageQueryRepDTO;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.repository.UserRepository;
import org.sounfury.system.repository.UserRoleMapRepository;
import org.sounfury.system.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleMapRepository userRoleMapRepository;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;


    @Override
    public PageRepDto<List<UserPageQueryRepDTO>> pageQueryUser(UserPageQueryReqDTO requestParam) {
       return  userRepository.pageQueryUser(requestParam);
    }
}
