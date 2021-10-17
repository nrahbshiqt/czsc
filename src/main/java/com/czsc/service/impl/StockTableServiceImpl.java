package com.czsc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockTableMapper;
import com.czsc.entity.StockTable;
import com.czsc.service.StockTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockTableServiceImpl extends ServiceImpl<StockTableMapper, StockTable> implements StockTableService  {
    @Autowired
    private StockTableMapper stockTableMapper;

    @Override
    public List<StockTable> getStockTablesBySymbolAndPeriod(String symbal, String period) {
        return stockTableMapper.getStockTablesBySymbolAndPeriod(symbal, period);
    }


    @Override
    public void saveStockTables(List<StockTable> result) {

    }
}
