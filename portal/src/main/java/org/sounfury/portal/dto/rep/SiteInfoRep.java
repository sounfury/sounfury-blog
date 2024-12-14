package org.sounfury.portal.dto.rep;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteInfoRep {
    private Long articleCount;
    private Long totalWords;
    private Long totalVisits;
    //精确到s
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastUpdateTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime siteCreationDate;
}
