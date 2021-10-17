package com.czsc.chanlun.kline.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zzw on 2021/4/10.
 */
@Data
public class BiDto extends TableKey {
    private Date startTime;
    private Integer startIndex;

    private Date endTime;
    private Integer endIndex;

    private BigDecimal startPrice;

    private BigDecimal endPrice;

    private Integer type;

    private List<KlineContainsDto> klineContainsDtos;
}
