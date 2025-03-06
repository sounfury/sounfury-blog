package org.sounfury.system.service;

import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserLoginReqDTO;
import org.sounfury.system.dto.req.UserRegisterReqDTO;
import org.sounfury.system.dto.urp.UserRolePermissionDto;
import org.sounfury.system.model.LoginUser;

public interface LoginService {
    /**
     * 注册
     * @param requestParam
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 登录
     * @param requestParam
     */
    LoginUser login(UserLoginReqDTO requestParam);

    /**
     * 检查username是否存在
     * @param username
     */
    boolean checkUsername(String username);

    /**
     * 登出
     */
    void logout();

    /**
     * 修改密码
     * @param requestParam
     */
    void changePassword(ChangePwdReqDTO requestParam);

    /**
     * 重置密码
     * @param username
     */
    void resetPassword(String username);


    void validateCaptcha(String username,String code, String uuid);


}
