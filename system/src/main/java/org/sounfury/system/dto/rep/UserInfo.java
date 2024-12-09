package org.sounfury.system.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sounfury.system.model.LoginUser;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 134564658234L;

    private LoginUser loginUser;
    private List<String> permissions;
    private List<String> roles;
}
