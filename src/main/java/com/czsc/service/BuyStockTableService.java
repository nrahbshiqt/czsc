package com.czsc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czsc.entity.BuyStockTable;

import java.util.List;


public interface BuyStockTableService extends IService<BuyStockTable> {
    List<BuyStockTable> selectBySymbol(String symbol);
}
