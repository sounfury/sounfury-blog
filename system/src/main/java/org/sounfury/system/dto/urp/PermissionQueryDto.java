package org.sounfury.system.dto.urp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionQueryDto {

  private Long roleId;
  private Long permissionId;
  private String permissionCode;
  private String permissionName;
  private List<Long> permissionIdList;
}
