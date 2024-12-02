package org.sounfury.system.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements Serializable {

    //序列化id
    private static final long serialVersionUID = 134564658234L;

    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Byte enableStatus;
    private String nickname;
    private String mail;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte delFlag;
    private String avatar;


}
