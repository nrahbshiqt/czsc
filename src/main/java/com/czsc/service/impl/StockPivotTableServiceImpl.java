package com.czsc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockPivotTableMapper;
import com.czsc.entity.StockPivotTable;
import com.czsc.service.StockPivotTableService;
import org.springframework.stereotype.Service;

@Service
public class StockPivotTableServiceImpl extends ServiceImpl<StockPivotTableMapper, StockPivotTable> implements StockPivotTableService {
}