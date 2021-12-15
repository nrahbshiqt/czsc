package com.czsc.dto;

import com.czsc.entity.StockBiTable;
import com.czsc.entity.StockContainsTable;
import com.czsc.entity.StockTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StockBiDto extends StockBiTable {
    private Integer startIndex;
    private Integer endIndex;
    private List<StockContainsTable> stockContainsTables;
    private List<StockDto> stockTables;
    private BigDecimal force;
}
