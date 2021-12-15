package com.czsc.dto;

import com.czsc.entity.StockPivotTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StockBiPivotDto extends StockPivotTable {
    private StockBiDto enterBiDto;
    private StockBiDto startBiDto;
    private StockBiDto endBiDto;
    private StockBiDto leaveBiDto;
    private List<StockBiDto> stockBiDtos;

    private String pivotType;
    private Integer trend;
    private Integer level;
    private BigDecimal aft_l_price;
    private Date aft_l_time;
    private BigDecimal future_zd;
    private BigDecimal future_zg;

    private BigDecimal gg;
    private BigDecimal zg;
    private BigDecimal zd;
    private BigDecimal dd;

    private Double finished;
    private BigDecimal enterForce;
    private BigDecimal leaveForce;
    private Integer size;
    private BigDecimal mean;
    private BigDecimal prev2Force;
    private BigDecimal prev2EndPrice;
    private BigDecimal prev1Force;

    public StockBiPivotDto(){}
}
