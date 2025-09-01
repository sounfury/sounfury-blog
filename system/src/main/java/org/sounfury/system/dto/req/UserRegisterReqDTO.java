package org.sounfury.system.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.sounfury.blog.jooq.tables.pojos.User;

@Data
@AutoMapper(target = User.class)
public class UserRegisterReqDTO {

    /**
     * 用户名
     */
    @NotNull
    private String username;

    /**
     * 密码
     */
    @NotNull
    private String password;

    /**
     * 昵称
     */
    private String nickname;


    /**
     * 邮箱
     */
    //校验邮箱格式
    @Email
    private String mail;

}
