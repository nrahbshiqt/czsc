package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.entity.StockContainsTable;

import java.util.Date;
import java.util.List;

public interface StockContainsTableService extends IService<StockContainsTable> {

    List<StockContainsTable> getStockContainsBySymbol(String symbol);

    List<StockContainsTable> getStockContainsBySymbolAndstartDt(String symbol, Date startDt);

    int getCountBySymbolAndStartTimeAndEndTime(String symbol, Date startTime, Date endTime);
}
