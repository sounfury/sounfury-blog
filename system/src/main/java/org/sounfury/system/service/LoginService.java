package org.sounfury.system.service;

import org.sounfury.system.dto.req.ChangePwdReqDTO;
import org.sounfury.system.dto.req.UserLoginReqDTO;
import org.sounfury.system.dto.req.UserRegisterReqDTO;

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
    Long login(UserLoginReqDTO requestParam);

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

}
