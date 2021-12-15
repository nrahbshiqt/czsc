package com.czsc.task;

import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockDto;
import com.czsc.dto.StockSegDto;
import com.czsc.dto.StockSegPivotDto;
import com.czsc.entity.BuyStockTable;
import com.czsc.entity.StockContainsTable;
import com.czsc.entity.StockListTable;
import com.czsc.service.BuyStockTableService;
import com.czsc.service.StockListTableService;
import com.czsc.service.StockTableService;
import com.czsc.util.BuyPointUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class TestBuy3Task {
    @Autowired
    private StockListTableService stockListTableService;
    @Autowired
    private StockTableService stockTableService;
    @Autowired
    private StockContainsTableTask stockContainsTableTask;
    @Autowired
    private StockBiTableTask stockBiTableTask;
    @Autowired
    private StockSegTableTask stockSegTableTask;
    @Autowired
    private BuyStockTableService buyStockTableService;
    @Autowired
    private StockSegPivotTableTask stockSegPivotTableTask;
    @Autowired
    private HistoryStockDataTask historyStockDataTask;
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 总线程数
     **/
    private static final int TOTAL_THREAD_NUMBER = 10;
    /**
     * 允许执行的线程数
     **/
    final static int ACQUIRE_THREAD_NUMBER = 5;

    public void doubleThread() throws InterruptedException {
        Semaphore semaphore = new Semaphore(ACQUIRE_THREAD_NUMBER);
        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_THREAD_NUMBER);
        List<StockListTable> allData = stockListTableService.getAllData();
        allData.forEach(i -> {
            executorService.execute(() -> {
                try {
                    System.out.println(String.format("线程【%s】>>>>准备工作", Thread.currentThread().getName()));
                    semaphore.acquire();
                    System.out.println(String.format("线程【%s】工作===>开始", Thread.currentThread().getName()));
                    dealData3(i.getSymbol());
                    System.out.println(String.format("线程【%s】工作<====结束", Thread.currentThread().getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            });
        });
        executorService.shutdown();

        // main线程监控信号量中许可数变化
        while (true) {
            if (executorService.isTerminated()) {
                break;
            }
            System.out.println("信号量中当前可用的许可数：" + semaphore.availablePermits());
            System.out.println("等待获取的线程数:" + semaphore.getQueueLength());
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void dealData3(String symbol) {
        try {
            Set<Date> dates = new HashSet<>();
            List<StockDto> stockTables1 = new ArrayList<>();
            List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
            if (CollectionUtils.isEmpty(stockTables)) {
                return;
            }
//            stockTableService.saveBatch(stockTables);

            for (StockDto stockTable : stockTables) {
                stockTables1.add(stockTable);
                if (stockTables1.size() < 100) {
                    continue;
                }
                List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables1);
                List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables1);
                List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables1);
                List<StockSegPivotDto> stockPivotDtos = stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
                boolean b = BuyPointUtil.buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
                if (b) {
                    dates.add(stockContainsTables.get(stockContainsTables.size() - 1).getDt());
                }
            }
            if (dates.size() == 0) {
                return;
            }
            List<StockSegPivotDto> stockPivotDtos = dealData2(stockTables);
            List<BuyStockTable> buyStockTables = checkBuy3(dates, stockPivotDtos, symbol);
            buyStockTables = checkBuy4(buyStockTables, stockPivotDtos, symbol);
            buyStockTableService.saveBatch(buyStockTables);
        } catch (Exception e) {
            log.error("unknow error. symbol: {}", symbol, e);
        }
    }

    public void dealData5(String symbol) {
        try {
            List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
            if (CollectionUtils.isEmpty(stockTables)) {
                return;
            }
            List<BuyStockTable> buyStockTables = buyStockTableService.selectBySymbol(symbol);
            if (CollectionUtils.isEmpty(buyStockTables)) {
                return;
            }
            List<StockSegPivotDto> stockPivotDtos = dealData2(stockTables);
            buyStockTables = checkBuy4(buyStockTables, stockPivotDtos, symbol);
            buyStockTableService.updateBatchById(buyStockTables);
        } catch (Exception e) {
            log.error("unknow error. symbol: {}", symbol, e);
        }
    }

    public void dealData6(String symbol) {
        try {
            List<BuyStockTable> buyStockTables = buyStockTableService.selectBySymbol(symbol);
            if (CollectionUtils.isEmpty(buyStockTables)) {
                return;
            }
            List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
            if (CollectionUtils.isEmpty(stockTables)) {
                return;
            }
            List<StockDto> temp = new ArrayList<>();
            for (BuyStockTable buyStockTable : buyStockTables) {
                temp.clear();
                Date createTime = buyStockTable.getCreateTime();
                for (StockDto stockTable : stockTables) {
                    temp.add(stockTable);
                    if (stockTable.getDt().getTime() < createTime.getTime()) {
                        continue;
                    }
                    if (stockTable.getDt().getTime() == createTime.getTime()) {
                        List<StockSegDto> stockSegDtos = dealData3(temp);
                        buyStockTable.setPreBiNum(stockSegDtos.get(stockSegDtos.size() - 1).getStockBiDtos().size());
                    }
                }
            }
//            buyStockTableService.updateBatchById(buyStockTables);
        } catch (Exception e) {
            log.error("unknow error. symbol: {}", symbol, e);
        }
    }

    private List<BuyStockTable> checkBuy4(List<BuyStockTable> buyStockTables, List<StockSegPivotDto> stockPivotDtos, String symbol) {
        List<StockBiDto> stockBiDtos = new ArrayList<>();
        for (StockSegPivotDto stockPivotDto : stockPivotDtos) {
            List<StockSegDto> stockSegDtos = stockPivotDto.getStockSegDtos();
            if (stockSegDtos.size() > 2) {
                StockSegDto stockSegDto = stockSegDtos.get(2);
                stockBiDtos.addAll(stockSegDto.getStockBiDtos());
            }
            StockSegDto leaveSegDto = stockPivotDto.getLeaveSegDto();
            if (leaveSegDto != null) {
                stockBiDtos.addAll(leaveSegDto.getStockBiDtos());
            }
        }

        for (BuyStockTable buyStockTable : buyStockTables) {
            if (stockBiDtos.get(stockBiDtos.size() - 1).getEndTime().getTime() <= buyStockTable.getCreateTime().getTime()) {
                continue;
            }
            for (StockBiDto stockBiDto : stockBiDtos) {
                if (stockBiDto.getType().equals(FenxingType.TOP_PART.getValue())
                        && stockBiDto.getStockContainsTables().get(2).getDt().getTime() == buyStockTable.getCreateTime().getTime()) {
                    buyStockTable.setBuyType(1);
                    break;
                }
            }
        }
        return buyStockTables;
    }

    private List<BuyStockTable> checkBuy3(Set<Date> dates, List<StockSegPivotDto> stockPivotDtos, String symbol) {
        List<BuyStockTable> buyStockTables = new ArrayList<>();
        List<StockBiDto> stockBiDtos = new ArrayList<>();
        for (StockSegPivotDto stockPivotDto : stockPivotDtos) {
            List<StockSegDto> stockSegDtos = stockPivotDto.getStockSegDtos();
            for (StockSegDto stockSegDto : stockSegDtos) {
                stockBiDtos.addAll(stockSegDto.getStockBiDtos());
            }
        }
        StockSegDto leaveSegDto = stockPivotDtos.get(stockPivotDtos.size() - 1).getLeaveSegDto();
        if (leaveSegDto != null) {
            stockBiDtos.addAll(leaveSegDto.getStockBiDtos());
        }
        for (Date date : dates) {
            if (stockBiDtos.get(stockBiDtos.size() - 1).getEndTime().getTime() <= date.getTime()) {
                continue;
            }
            BuyStockTable buyStockTable = new BuyStockTable();
            buyStockTable.setSymbol(symbol);
            buyStockTable.setId(UUID.randomUUID().toString());
            buyStockTable.setCreateTime(date);
            for (StockBiDto stockBiDto : stockBiDtos) {
                if (stockBiDto.getType().equals(FenxingType.TOP_PART.getValue())
                        && stockBiDto.getStockContainsTables().get(2).getDt().getTime() == date.getTime()) {
                    buyStockTable.setType(1);
                    break;
                }
            }
            if (buyStockTable.getType() == null) {
                buyStockTable.setType(0);
            }
            buyStockTables.add(buyStockTable);
        }
        return buyStockTables;
    }

    public List<StockSegPivotDto> dealData2(List<StockDto> stockTables) {
//        List<StockTable> stockTables = historyStockDataTask.getHistoryStockTableBySymbol(symbol);
//        List<StockTable> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables);
        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
        List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables);
        return stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
//        boolean b = BuyPointUtil.buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
    }

    public List<StockSegDto> dealData3(List<StockDto> stockTables) {
//        List<StockTable> stockTables = historyStockDataTask.getHistoryStockTableBySymbol(symbol);
//        List<StockTable> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables);
        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
        List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables);
        List<StockSegPivotDto> stockPivotDtos = stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
        boolean b = BuyPointUtil.buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
        return stockSegDtos;

    }
}
