package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.StockBiTable;
import com.czsc.entity.StockContainsTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockBiTableMapper extends BaseMapper<StockBiTable> {
    int insert(StockBiTable record);

    List<StockBiTable> selectAll();
}