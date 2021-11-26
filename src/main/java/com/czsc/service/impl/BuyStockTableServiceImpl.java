package com.czsc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czsc.dao.BuyStockTableMapper;
import com.czsc.entity.BuyStockTable;
import com.czsc.service.BuyStockTableService;
import org.springframework.stereotype.Service;

@Service
public class BuyStockTableServiceImpl extends ServiceImpl<BuyStockTableMapper, BuyStockTable> implements BuyStockTableService {
}
