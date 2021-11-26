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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class HistoryStockDataTask {
    private static final List<Integer> StockYearList = new ArrayList<>();
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Autowired
    private StockTableService stockTableService;
    @Autowired
    private StockListTableService stockListTableService;
    @Autowired
    private StockListTask stockListTask;

    public void dealData() {
        List<StockListTable> stockListTables = stockListTableService.getStockListTabelsByStatus(1);
        if (CollectionUtils.isEmpty(stockListTables)) {
            stockListTables = initStockList();
        }
        Set<String> symbolList = stockTableService.getAllSymbol();
        for (StockListTable stockListTable : stockListTables) {
            String symbol = stockListTable.getSymbol();
            if (symbolList.contains(symbol)) {
                continue;
            }
            try {
                Thread.sleep(1000);
                JSONObject json = RequestUtil.getStockHistoryData2(symbol);
                JSONArray sortYear = json.getJSONArray("sortYear");
                StockYearList.clear();
                Map<Integer, Integer> yearCountMap = new HashMap<>();
                for (int i = 0; i < sortYear.size(); i++) {
                    StockYearList.add(sortYear.getJSONArray(i).getInteger(0));
                    yearCountMap.put(sortYear.getJSONArray(i).getInteger(0), sortYear.getJSONArray(i).getInteger(1));
                }
                Integer total = json.getInteger("total");
                String priceStr = json.getString("price");
                String volumnStr = json.getString("volumn");
                String dateStr = json.getString("dates");
                String priceFactor = json.getString("priceFactor");
                List<StockTable> stockTables = buildStockTable(symbol, total, dateStr, priceStr, priceFactor, volumnStr, yearCountMap);
                if (CollectionUtils.isEmpty(stockTables)) {
                    log.error("get history data empty. symbol : {}", symbol);
                }
                stockTableService.saveBatch(stockTables);
            } catch (Exception e) {
                log.error("get history data error. symbol : {}", symbol, e);
            }
        }
    }

    public List<StockTable> getHistoryStockTableBySymbol(String symbol) {
        try {
            Thread.sleep(1000);
            JSONObject json = RequestUtil.getStockHistoryData1(symbol);
            JSONArray sortYear = json.getJSONArray("sortYear");
            StockYearList.clear();
            Map<Integer, Integer> yearCountMap = new HashMap<>();
            for (int i = 0; i < sortYear.size(); i++) {
                StockYearList.add(sortYear.getJSONArray(i).getInteger(0));
                yearCountMap.put(sortYear.getJSONArray(i).getInteger(0), sortYear.getJSONArray(i).getInteger(1));
            }
            Integer total = json.getInteger("total");
            String priceStr = json.getString("price");
            String volumnStr = json.getString("volumn");
            String dateStr = json.getString("dates");
            String priceFactor = json.getString("priceFactor");
            List<StockTable> stockTables = buildStockTable(symbol, total, dateStr, priceStr, priceFactor, volumnStr, yearCountMap);
            if (CollectionUtils.isEmpty(stockTables)) {
                log.error("get history data empty. symbol : {}", symbol);
            }
            return stockTables;
        } catch (Exception e) {
            log.error("get history data error. symbol : {}", symbol, e);
        }
        return null;
    }
    public List<StockTable> getHistoryStockTableBySymbol11(String symbol) {
        try {
            Thread.sleep(1000);
            JSONObject json = RequestUtil.getStockHistoryData1(symbol);
            JSONArray sortYear = json.getJSONArray("sortYear");
            StockYearList.clear();
            Map<Integer, Integer> yearCountMap = new HashMap<>();
            for (int i = 0; i < sortYear.size(); i++) {
                StockYearList.add(sortYear.getJSONArray(i).getInteger(0));
                yearCountMap.put(sortYear.getJSONArray(i).getInteger(0), sortYear.getJSONArray(i).getInteger(1));
            }
            Integer total = json.getInteger("total");
            String priceStr = json.getString("price");
            String volumnStr = json.getString("volumn");
            String dateStr = json.getString("dates");
            String priceFactor = json.getString("priceFactor");
            List<StockTable> stockTables = buildStockTable(symbol, total, dateStr, priceStr, priceFactor, volumnStr, yearCountMap);
            if (CollectionUtils.isEmpty(stockTables)) {
                log.error("get history data empty. symbol : {}", symbol);
            }
            stockTableService.saveBatch(stockTables);
            return stockTables;
        } catch (Exception e) {
            log.error("get history data error. symbol : {}", symbol, e);
        }
        return null;
    }

    private static List<StockTable> buildStockTable(String symbol, Integer total, String dateStr, String priceStr, String priceFactor, String volumnStr, Map<Integer, Integer> yearCountMap) throws
            ParseException {
        BigDecimal priceFactorBd = new BigDecimal(priceFactor);
        List<StockTable> stockTables = new ArrayList<>();
        String[] dateArr = dateStr.split(",");
        String[] priceArr = priceStr.split(",");
        List<Object[]> priceList = splitArray(priceArr, 4);
        String[] volumnArr = volumnStr.split(",");
        Integer yearCount = 0;
        for (int i = 0; i < dateArr.length; i++) {
            String date = dateArr[i];
            StockTable stockTable = new StockTable();
            stockTable.setSymbol("000009_pre");
            stockTable.setPeriod("1day");

            Integer year = StockYearList.get(0);
            Integer count = yearCountMap.get(year);
            if (yearCount.equals(count)) {
                StockYearList.remove(0);
                yearCount = 0;
            }
            yearCount++;
            String dtStr = StockYearList.get(0) + date + " 15:00:00";
            Date dt = sd.parse(dtStr);

            stockTable.setDt(dt);
            Object[] prices = priceList.get(i);
            //1932,31,42,18 open,high,close
            stockTable.setLow(new BigDecimal(prices[0].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP));
            stockTable.setOpen(stockTable.getLow().add(new BigDecimal(prices[1].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP)));
            stockTable.setHigh(stockTable.getLow().add(new BigDecimal(prices[2].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP)));
            stockTable.setClose(stockTable.getLow().add(new BigDecimal(prices[3].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP)));
            stockTable.setVolume(new BigDecimal(volumnArr[i]));
            stockTable.setUpdateTime(stockTable.getDt());
            if (stockTables.size() > 0) {
                StockTable preStock = stockTables.get(stockTables.size() - 1);
                if (preStock.getDt().getTime() >= stockTable.getDt().getTime()) {
                    log.error("the date is simile pre date. current stock: {}, pre stock: {}", stockTable.toString(), preStock.toString());
                    continue;
                }
            }
            stockTables.add(stockTable);
        }
        return stockTables;
    }

    /**
     * 分割数组
     *
     * @param array 原数组
     * @param Size  分割后每个数组的最大长度
     * @param <T>   原数组的类型
     * @return
     */
    public static <T> List<Object[]> splitArray(T[] array, int Size) {
        List<Object[]> list = new ArrayList<Object[]>();
        int i = (array.length) % Size == 0 ? (array.length) / Size : (array.length) / Size + 1;
        for (int j = 0; j < i; j++) {
            List<T> list1 = new ArrayList<T>();
            for (int k = 0; k < Size; k++) {
                if ((j * Size + k) >= array.length) {
                    break;
                } else {
                    list1.add((array[j * Size + k]));
                }
            }
            /**
             * list.toArray()之后只能转成Object类型的数组，不能转成int和Integer等
             * 否则会报：java.lang.ClassCastException:
             * [Ljava.lang.Object; cannot be cast to [Ljava.lang.Integer;
             * 非检测性异常
             * 所以只能是List<Object[]>，不能是List<T[]>
             */
            list.add(list1.toArray());
        }
        return list;
    }

    private List<StockListTable> initStockList() {
        return stockListTask.initStockList();
    }
}
