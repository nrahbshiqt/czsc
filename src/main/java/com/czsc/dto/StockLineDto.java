package com.czsc.dto;

import lombok.Data;

@Data
public class StockLineDto {
    private String date;
    private String open;
    private String close;
    private String high;
    private String low;
    private String volume;
    private String money;

}
