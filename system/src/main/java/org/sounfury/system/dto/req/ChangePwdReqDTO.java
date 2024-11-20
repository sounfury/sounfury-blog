package org.sounfury.system.dto.req;

import lombok.Data;

@Data
public class ChangePwdReqDTO {
    String username;
    String oldPassword;
    String newPassword;
}
