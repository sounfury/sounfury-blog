package org.sounfury.system.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.core.utils.StringUtils;
import org.sounfury.jooq.tables.pojos.User;
import org.sounfury.jooq.tables.pojos.UserRoleMap;
import org.sounfury.satoken.util.TokenPropertiesUtil;
import org.sounfury.system.common.enums.RoleEnum;
import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserLoginReqDTO;
import org.sounfury.system.dto.req.UserRegisterReqDTO;
import org.sounfury.system.model.LoginUser;
import org.sounfury.system.repository.urp.UserRepository;
import org.sounfury.system.repository.urp.UserRoleMapRepository;
import org.sounfury.system.service.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.sounfury.core.constant.CacheNames.LOGIN_USER;
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
        if (StringUtils.isEmpty(requestParam.getNickname())) {
            requestParam.setNickname(requestParam.getUsername());
        }
        Long id = userRepository.insertUser(
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
        LoginUser loginUser = BeanUtil.copyProperties(user, LoginUser.class);
        StpUtil.getSession()
                .set(LOGIN_USER + user.getUsername(), loginUser);
        return user.getId();
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
