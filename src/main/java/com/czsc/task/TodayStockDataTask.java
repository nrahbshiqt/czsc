package com.czsc.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.czsc.entity.StockListTable;
import com.czsc.entity.StockTable;
import com.czsc.request.RequestUtil;
import com.czsc.service.StockListTableService;
import com.czsc.service.StockTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
@EnableScheduling
public class TodayStockDataTask {
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private static final SimpleDateFormat sdd = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private StockTableService stockTableService;
    @Autowired
    private StockListTableService stockListTableService;
    @Autowired
    private StockListTask stockListTask;
    @Autowired
    private ChanLunUtilTask chanLunUtilTask;

    @Scheduled(cron = "0 0 16 * * ?")
    public void dealData() throws ParseException {
        List<StockListTable> stockListTables = stockListTableService.getStockListTabelsByStatus(1);
        if (CollectionUtils.isEmpty(stockListTables)) {
            stockListTables = initStockList();
        }
        List<String> symbols = new ArrayList<>();
        List<StockTable> stockTables = new ArrayList<>();
        for (StockListTable stockListTable : stockListTables) {
            String symbol = stockListTable.getSymbol();
            try {
                Thread.sleep(1000);

                StockTable stockTable = getStockTable(symbol);
                if (stockTable == null) {
                    symbols.add(symbol);
                }
                stockTables.add(stockTable);
            } catch (Exception e) {
                log.error("get history data error. symbol : {}", symbol, e);
            }
        }
        if (!CollectionUtils.isEmpty(symbols)) {
            Iterator<String> it = symbols.iterator();
            while (it.hasNext()) {
                String symbol = it.next();
                try {
                    Thread.sleep(1000);
                    StockTable stockTable = getStockTable(symbol);
                    if (stockTable != null) {
                        stockTables.add(stockTable);
                        it.remove();
                    }
                } catch (Exception e) {
                    log.error("get history data error. symbol : {}", symbol, e);
                }
            }
        }
        stockTableService.replaceStockTables(stockTables);
        Date dt = stockTableService.getMaxDt();
        if (sdd.format(dt).equals(sdd.format(new Date()))) {
            chanLunUtilTask.dealData();
        }
    }

    private StockTable getStockTable(String symbol) throws ParseException {
        JSONObject json = RequestUtil.getTodayStockData2(symbol);
        String code = "hs_"+ symbol;
        json = json.getJSONObject(code);
        String date = json.getString("1");
        String open = json.getString("7");
        String high = json.getString("8");
        String low = json.getString("9");
        String close = json.getString("11");
        Integer volumn = json.getInteger("13");
        StockTable stockTable = new StockTable();
        Date dt = sd.parse(date + " 15:00:00");
        stockTable.setDt(dt);
        stockTable.setOpen(new BigDecimal(open));
        stockTable.setHigh(new BigDecimal(high));
        stockTable.setLow(new BigDecimal(low));
        stockTable.setClose(new BigDecimal(close));
        stockTable.setVolume(new BigDecimal(volumn));
        stockTable.setUpdateTime(dt);
        stockTable.setSymbol(symbol);
        stockTable.setPeriod("1day");
        return stockTable;
    }

    private List<StockListTable> initStockList() {
        return stockListTask.initStockList();
    }
}
