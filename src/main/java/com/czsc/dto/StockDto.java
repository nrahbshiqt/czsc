package com.czsc.dto;

import com.czsc.entity.StockTable;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockDto extends StockTable {
    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal macd;
}