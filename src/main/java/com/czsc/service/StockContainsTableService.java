package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.entity.StockContainsTable;

import java.util.List;

public interface StockContainsTableService extends IService<StockContainsTable> {

    List<StockContainsTable> getStockContainsBySymbol(String symbol);
}
