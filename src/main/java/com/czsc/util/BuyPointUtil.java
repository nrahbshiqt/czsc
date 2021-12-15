package com.czsc.util;

import com.czsc.common.FenxingLevel;
import com.czsc.common.FenxingType;
import com.czsc.dto.*;
import com.czsc.entity.StockContainsTable;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class BuyPointUtil {

    private static final BigDecimal _098 = new BigDecimal("0.98");
    private static final BigDecimal _102 = new BigDecimal("1.02");
    private static final BigDecimal _11 = new BigDecimal("1.1");
    private static final BigDecimal _0618 = new BigDecimal("0.618");

    public static boolean buy3_dec(List<StockContainsTable> stockContainsTables, List<StockBiDto> stockBiDtos,
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
                && biDtos.get(0).getForce().compareTo(biDtos.get(biDtos.size() - 1).getForce()) > 0
                && getForceByMacd(biDtos.subList(0, 2)).compareTo(getForceByMacd(Arrays.asList(biDtos.get(biDtos.size() - 1)))) < 0) {
            return true;
        }
        return false;
    }

    public static boolean getWeekBiUp(List<StockDto> weekStockDto, List<StockContainsTable> stockContainsTables,
                                      List<StockBiDto> stockBiDtos, List<StockBiPivotDto> stockPivotDtos) {
        if (CollectionUtils.isEmpty(stockPivotDtos)) {
            return false;
        }
        StockDto stockDto = weekStockDto.get(weekStockDto.size() - 1);
        StockBiDto stockBiDto = stockBiDtos.get(stockBiDtos.size() - 1);
        BigDecimal maxPrice = getMaxPriceFromBiDto(stockBiDto);
        int maxIndex = getMaxIndexFromBiDto(stockContainsTables, maxPrice);
        List<StockContainsTable> tables = stockContainsTables.subList(maxIndex, stockContainsTables.size());
        List<BigDecimal> lowList = tables.stream().map(StockContainsTable::getLow).collect(Collectors.toList());
        Optional<BigDecimal> low_extre = lowList.stream().min(BigDecimal::compareTo);

        StockContainsTable stockContainsTable1 = stockContainsTables.get(stockContainsTables.size() - 1);
        StockContainsTable stockContainsTable2 = stockContainsTables.get(stockContainsTables.size() - 2);
        StockBiPivotDto stockSegPivotDto = stockPivotDtos.get(stockPivotDtos.size() - 1);

        if (stockBiDto.getType() == 1
                && stockDto.getOpen().compareTo(stockDto.getClose()) < 0
                && stockContainsTables.size() - maxIndex < 7
                && low_extre.get().compareTo(maxPrice.multiply(_0618)) > 0
                && low_extre.get().compareTo(stockSegPivotDto.getZg().multiply(_11)) > 0
                && stockContainsTable1.getOpen().compareTo(stockContainsTable1.getClose()) < 0
                && stockContainsTable2.getFenxingType().equals(FenxingType.BOTTOM_PART.getValue())
                && stockBiDto.getEndPrice().compareTo(stockSegPivotDto.getZg()) > 0
                && stockSegPivotDto.getLeaveBiDto().getStartTime().getTime() == stockBiDto.getStartTime().getTime()) {
            return true;
        }
        return false;
    }

    private static int getMaxIndexFromBiDto(List<StockContainsTable> stockContainsTables, BigDecimal maxPrice) {
        for (int i = stockContainsTables.size() - 1; i >= 0; i--) {
            StockContainsTable stockContainsTable = stockContainsTables.get(i);
            if (stockContainsTable.getHigh().compareTo(maxPrice) == 0) {
                return i;
            }
        }
        return -1;
    }

    private static BigDecimal getMaxPriceFromBiDto(StockBiDto stockBiDto) {
        List<StockContainsTable> stockContainsTables = stockBiDto.getStockContainsTables();
        return stockContainsTables.stream().map(StockContainsTable::getHigh).max((x1, x2) -> x1.compareTo(x2)).get();
    }

    public static boolean getLowBiBuy3(List<StockContainsTable> stockContainsTables, List<StockBiPivotDto> stockBiPivotDtos, List<StockDto> lowLevelsStockDtos,
                                       List<StockContainsTable> lowLevelStockContainsTable, List<StockBiDto> lowLevelStockBiDtos) {
        StockBiPivotDto stockBiPivotDto = stockBiPivotDtos.get(stockBiPivotDtos.size() - 1);
        StockBiDto leaveBiDto = stockBiPivotDto.getLeaveBiDto();
        Date time = leaveBiDto.getStartTime();
        List<StockBiDto> newLowBiDto = new ArrayList<>();

        for (int i = 0; i < lowLevelStockBiDtos.size(); i++) {
            StockBiDto stockBiDto = lowLevelStockBiDtos.get(i);
            Date startTime = stockBiDto.getStartTime();
            Date endTime = stockBiDto.getEndTime();
            if (startTime.getTime() <= time.getTime() && endTime.getTime() >= time.getTime()) {
                newLowBiDto.addAll(lowLevelStockBiDtos.subList(i, lowLevelStockBiDtos.size()));
                break;
            }
        }
        if (newLowBiDto.size() < 2) {
            return false;
        }
        StockBiDto stockBiDto = newLowBiDto.get(0);
        if (!stockBiDto.getType().equals(leaveBiDto.getType())) {
            newLowBiDto.remove(0);
        }
        List<BigDecimal> lowList = newLowBiDto.stream().map(StockBiDto::getEndPrice).collect(Collectors.toList());
        Optional<BigDecimal> low_extre = lowList.stream().min(BigDecimal::compareTo);
        if (low_extre.get().compareTo(_102.multiply(stockBiPivotDto.getZg())) <= 0) {
            return false;
        }
        System.out.println("a");
        return false;
    }


    private static int getCountByStartTimeAndEndTime(List<StockContainsTable> stockContainsTables, Date startTime, Date endTime) {
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

    private static BigDecimal getForceByMacd(List<StockBiDto> stockBiDtos) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < stockBiDtos.size(); i++) {
            StockBiDto stockBiDto = stockBiDtos.get(i);
            List<StockDto> stockTables = stockBiDto.getStockTables();
            for (StockDto stockTable : stockTables) {
                BigDecimal macd = stockTable.getMacd();
                if (macd.compareTo(BigDecimal.ZERO) < 0) {
                    total = total.add(macd);
                }
            }
        }
        return total;
    }
}
