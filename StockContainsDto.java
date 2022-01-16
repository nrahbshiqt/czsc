package com.czsc.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockContainsDto implements Serializable {
    private Date dt;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal open;

    private BigDecimal close;

    private BigDecimal volume;

    private String symbol;

    private Date updateTime;

    private Integer index;
}