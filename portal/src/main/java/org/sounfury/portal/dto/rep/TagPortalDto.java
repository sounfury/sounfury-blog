package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.types.UInteger;

@AllArgsConstructor
@Data
public class TagPortalDto {
    private UInteger id;
    private String name;
}
