package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockTableMapper;
import com.czsc.dto.StockDto;
import com.czsc.entity.StockListTable;
import com.czsc.entity.StockTable;
import com.czsc.service.StockTableService;
import com.czsc.util.Arithmetic;
import org.apache.commons.collections4.list.SetUniqueList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockTableServiceImpl extends ServiceImpl<StockTableMapper, StockTable> implements StockTableService  {
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    @Autowired
    private StockTableMapper stockTableMapper;

    @Override
    public List<StockDto> getStockTablesBySymbolAndPeriod(String symbal, String period) {
        QueryWrapper<StockTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbal);
        queryWrapper.eq("period", period);
//        queryWrapper.le("dt", "1997-03-07 15:00:00");
        queryWrapper.orderByAsc("dt");
        List<StockTable> stockTables = stockTableMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(stockTables)) {
            return null;
        }
        List<BigDecimal> closeList = stockTables.stream().map(StockTable::getClose).collect(Collectors.toList());
//        List<List<BigDecimal>> macd = Arithmetic.getMACD(closeList, 12, 26, 9);
//        List<BigDecimal> macds = macd.get(2);
        List<BigDecimal> ma5s = Arithmetic.getMA(closeList, 5);
        List<BigDecimal> ma10s = Arithmetic.getMA(closeList, 10);
        assert ma5s.size() == stockTables.size();
        assert ma10s.size() == stockTables.size();
        List<StockDto> stockDtos = new ArrayList<>();
        for (int i = 0; i < stockTables.size(); i++) {
            StockTable stockTable = stockTables.get(i);
            StockDto stockDto = new StockDto();
            BeanUtils.copyProperties(stockTable, stockDto);
//            stockDto.setMacd(macds.get(i));
            stockDto.setMa5(ma5s.get(i));
            stockDto.setMa10(ma10s.get(i));
            stockDtos.add(stockDto);
        }
        return stockDtos;
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
