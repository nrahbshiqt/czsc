package com.czsc.task;

import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockDto;
import com.czsc.dto.StockSegPivotDto;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.BuyStockTable;
import com.czsc.entity.StockContainsTable;
import com.czsc.entity.StockListTable;
import com.czsc.service.BuyStockTableService;
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
import java.util.stream.Collectors;

@Slf4j
@Component
public class SegChanLunUtilTask {
    private static final BigDecimal _098 = new BigDecimal("0.98");
    private static final BigDecimal _102 = new BigDecimal("1.02");
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

    public void dealData1() throws ParseException {
        List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001", "1day");
        ArrayList<StockDto> stockTables1 = new ArrayList<>();
        Set<Date> dates = new HashSet<>();
        for (StockDto stockTable : stockTables) {
            stockTables1.add(stockTable);
            if (stockTables1.size() < 100) {
                continue;
            }
//            if (stockTable.getDt().getTime() < sd.parse("1999-12-15 15:00:00").getTime()) {
//                continue;
//            }
            List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables1);
            List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
            List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables);
            List<StockSegPivotDto> stockPivotDtos = stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
            boolean b = buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
            if (b) {
                dates.add(stockContainsTables.get(stockContainsTables.size() -1).getDt());
            }
        }
        System.out.println("a");
    }

    public void dealData() {
        log.error("start deal current date stock buy 3. date: {}", sd.format(new Date()));
        List<StockListTable> allData = stockListTableService.getStockListTabelsByStatus(1);
        log.info("deal stock size: {}", allData.size());
        List<BuyStockTable> buyStockTables = new ArrayList<>();
        for (StockListTable stockListTable : allData) {
            String symbol = stockListTable.getSymbol();
            try {
                List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
                if (CollectionUtils.isEmpty(stockTables)) {
                    log.error("chanlun stock data is empty. symbol: {}", symbol);
                    continue;
                }
                StockDto lastStockTable = stockTables.get(stockTables.size() - 1);
                if (lastStockTable.getDt().getTime() != sd.parse(sdd.format(new Date()) + " 15:00:00").getTime()) {
                    log.error("stock new data is not current date. " +
                            "symbol:{}, dt: {}, current date: {}", symbol, sd.format(lastStockTable.getDt()), sdd.format(new Date()) + " 15:00:00");
                    continue;
                }
                List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables);
                List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
                List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables);
                List<StockSegPivotDto> stockPivotDtos = stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
                boolean b = buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
                if (b) {
                    log.error("buy 3. symbol: {}", symbol);
                    BuyStockTable buyStockTable = new BuyStockTable();
                    buyStockTable.setId(UUID.randomUUID().toString());
                    buyStockTable.setSymbol(symbol);
                    buyStockTable.setName(stockListTable.getStockName());
                    buyStockTable.setType(3);
                    buyStockTable.setPreBiNum(stockSegDtos.get(stockSegDtos.size() - 1).getStockBiDtos().size());
                    buyStockTable.setCreateTime(new Date());
                    buyStockTables.add(buyStockTable);
                }
            } catch (Exception e) {
                log.error("unknow error. symbol: {}", symbol, e);
            }
        }
        if (CollectionUtils.isEmpty(buyStockTables)) {
            log.error("today not have 3 buy stock.");
            return;
        }
        buyStockTableService.saveBatch(buyStockTables);
        log.error("end deal current date stock buy 3. date: {}", sd.format(new Date()));

    }

    public void dealData3() {
        List<StockListTable> allData = stockListTableService.getStockListTabelsByStatus(1);
        Set<String> buyStocks = new HashSet<>();
        Map<Date, List<StockDto>> map = new HashMap<>();
        for (StockListTable stockListTable : allData) {
            String symbol = stockListTable.getSymbol();
            List<StockDto> stockTables1 = new ArrayList<>();
            List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
            try {
                for (StockDto stockTable : stockTables) {
                    stockTables1.add(stockTable);
                    if (stockTables1.size() < 100) {
                        continue;
                    }
                    if (stockTable.getDt().getTime() > sd.parse("2021-11-15 00:00:00").getTime()
                            && stockTable.getDt().getTime() < sd.parse("2021-11-20 00:00:00").getTime()) {
                        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables1);
                        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
                        List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables);
                        List<StockSegPivotDto> stockPivotDtos = stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
                        boolean b = buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
                        if (b) {
                            buyStocks.add(symbol);
                            List<StockDto> tables = map.get(stockTable.getDt());
                            if (CollectionUtils.isEmpty(tables)) {
                                tables = new ArrayList<>();
                            }
                            tables.add(stockTable);
                            map.put(stockTable.getDt(), tables);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("unknow error. symbol: {}", symbol, e);
            }
        }
        System.out.println("a");

    }

    public void dealData2() {
//        List<StockTable> stockTables = historyStockDataTask.getHistoryStockTableBySymbol("000001");
        List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001", "1day");

        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables);
        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
        List<StockSegDto> stockSegDtos = stockSegTableTask.dealData(stockBiDtos, stockContainsTables, stockTables);
        List<StockSegPivotDto> stockPivotDtos = stockSegPivotTableTask.dealData(stockSegDtos, stockBiDtos, stockContainsTables);
        boolean b = buy3_dec(stockContainsTables, stockBiDtos, stockSegDtos, stockPivotDtos);
    }


    public boolean buy3_dec(List<StockContainsTable> stockContainsTables, List<StockBiDto> stockBiDtos,
                            List<StockSegDto> stockSegDtos, List<StockSegPivotDto> stockPivotDtos) {
        if (CollectionUtils.isEmpty(stockPivotDtos)) {
            return false;
        }
        StockContainsTable stockContainsTable = stockContainsTables.get(stockContainsTables.size() - 1);
        StockBiDto stockBiDto = stockBiDtos.get(stockBiDtos.size() - 1);
        StockSegDto stockSegDto = stockSegDtos.get(stockSegDtos.size() - 1);
        List<StockBiDto> biDtos = stockSegDto.getStockBiDtos();
        StockSegPivotDto stockPivotDto = stockPivotDtos.get(stockPivotDtos.size() - 1);
        if (stockPivotDto.getFinished() != 1.0 || stockSegDto.getType() == 1
                || stockPivotDto.getLeaveSegDto().getType() != 1
                || stockBiDto.getType() == 1
                || biDtos == null || biDtos.size() < 3) {
            return false;
        }

        StockContainsTable stockContainsTable3 = stockContainsTables.get(stockContainsTables.size() - 3);
        StockContainsTable stockContainsTable2 = stockContainsTables.get(stockContainsTables.size() - 2);
        List<BigDecimal> lowList = biDtos.stream().map(StockBiDto::getEndPrice).collect(Collectors.toList());
        Optional<BigDecimal> low_extre = lowList.stream().min(BigDecimal::compareTo);
        int count = getCountByStartTimeAndEndTime(stockContainsTables, stockSegDto.getEndTime(), stockContainsTable.getDt());
        if (count == 2
                && low_extre.get().compareTo(_102.multiply(stockPivotDto.getZg())) > 0
                && stockContainsTable.getType().equals(FenxingType.TOP_PART.getValue())
                && stockContainsTable.getHigh().compareTo(stockContainsTable2.getHigh()) > 0
                && stockContainsTable3.getDt().getTime() == stockSegDto.getEndTime().getTime()
                && stockContainsTable.getLow().compareTo(_098.multiply(stockPivotDto.getLeaveSegDto().getEndPrice())) < 0
                && stockContainsTable.getLow().compareTo(_102.multiply(stockPivotDto.getZg())) > 0
                && stockSegDto.getStartTime().getTime() == stockPivotDto.getLeaveSegDto().getEndTime().getTime()
                && stockPivotDto.getLeaveForce().compareTo(stockPivotDto.getPrev2Force()) > 0
                && stockPivotDto.getLeaveSegDto().getEndPrice().compareTo(stockPivotDto.getPrev2EndPrice()) > 0
                && biDtos.get(0).getForce().compareTo(biDtos.get(biDtos.size() -1).getForce()) > 0) {
            return true;
        }
        return false;
    }

    private int getCountByStartTimeAndEndTime(List<StockContainsTable> stockContainsTables, Date startTime, Date endTime) {
        int count = 0;
        for (StockContainsTable stockContainsTable : stockContainsTables) {
            long time = stockContainsTable.getDt().getTime();
            if (startTime.getTime() <= time && endTime.getTime() > time) {
                count++;
            }
            if (endTime.getTime() <= time) {
                break;
            }
        }
        return count;
    }

    public boolean lowLevelBuy3Des(List<StockContainsTable> stockContainsTables, List<StockBiDto> stockBiDtos,
                                   List<StockSegDto> stockSegDtos, List<StockSegPivotDto> stockPivotDtos) {
        if (CollectionUtils.isEmpty(stockPivotDtos)) {
            return false;
        }
        StockContainsTable stockContainsTable = stockContainsTables.get(stockContainsTables.size() - 1);
        StockBiDto stockBiDto = stockBiDtos.get(stockBiDtos.size() - 1);
        StockSegDto stockSegDto = stockSegDtos.get(stockSegDtos.size() - 1);
        List<StockBiDto> biDtos = stockSegDto.getStockBiDtos();
        StockSegPivotDto stockPivotDto = stockPivotDtos.get(stockPivotDtos.size() - 1);
        if (stockPivotDto.getFinished() == 1.0 || stockPivotDto.getFinished() == 0.5 || stockSegDto.getType() != 1
                || stockPivotDto.getLeaveSegDto().getType() != 1
                || stockBiDto.getType() == 1
                || biDtos == null || biDtos.size() < 3) {
            return false;
        }
        StockBiDto segLastBiDto = biDtos.get(biDtos.size() - 1);
        StockContainsTable stockContainsTable3 = stockContainsTables.get(stockContainsTables.size() - 3);
        StockContainsTable stockContainsTable2 = stockContainsTables.get(stockContainsTables.size() - 2);
        int count = getCountByStartTimeAndEndTime(stockContainsTables, stockBiDto.getEndTime(), stockContainsTable.getDt());
        if (count == 2
                && segLastBiDto.getType() == 1
                && segLastBiDto.getEndTime().getTime() == stockBiDto.getStartTime().getTime()
                && stockBiDto.getEndPrice().compareTo(_102.multiply(stockPivotDto.getZg())) > 0
                && stockContainsTable.getHigh().compareTo(stockContainsTable2.getHigh()) > 0
                && stockContainsTable3.getDt().getTime() == stockBiDto.getEndTime().getTime()) {
            return true;
        }

        return false;
    }
}
