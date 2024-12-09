package org.sounfury.portal.dto.rep;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoCountRep implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer articleCount;
    private Integer tagsCount;
    private Integer categoryCount;
}
