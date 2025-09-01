package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCount implements java.io.Serializable {
    private static final long serialVersionUID = 1123124L;
    private String date; // 日期
    private Integer count; // 文章数量
}
