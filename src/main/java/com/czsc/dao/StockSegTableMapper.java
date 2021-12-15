package com.czsc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czsc.entity.StockContainsTable;
import com.czsc.entity.StockSegTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockSegTableMapper extends BaseMapper<StockSegTable> {
    int insert(StockSegTable record);

    List<StockSegTable> selectAll();
}