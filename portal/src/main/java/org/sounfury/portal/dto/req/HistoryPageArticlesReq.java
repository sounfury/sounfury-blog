package org.sounfury.portal.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.portal.common.enums.Accuracy;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class HistoryPageArticlesReq extends PageReqDto {
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime historyTime;
    //精度
    private Accuracy accuracy;
}
