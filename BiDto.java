package com.czsc.util;

import com.czsc.dto.FenxingDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BiDto {
    private FenxingDto start;
    private FenxingDto end;
    private BigDecimal high;
    private BigDecimal low;
    private Integer type;
    private Boolean done;
    private Integer index;
    private Integer fxNum;
}
