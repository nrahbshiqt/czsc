package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.StockContainsTable;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StockContainsTableMapper extends BaseMapper<StockContainsTable> {
    int insert(StockContainsTable record);

    List<StockContainsTable> selectAll();

    List<StockContainsTable> getStockContainsTablesBySymbolAndStartTimeAndEndTime(@Param("symbol") String symbol, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}