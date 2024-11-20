package org.sounfury.system.dto.urp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleQueryDto {

  private UInteger userId;
  private UInteger roleId;
  private String roleCode;
  private String roleName;
  private List<UInteger> roleIdList;
}
