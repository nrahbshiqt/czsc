package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockListTableMapper;
import com.czsc.entity.StockListTable;
import com.czsc.service.StockListTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockListTableServiceImpl extends ServiceImpl<StockListTableMapper, StockListTable> implements StockListTableService {
    @Autowired
    private StockListTableMapper stockListTableMapper;
    @Override
    public List<StockListTable> getAllData() {
        QueryWrapper<StockListTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("symbol");
        return stockListTableMapper.selectList(queryWrapper);
    }

    @Override
    public void updateStatusBySymbols(List<String> symbols, Integer status) {
        for (String symbol : symbols) {
            QueryWrapper<StockListTable> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("symbol", symbol);
            StockListTable stockListTable = new StockListTable();
            stockListTable.setStatus(status);
            stockListTableMapper.update(stockListTable, queryWrapper);
        }
    }

    @Override
    public List<StockListTable> getStockListTabelsByStatus(Integer status) {
        QueryWrapper<StockListTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        queryWrapper.orderByAsc("symbol");
        return stockListTableMapper.selectList(queryWrapper);
    }
}
