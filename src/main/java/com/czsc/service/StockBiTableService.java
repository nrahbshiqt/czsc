package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.dto.StockBiDto;
import com.czsc.entity.StockBiTable;

import java.util.List;

public interface StockBiTableService extends IService<StockBiTable> {
    List<StockBiDto> getStockBiDtoBySymbol(String symbol);
}
