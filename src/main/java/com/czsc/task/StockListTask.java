package com.czsc.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.czsc.entity.StockListTable;
import com.czsc.request.RequestUtil;
import com.czsc.service.StockListTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StockListTask {
    @Autowired
    private StockListTableService stockListTableService;

    public void dealData() {
        List<StockListTable> allData = stockListTableService.getAllData();
        if (CollectionUtils.isEmpty(allData)) {
            initStockList();
            return;
        }
        Set<String> symbolSet = allData.stream().map(StockListTable::getSymbol).collect(Collectors.toSet());
        List<StockListTable> stockListTables = getRequestData();
        if (CollectionUtils.isEmpty(stockListTables)) {
            log.error("get new stock list empty.");
        }

        List<StockListTable> saveList = new ArrayList<>();
        Set<String> insertSymbols = new HashSet<>();
        for (StockListTable stockListTable : stockListTables) {
            String symbol = stockListTable.getSymbol();
            if (symbolSet.contains(symbol)) {
                continue;
            }
            if (insertSymbols.contains(symbol)) {
                continue;
            }
            insertSymbols.add(symbol);
            saveList.add(stockListTable);
        }
        if (!CollectionUtils.isEmpty(saveList)) {
           stockListTableService.saveBatch(saveList);
        }
    }

    private List<StockListTable> getRequestData() {
        JSONArray stockList = RequestUtil.getStockList();
        if (CollectionUtils.isEmpty(stockList)) {
            log.error("init stock list error.");
        }
        List<StockListTable> stockListTables = new ArrayList<>();
        for (int i = 0; i < stockList.size(); i++) {
            JSONObject json = stockList.getJSONObject(i);
            String name = json.getString("NAME");
            String symbol = json.getString("SYMBOL");
            StockListTable stockListTable = new StockListTable();
            stockListTable.setStockName(name);
            stockListTable.setSymbol(symbol);
            stockListTable.setStatus(1);
            stockListTable.setUpdateTime(new Date());
            stockListTables.add(stockListTable);
        }
        return stockListTables;
    }

    public List<StockListTable> initStockList() {
        List<StockListTable> stockListTables = getRequestData();
        if (CollectionUtils.isEmpty(stockListTables)) {
            return null;
        }
        stockListTableService.saveBatch(stockListTables);
        return stockListTables;
    }
}
