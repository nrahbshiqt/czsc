package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.StockBiTable;
import com.czsc.entity.StockSegTable;

import java.util.List;

public interface StockSegTableService extends IService<StockSegTable> {
    List<StockSegDto> getStockSegDtosBySymbol(String symbol);
}
