package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.entity.StockTable;

import java.util.List;

public interface StockTableService extends IService<StockTable> {
    /**
     * 根据股票代码和周期获取数据
     * @param symbal
     * @param period
     * @return
     */
    List<StockTable> getStockTablesBySymbolAndPeriod(String symbal, String period);

    /**
     * 保存数据
     * @param result
     */
    void saveStockTables(List<StockTable> result);
}
