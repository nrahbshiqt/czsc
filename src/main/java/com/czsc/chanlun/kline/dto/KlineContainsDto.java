package com.czsc.chanlun.kline.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zzw on 2021/4/2.
 */
@Data
public class KlineContainsDto extends TableKey{

    private Date dt;

    private BigDecimal high;

    private BigDecimal low;

    private Integer fenXing;
}
