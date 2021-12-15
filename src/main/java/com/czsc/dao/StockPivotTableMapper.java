package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.StockPivotTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockPivotTableMapper extends BaseMapper<StockPivotTable> {
    int insert(StockPivotTable record);

    List<StockPivotTable> selectAll();
}