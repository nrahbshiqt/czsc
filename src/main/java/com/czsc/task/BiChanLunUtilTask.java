package com.czsc.task;

import com.czsc.common.FenxingLevel;
import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockBiPivotDto;
import com.czsc.dto.StockDto;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.BuyStockTable;
import com.czsc.entity.StockContainsTable;
import com.czsc.entity.StockListTable;
import com.czsc.entity.StockTable;
import com.czsc.service.BuyStockTableService;
import com.czsc.service.StockListTableService;
import com.czsc.service.StockTableService;
import com.czsc.util.Arithmetic;
import com.czsc.util.BuyPointUtil;
import com.czsc.util.DateStockToWeekStockUtil;
import com.czsc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
public class BiChanLunUtilTask {
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
    private BuyStockTableService buyStockTableService;
    @Autowired
    private StockBiPivotTableTask stockBiPivotTableTask;
    @Autowired
    private HistoryStockDataTask historyStockDataTask;
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd");

    public void dealData1() throws ParseException {
        List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001", "1day");
        List<StockDto> stockTables1 = new ArrayList<>();
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
            List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables1);
            List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
            boolean b = buy3_dec(stockTables1, stockContainsTables, stockBiDtos, stockPivotDtos);
            if (b) {
                dates.add(stockContainsTables.get(stockContainsTables.size() - 1).getDt());
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
                List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
                boolean b = buy3_dec(stockTables, stockContainsTables, stockBiDtos, stockPivotDtos);
                if (b) {
                    log.error("buy 3. symbol: {}", symbol);
                    BuyStockTable buyStockTable = new BuyStockTable();
                    buyStockTable.setId(UUID.randomUUID().toString());
                    buyStockTable.setSymbol(symbol);
                    buyStockTable.setName(stockListTable.getStockName());
                    buyStockTable.setType(3);
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
    public void dealData11() {
        Set<String> dates = new TreeSet<>();
        log.error("start deal current date stock buy 3. date: {}", sd.format(new Date()));
        List<StockListTable> allData = stockListTableService.getStockListTabelsByStatus(1);
        log.info("deal stock size: {}", allData.size());
        List<BuyStockTable> buyStockTables = new ArrayList<>();
        for (StockListTable stockListTable : allData) {
            String symbol = stockListTable.getSymbol();
            List<StockDto> stockDtos = stockTableService.getStockTablesBySymbolAndPeriod(symbol, "1day");
            List<StockDto> stockDtos1 = new ArrayList<>();
            if (CollectionUtils.isEmpty(stockDtos)) {
                continue;
            }
            for (StockDto stockDto : stockDtos) {
                stockDtos1.add(stockDto);
                try {
                    if (stockDto.getDt().getTime() == sd.parse("2021-12-10 15:00:00").getTime()) {
                        if (CollectionUtils.isEmpty(stockDtos1)) {
                            log.error("chanlun stock data is empty. symbol: {}", symbol);
                            continue;
                        }
                        List<StockDto> weekStockDto = DateStockToWeekStockUtil.dateToWeekStockDto(stockDtos1);
                        if (weekStockDto.size() < 10) {
                            continue;
                        }
                        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(weekStockDto);
                        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, weekStockDto);
                        List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
                        boolean b = BuyPointUtil.getWeekBiUp(weekStockDto, stockContainsTables, stockBiDtos, stockPivotDtos);
                        if (b) {
                            dates.add(symbol);
                        }
                        break;
                    }
                } catch (Exception e) {
                    log.error("unknow error. symbol: {}", symbol, e);
                }
            }
        }
        if (CollectionUtils.isEmpty(buyStockTables)) {
            log.error("today not have 3 buy stock.");
            return;
        }
//        buyStockTableService.saveBatch(buyStockTables);
        log.error("end deal current date stock buy 3. date: {}", sd.format(new Date()));
    }

    public void dealData2() {
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
                        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables1);
                        List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
                        boolean b = buy3_dec(stockTables1, stockContainsTables, stockBiDtos, stockPivotDtos);
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

    public void dealData3() {
//        List<StockDto> stockTables = historyStockDataTask.getHistoryStockTableBySymbol("000001");
        List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001", "1day");
        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(stockTables);
        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, stockTables);
        List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
        boolean b = buy3_dec(stockTables, stockContainsTables, stockBiDtos, stockPivotDtos);
    }

    public void dealData4() {
        List<StockTable> stockTables = historyStockDataTask.getHistoryStockTableBySymbol("000001");
        List<StockDto> stockDtos = new ArrayList<>();
        for (int i = 0; i < stockTables.size(); i++) {
            StockTable stockTable = stockTables.get(i);
            StockDto stockDto = new StockDto();
            BeanUtils.copyProperties(stockTable, stockDto);
            stockDtos.add(stockDto);
        }
//        List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001", "1day");
        List<StockDto> weekStockDto = DateStockToWeekStockUtil.dateToWeekStockDto(stockDtos);
        List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(weekStockDto);
        List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, weekStockDto);
        List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
        boolean b = buy3_dec(weekStockDto, stockContainsTables, stockBiDtos, stockPivotDtos);
    }

    public void dealData5() throws ParseException {
        Set<Date> dates = new TreeSet<>();
        List<StockTable> stockTables = historyStockDataTask.getHistoryStockTableBySymbol("000001");
        List<StockDto> stockDtos = new ArrayList<>();
        for (int i = 0; i < stockTables.size(); i++) {
            StockTable stockTable = stockTables.get(i);
            StockDto stockDto = new StockDto();
            BeanUtils.copyProperties(stockTable, stockDto);
            stockDtos.add(stockDto);
        }
//        List<StockDto> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001", "1day");
        List<StockDto> stockDtos1 = new ArrayList<>();
        for (StockDto stockDto : stockDtos) {
            stockDtos1.add(stockDto);
//            if (stockDto.getDt().getTime() < sd.parse("1996-07-28 15:00:00").getTime()) {
//                continue;
//            }
            if (!DateUtil.dateToWeek(stockDto.getDt()).equals("星期五")) {
                continue;
            }
            List<StockDto> weekStockDto = DateStockToWeekStockUtil.dateToWeekStockDto(stockDtos1);
            if (weekStockDto.size() < 10) {
                continue;
            }
            List<StockContainsTable> stockContainsTables = stockContainsTableTask.dealData(weekStockDto);
            List<StockBiDto> stockBiDtos = stockBiTableTask.dealData(stockContainsTables, weekStockDto);
            List<StockBiPivotDto> stockPivotDtos = stockBiPivotTableTask.dealData(stockBiDtos, stockContainsTables);
            boolean b = BuyPointUtil.getWeekBiUp(weekStockDto, stockContainsTables, stockBiDtos, stockPivotDtos);
            if (b) {
                List<StockContainsTable> lowLevelStockContainsTable = stockContainsTableTask.dealData(stockDtos1);
                List<StockBiDto> lowLevelStockBiDtos = stockBiTableTask.dealData(lowLevelStockContainsTable, stockDtos1);
                deaLastBiDto(lowLevelStockBiDtos, lowLevelStockContainsTable);
                boolean lowBiBuy3 = BuyPointUtil.getLowBiBuy3(stockContainsTables, stockPivotDtos, stockDtos1, lowLevelStockContainsTable, lowLevelStockBiDtos);
                System.out.println("a");
                dates.add(weekStockDto.get(weekStockDto.size() - 1).getDt());
            }

        }
        System.out.println("a");
    }

    private void deaLastBiDto(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables) {
        StockBiDto stockBiDto = stockBiDtos.get(stockBiDtos.size() - 1);
        Integer type = stockBiDto.getType();
        for (int i = stockContainsTables.size() - 1; i > 0; i--) {
            StockContainsTable stockContainsTable = stockContainsTables.get(i);
            if (type.equals(stockContainsTable.getFenxingType()) && stockBiDto.getStartIndex() + 4 <= i) {
                if (stockBiDto.getEndPrice() == null) {
                    stockBiDto.setEndPrice(stockContainsTable.getFenxingType().equals(FenxingType.TOP_PART.getValue()) ? stockContainsTable.getHigh() : stockContainsTable.getLow());
                    stockBiDto.setEndTime(stockContainsTable.getDt());
                    stockBiDto.setEndIndex(i);
                    stockBiDto.setUpdateTime(stockContainsTable.getDt());
                    stockBiDto.setForce(StockBiTableTask.biForce(stockBiDto));
                } else {
                    BigDecimal newPrice = stockContainsTable.getFenxingType().equals(FenxingType.TOP_PART.getValue()) ? stockContainsTable.getHigh() : stockContainsTable.getLow();
                    BigDecimal endPrice = stockBiDto.getEndPrice();
                    if ((type.equals(FenxingType.TOP_PART.getValue()) && newPrice.compareTo(endPrice) >= 0)
                            || (type.equals(FenxingType.BOTTOM_PART.getValue()) && newPrice.compareTo(endPrice) <= 0)) {
                        stockBiDto.setEndPrice(stockContainsTable.getFenxingType().equals(FenxingType.TOP_PART.getValue()) ? stockContainsTable.getHigh() : stockContainsTable.getLow());
                        stockBiDto.setEndTime(stockContainsTable.getDt());
                        stockBiDto.setEndIndex(i);
                        stockBiDto.setUpdateTime(stockContainsTable.getDt());
                        stockBiDto.setForce(StockBiTableTask.biForce(stockBiDto));
                    }
                }
            }
        }
        Date endTime = stockBiDto.getEndTime();
        if (endTime != null) {
            List<StockContainsTable> containsTableList =
                    getStockContainsTableByStartTimeAndEndTime(stockContainsTables, stockBiDto.getStartTime(), endTime);
            stockBiDto.setStockContainsTables(containsTableList);
            stockBiDto.setForce(StockBiTableTask.biForce(stockBiDto));
        }
    }
    private List<StockContainsTable> getStockContainsTableByStartTimeAndEndTime(List<StockContainsTable> stockContainsTables, Date startTime, Date endTime) {
        List<StockContainsTable> result = new ArrayList<>();
        for (StockContainsTable stockContainsTable : stockContainsTables) {
            Date dt = stockContainsTable.getDt();
            if (dt.getTime() >= startTime.getTime() && dt.getTime() <= endTime.getTime()) {
                result.add(stockContainsTable);
            }
        }
        return result;
    }


    public boolean buy3_dec(List<StockDto> stockTables, List<StockContainsTable> stockContainsTables, List<StockBiDto> stockBiDtos,
                            List<StockBiPivotDto> stockPivotDtos) {
        if (CollectionUtils.isEmpty(stockPivotDtos)) {
            return false;
        }
        StockContainsTable stockContainsTable = stockContainsTables.get(stockContainsTables.size() - 1);
        StockBiDto stockBiDto = stockBiDtos.get(stockBiDtos.size() - 1);
        StockBiPivotDto stockPivotDto = stockPivotDtos.get(stockPivotDtos.size() - 1);
        if (stockPivotDto.getFinished() != 1.0 || stockBiDto.getType() == 1 || stockPivotDto.getType().startsWith("-1_")
                || stockPivotDto.getLeaveBiDto().getType() != 1
                || stockBiDto.getType() == 1) {
            return false;
        }
        StockDto stockDto = stockTables.get(stockTables.size() - 1);

        StockContainsTable stockContainsTable2 = stockContainsTables.get(stockContainsTables.size() - 2);
        int count = getCountByStartTimeAndEndTime(stockContainsTables, stockBiDto.getEndTime(), stockContainsTable.getDt());
        if (count == 1
                && (stockContainsTable2.getFenxingPower().equals(FenxingLevel.STRONGEST.getValue()) || stockContainsTable2.getFenxingPower().equals(FenxingLevel.STRONG.getValue()))
                && stockDto.getClose().compareTo(stockDto.getMa5()) > 0
                && stockContainsTable.getType().equals(FenxingType.TOP_PART.getValue())
                && stockContainsTable.getHigh().compareTo(stockContainsTable2.getHigh()) > 0
                && stockContainsTable2.getDt().getTime() == stockBiDto.getEndTime().getTime()
                && stockContainsTable.getLow().compareTo(_098.multiply(stockPivotDto.getLeaveBiDto().getEndPrice())) < 0
                && stockContainsTable.getLow().compareTo(_102.multiply(stockPivotDto.getZg())) > 0
                && stockBiDto.getStartTime().getTime() == stockPivotDto.getLeaveBiDto().getEndTime().getTime()
                && stockPivotDto.getLeaveForce().compareTo(stockPivotDto.getPrev2Force()) > 0
                && stockPivotDto.getLeaveBiDto().getEndPrice().compareTo(stockPivotDto.getPrev2EndPrice()) > 0) {
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

}
