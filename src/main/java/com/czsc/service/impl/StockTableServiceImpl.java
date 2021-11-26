package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockTableMapper;
import com.czsc.entity.StockListTable;
import com.czsc.entity.StockTable;
import com.czsc.service.StockTableService;
import org.apache.commons.collections4.list.SetUniqueList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockTableServiceImpl extends ServiceImpl<StockTableMapper, StockTable> implements StockTableService  {
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    @Autowired
    private StockTableMapper stockTableMapper;

    @Override
    public List<StockTable> getStockTablesBySymbolAndPeriod(String symbal, String period) {
        QueryWrapper<StockTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbal);
        queryWrapper.eq("period", period);
//        queryWrapper.le("dt", "1997-03-07 15:00:00");
        queryWrapper.orderByAsc("dt");
        return stockTableMapper.selectList(queryWrapper);
    }


    @Override
    public void saveStockTables(List<StockTable> result) {

    }

    @Override
    public Set<String> getAllSymbol() {
        QueryWrapper<StockTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct symbol");
        List<StockTable> stockTables = stockTableMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(stockTables)) {
            return new HashSet<String>();
        }
        return stockTables.stream().map(StockTable::getSymbol).collect(Collectors.toSet());
    }

    @Override
    public Date getMaxDtBySymbol(String symbol) {
        QueryWrapper<StockTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("max(dt) as dt");
        queryWrapper.eq("symbol", symbol);
        StockTable stockTable = stockTableMapper.selectOne(queryWrapper);
        if (stockTable == null) {
            return null;
        }
        return stockTable.getDt();
    }

    @Override
    public void replaceStockTables(List<StockTable> stockTables) {
        stockTableMapper.replaceStockTables(stockTables);
    }

    @Override
    public void deleteAllData() {
        stockTableMapper.deleteAllData();
    }

    @Override
    public Date getMaxDt() {
        QueryWrapper<StockTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("max(dt) as dt");
        StockTable stockTable = stockTableMapper.selectOne(queryWrapper);
        if (stockTable == null) {
            return null;
        }
        return stockTable.getDt();
    }
}
