package org.sounfury.system.dto.urp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleQueryDto {

  private Long userId;
  private Long roleId;
  private String roleCode;
  private String roleName;
  private List<Long> roleIdList;
}
