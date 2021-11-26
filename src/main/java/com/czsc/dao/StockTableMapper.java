package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.StockTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockTableMapper extends BaseMapper<StockTable> {
    int insert(StockTable record);

    List<StockTable> selectAll();

    List<StockTable> getStockTablesBySymbolAndPeriod(@Param("symbol") String symbol, @Param("period") String period);

    void replaceStockTables(List<StockTable> stockTables);

    void deleteAllData();
}