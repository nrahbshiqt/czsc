package com.czsc.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitStockTask {

    @Autowired
    private StockListTask stockListTask;

    @Autowired
    private StockTableTask stockTableTask;

    @Autowired
    private TodayStockDataTask todayStockDataTask;

    public void dealData() {
        stockListTask.dealData();
        stockTableTask.dealData();
        todayStockDataTask.dealData();
    }
}
