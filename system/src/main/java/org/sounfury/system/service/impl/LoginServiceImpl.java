package org.sounfury.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.types.UInteger;
import org.redisson.api.RBloomFilter;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.jooq.tables.pojos.User;
import org.sounfury.jooq.tables.pojos.UserRoleMap;
import org.sounfury.system.common.enums.RoleEnum;
import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserLoginReqDTO;
import org.sounfury.system.dto.req.UserRegisterReqDTO;
import org.sounfury.system.repository.UserRepository;
import org.sounfury.system.repository.UserRoleMapRepository;
import org.sounfury.system.service.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.sounfury.core.enums.UserErrorCodeEnum.USER_EXIST;
import static org.sounfury.core.enums.UserErrorCodeEnum.USER_NULL;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final UserRoleMapRepository userRoleMapRepository;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;


    @Override
    @Transactional
    public void register(UserRegisterReqDTO requestParam) {
        if (checkUsername(requestParam.getUsername())) {
            throw new ClientException(USER_EXIST);
        }
        UInteger id = userRepository.insertUser(
                Objects.requireNonNull(MapstructUtils.convert(requestParam, User.class)));
        userRoleMapRepository.insert(new UserRoleMap().setUserId(id)
                .setRoleId(RoleEnum.fromCode("EDITOR")
                        .getId()));
    }

    @Override
    public Long login(UserLoginReqDTO requestParam) {
        User user = userRepository.getByUsernameAndPassword(requestParam.getUsername(), requestParam.getPassword());
        if (user == null) {
            throw new ClientException(USER_NULL);
        }
        return user.getId()
                .longValue();

    }

    @Override
    public boolean checkUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub
    }

    @Override
    public void changePassword(ChangePwdReqDTO requestParam) {
        try {
            userRepository.getByUsernameAndPassword(requestParam.getUsername(), requestParam.getOldPassword());
        } catch (Exception e) {
            throw new ClientException("原密码错误");
        }
        try {
            userRepository.updatePassword(requestParam);
        } catch (Exception e) {
            throw new ClientException("修改密码失败");
        }
    }

    @Override
    public void resetPassword(String username) {
        userRepository.resetPassword(username);
    }
}
