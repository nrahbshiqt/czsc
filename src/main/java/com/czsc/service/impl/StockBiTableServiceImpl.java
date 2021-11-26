package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockBiTableMapper;
import com.czsc.dao.StockContainsTableMapper;
import com.czsc.dto.StockBiDto;
import com.czsc.entity.StockBiTable;
import com.czsc.entity.StockContainsTable;
import com.czsc.service.StockBiTableService;
import com.czsc.service.StockContainsTableService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockBiTableServiceImpl extends ServiceImpl<StockBiTableMapper, StockBiTable> implements StockBiTableService {
    @Autowired
    private StockBiTableMapper stockBiTableMapper;

    @Autowired
    private StockContainsTableMapper stockContainsTableMapper;
    @Override
    public List<StockBiDto> getStockBiDtoBySymbol(String symbol) {
        List<StockBiTable> stockBiTables = getStockBiTableBySymbol(symbol);
        if (CollectionUtils.isEmpty(stockBiTables)) {
            return null;
        }
        List<StockBiDto> stockBiDtos = new ArrayList<>();
        for (StockBiTable stockBiTable : stockBiTables) {
            StockBiDto stockBiDto = new StockBiDto();
            BeanUtils.copyProperties(stockBiTable, stockBiDto);
            List<StockContainsTable> stockContainsTables = stockContainsTableMapper.getStockContainsTablesBySymbolAndStartTimeAndEndTime(symbol, stockBiTable.getStartTime(), stockBiTable.getEndTime());
            if (CollectionUtils.isEmpty(stockContainsTables)) {
                continue;
            }
            stockBiDto.setStockContainsTables(stockContainsTables);
            stockBiDtos.add(stockBiDto);
        }
        return stockBiDtos;
    }

    private List<StockBiTable> getStockBiTableBySymbol(String symbol) {
        QueryWrapper<StockBiTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbol);
        queryWrapper.orderByAsc("start_time");
        return stockBiTableMapper.selectList(queryWrapper);
    }
}
