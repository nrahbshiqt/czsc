package com.czsc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.BuyStockTableMapper;
import com.czsc.entity.BuyStockTable;
import com.czsc.service.BuyStockTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyStockTableServiceImpl extends ServiceImpl<BuyStockTableMapper, BuyStockTable> implements BuyStockTableService {
    @Autowired
    private BuyStockTableMapper buyStockTableMapper;
    @Override
    public List<BuyStockTable> selectBySymbol(String symbol) {
        QueryWrapper<BuyStockTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbol);
        queryWrapper.orderByAsc("create_time");
        return buyStockTableMapper.selectList(queryWrapper);
    }
}
