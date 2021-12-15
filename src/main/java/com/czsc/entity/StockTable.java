package com.czsc.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockTable implements Serializable {
    private String symbol;

    private String period;

    private Date dt;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal volume;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}