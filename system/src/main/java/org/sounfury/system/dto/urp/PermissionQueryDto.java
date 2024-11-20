package org.sounfury.system.dto.urp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionQueryDto {

  private UInteger roleId;
  private UInteger permissionId;
  private String permissionCode;
  private String permissionName;
  private List<Long> permissionIdList;
}
