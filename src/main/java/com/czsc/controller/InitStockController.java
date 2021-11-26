package com.czsc.controller;

import com.czsc.result.Result;
import com.czsc.task.InitStockTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class InitStockController {
    private Date init_stock_date = null;
    private int five_minutes = 5 * 60 * 1000;
    @Autowired
    private InitStockTask initStockTask;

    @RequestMapping(value = "/init_stock", method = RequestMethod.GET)
    public Result initStock() {
        Date currentDate = new Date();
        if (init_stock_date == null) {
            init_stock_date = currentDate;
            return new Result(0, "again");
        }
        if (init_stock_date.getTime() + five_minutes > currentDate.getTime()) {
            initStockTask.dealData();
        } else {
            init_stock_date = currentDate;
            return new Result(0, "overtime, please again");
        }
        return new Result(1, "success");
    }


}
