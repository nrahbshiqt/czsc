package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.entity.StockListTable;

import java.util.List;

public interface StockListTableService extends IService<StockListTable> {
    List<StockListTable> getAllData();

    void updateStatusBySymbols(List<String> symbols, Integer status);

    List<StockListTable> getStockListTabelsByStatus(Integer status);
}
