package org.sounfury.system.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.sounfury.jooq.tables.pojos.User;

@Data
@AutoMapper(target = User.class)
public class UserLoginReqDTO {

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 记住我
     */
    @NotNull
    private Boolean rememberMe;

}
