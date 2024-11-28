package org.sounfury.system.dto.urp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionDto {
  private Long id;
  private String code;
  private String name;
}
