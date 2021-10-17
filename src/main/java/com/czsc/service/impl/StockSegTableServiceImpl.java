package com.czsc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.StockSegTableMapper;
import com.czsc.entity.StockSegTable;
import com.czsc.service.StockSegTableService;
import org.springframework.stereotype.Service;

@Service
public class StockSegTableServiceImpl extends ServiceImpl<StockSegTableMapper, StockSegTable> implements StockSegTableService {
}
