package org.sounfury.system.dto.urp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDto {
  private UInteger id;
  private String code;
  private String name;
  List<PermissionDto> permissions = new LinkedList<>();
}
