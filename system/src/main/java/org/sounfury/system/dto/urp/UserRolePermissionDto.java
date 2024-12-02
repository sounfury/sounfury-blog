package org.sounfury.system.dto.urp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRolePermissionDto {
    private Long id;

    private Boolean enableStatus;
    private List<RoleDto> roles = new LinkedList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime createTime;

    public Set<PermissionDto> getPermissions() {
        return roles.stream()
                .flatMap((roleDto) -> roleDto.getPermissions()
                        .stream())
                .collect(Collectors.toSet());
    }
}
