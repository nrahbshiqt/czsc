package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockContainsTableMapper;
import com.czsc.entity.StockContainsTable;
import com.czsc.service.StockContainsTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockContainsTableServiceImpl extends ServiceImpl<StockContainsTableMapper, StockContainsTable> implements StockContainsTableService {
    @Autowired
    private StockContainsTableMapper stockFenxingTableMapper;

    @Override
    public List<StockContainsTable> getStockContainsBySymbol(String symbol) {
        QueryWrapper<StockContainsTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbol);
        queryWrapper.orderByAsc("dt");
        return stockFenxingTableMapper.selectList(queryWrapper);

    }
}
