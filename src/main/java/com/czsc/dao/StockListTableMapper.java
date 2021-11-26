package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.StockListTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockListTableMapper extends BaseMapper<StockListTable> {
    int insert(StockListTable record);

    List<StockListTable> selectAll();
}