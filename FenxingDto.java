package com.czsc.dto;

import com.czsc.entity.StockContainsTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FenxingDto {
    private List<StockContainsDto> stockContainsDtos;
    private Integer type;
    private StockContainsDto stockContainsDto;
    private Integer index;
    private Boolean real;
    private Boolean done;
    private BigDecimal val;
    private Integer power;
}
