package com.czsc.dto;

import com.czsc.entity.StockSegTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class StockSegDto extends StockSegTable {
    private List<StockBiDto> stockBiDtos;
    private Boolean finished;
    private Boolean gap;
    private BigDecimal curExtreme;
    private Date curExtremePos;
    private BigDecimal prevExtreme;
    private BigDecimal force;

    public StockSegDto(List<StockBiDto> stockBiDtos) {
        setId(UUID.randomUUID().toString());
        setSymbol(stockBiDtos.get(0).getSymbol());
        setStartTime(stockBiDtos.get(0).getStartTime());
        setStartPrice(stockBiDtos.get(0).getStartPrice());
        setType(stockBiDtos.get(0).getType());
        finished = false;
        gap = false;
        this.stockBiDtos = stockBiDtos;
        curExtreme = stockBiDtos.get(3).getStartPrice();
        curExtremePos = stockBiDtos.get(3).getStartTime();
        prevExtreme = stockBiDtos.get(1).getStartPrice();
    }

    public StockSegDto() {
    }
}
