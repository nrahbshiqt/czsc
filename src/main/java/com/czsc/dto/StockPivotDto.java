package com.czsc.dto;

import com.czsc.common.FenxingType;
import com.czsc.entity.StockPivotTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class StockPivotDto extends StockPivotTable {
    private StockSegDto enterSegDto;
    private StockSegDto startSegDto;
    private StockSegDto endSegDto;
    private StockSegDto leaveSegDto;
    private List<StockSegDto> stockSegDtos;

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

    public StockPivotDto(){}
}
