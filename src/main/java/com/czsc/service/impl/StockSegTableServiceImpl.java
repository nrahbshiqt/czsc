package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockSegTableMapper;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.StockSegTable;
import com.czsc.service.StockBiTableService;
import com.czsc.service.StockSegTableService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StockSegTableServiceImpl extends ServiceImpl<StockSegTableMapper, StockSegTable> implements StockSegTableService {

    @Autowired
    private StockSegTableMapper stockSegTableMapper;

    @Autowired
    private StockBiTableService stockBiTableService;

    @Override
    public List<StockSegDto> getStockSegDtosBySymbol(String symbol) {
        List<StockSegTable> stockSegTables = getStockSegTableBySymbol(symbol);
        if (CollectionUtils.isEmpty(stockSegTables)) {
            return null;
        }
        List<StockBiDto> stockBiDtos = stockBiTableService.getStockBiDtoBySymbol(symbol);
        if (CollectionUtils.isEmpty(stockBiDtos)) {
            return null;
        }
        List<StockSegDto> stockSegDtos = new ArrayList<>();
        for (StockSegTable stockSegTable : stockSegTables) {
            StockSegDto stockSegDto = new StockSegDto();
            BeanUtils.copyProperties(stockSegTable, stockSegDto);
            setStockBiDtos(stockSegDto, stockBiDtos);
            stockSegDtos.add(stockSegDto);
        }
        return stockSegDtos;
    }

    private void setStockBiDtos(StockSegDto stockSegDto, List<StockBiDto> stockBiDtos) {
        Date startTime = stockSegDto.getStartTime();
        Date endTime = stockSegDto.getEndTime();
        List<StockBiDto> list = new ArrayList<>();
        for (StockBiDto stockBiDto : stockBiDtos) {
            if (stockBiDto.getStartTime().getTime() >= startTime.getTime()
                    && stockBiDto.getStartTime().getTime() < endTime.getTime()) {
                list.add(stockBiDto);
            }
        }
        stockSegDto.setStockBiDtos(list);
    }

    private List<StockSegTable> getStockSegTableBySymbol(String symbol) {
        QueryWrapper<StockSegTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbol);
        queryWrapper.orderByAsc("start_time");
        return stockSegTableMapper.selectList(queryWrapper);
    }
}
