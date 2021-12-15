package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.BuyStockTable;
import com.czsc.entity.StockBiTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyStockTableMapper extends BaseMapper<BuyStockTable> {
    int insert(BuyStockTable record);

    List<BuyStockTable> selectAll();
}