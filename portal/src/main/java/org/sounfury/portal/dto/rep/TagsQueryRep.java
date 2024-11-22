package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagsQueryRep {
    private Long id;
    private String name;

    /**
     * 该标签下文章的数量
     */
    private Integer articleCount;

}
