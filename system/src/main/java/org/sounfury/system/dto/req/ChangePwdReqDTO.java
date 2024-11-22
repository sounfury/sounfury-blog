package org.sounfury.system.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePwdReqDTO {
    @NotNull
    String username;
    @NotNull
    String oldPassword;
    @NotNull
    String newPassword;
}
