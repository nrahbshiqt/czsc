package com.czsc.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StockListTable implements Serializable {
    private String symbol;

    private String stockName;

    private Integer status;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}