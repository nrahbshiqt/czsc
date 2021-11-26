package com.czsc.task;

import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.StockContainsTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class StockBiTableTask {
    private static final BigDecimal _1 = new BigDecimal("1");
    private static final BigDecimal _1000 = new BigDecimal("1000");

    public List<StockBiDto> dealData(List<StockContainsTable> stockContainsTables) {
//        List<StockContainsTable> stockContainsTables = stockContainsTableService.getStockContainsBySymbol("000001.XSHG");
        if (CollectionUtils.isEmpty(stockContainsTables)) {
            return null;
        }
        if (stockContainsTables.size() <= 60) {
            return null;
        }
        checkInitBi(stockContainsTables);
        List<StockBiDto> stockBiDtos = new ArrayList<>();
        StockBiDto stockBiDto = new StockBiDto();
        StockContainsTable stockContainsTable = stockContainsTables.get(0);
        stockBiDto.setSymbol(stockContainsTable.getSymbol());
        stockBiDto.setStartPrice(stockContainsTable.getHigh());
        stockBiDto.setStartTime(stockContainsTable.getDt());
        stockBiDto.setStartIndex(0);
        stockBiDto.setType(getOtherType(stockContainsTable.getFenxingType()));
        List<StockContainsTable> list = new ArrayList<>();
        list.add(stockContainsTable);
        stockBiDto.setStockContainsTables(list);
        stockBiDtos.add(stockBiDto);
        List<Integer> judge = Arrays.asList(0, 0, getOtherType(stockContainsTable.getFenxingType()));
        while (true) {
            judge = judge(judge.get(0), judge.get(1), judge.get(2), stockBiDtos, stockContainsTables);
            if (CollectionUtils.isEmpty(judge)) {
                break;
            }
        }
        if (CollectionUtils.isEmpty(stockBiDtos)) {
            return null;
        }
        for (StockBiDto dto : stockBiDtos) {
            Date startTime = dto.getStartTime();
            Date endTime = dto.getEndTime();
            List<StockContainsTable> containsTableList =
                    getStockContainsTableByStartTimeAndEndTime(stockContainsTables, startTime, endTime);
            dto.setStockContainsTables(containsTableList);
            dto.setForce(biForce(dto));
        }
//        List<StockBiTable> stockBiTables = new ArrayList<>();
//        for (StockBiDto dto :
//                stockBiDtos) {
//            StockBiTable stockBiTable = new StockBiTable();
//            BeanUtils.copyProperties(dto, stockBiTable);
//            stockBiTables.add(stockBiTable);
//        }
//        stockBiTableService.saveBatch(stockBiTables);
        return stockBiDtos;
    }

    public static BigDecimal biForce(StockBiDto stockBiDto) {
        try {
            Integer startIndex = stockBiDto.getStartIndex();
            Integer endIndex = stockBiDto.getEndIndex();
            BigDecimal startPrice = stockBiDto.getStartPrice();
            BigDecimal endPrice = stockBiDto.getEndPrice();
            if (endIndex == null) {
                return null;
            }
            if (startPrice == null | startPrice.equals(BigDecimal.ZERO)
                    | endPrice == null | endPrice.equals(BigDecimal.ZERO)) {
                return null;
            }
            BigDecimal abs = (endPrice.divide(startPrice, 6, BigDecimal.ROUND_HALF_UP).subtract(_1)).abs();
            return abs.divide(new BigDecimal(endIndex - startIndex), 6, BigDecimal.ROUND_HALF_UP).multiply(_1000);
        } catch (Exception e) {
            log.error("bi force error, stockBiDto: {}", stockBiDto.toString());
        }
        return null;
    }

    private List<StockContainsTable> getStockContainsTableByStartTimeAndEndTime(List<StockContainsTable> stockContainsTables, Date startTime, Date endTime) {
        List<StockContainsTable> result = new ArrayList<>();
        if (endTime == null) {
            endTime = new Date();
        }
        for (StockContainsTable stockContainsTable : stockContainsTables) {
            Date dt = stockContainsTable.getDt();
            if (dt.getTime() >= startTime.getTime() && dt.getTime() <= endTime.getTime()) {
                result.add(stockContainsTable);
            }
        }
        return result;
    }

    private Integer getOtherType(Integer type) {
        if (type.equals(FenxingType.TOP_PART.getValue())) {
            return FenxingType.BOTTOM_PART.getValue();
        } else if (type.equals(FenxingType.BOTTOM_PART.getValue())) {
            return FenxingType.TOP_PART.getValue();
        }
        return FenxingType.NONE_PART.getValue();
    }

    private void checkInitBi(List<StockContainsTable> stockContainsTables) {
        for (int i = 0; i < stockContainsTables.size(); i++) {
            if (stockContainsTables.get(i).getFenxingType() == 0) {
                stockContainsTables.remove(i);
                i--;
            } else {
                break;
            }
        }
        for (int i = 0; i < stockContainsTables.size(); i++) {
            StockContainsTable klineContainsDto = stockContainsTables.get(i);
            if (klineContainsDto.getFenxingType() != 0) {
                StockContainsTable klineContainsDto1 = stockContainsTables.get(i + 1);
                StockContainsTable klineContainsDto2 = stockContainsTables.get(i + 2);
                StockContainsTable klineContainsDto3 = stockContainsTables.get(i + 3);
                if (klineContainsDto1.getFenxingType() == 0
                        && klineContainsDto2.getFenxingType() == 0
                        && klineContainsDto3.getFenxingType() == 0) {
                    break;
                }
            }
            stockContainsTables.remove(i);
            i--;
        }
    }

    private List<Integer> judge(int preIndex, int index, int d, List<StockBiDto> biDtos, List<StockContainsTable> stockContainsTables) {
        if (index + 4 >= stockContainsTables.size() - 1) {
            return null;
        }
        if (index - preIndex < 4 || stockContainsTables.get(index).getFenxingType() != d) {
            index++;
            return judge(preIndex, index, d, biDtos, stockContainsTables);
        }
        List<Object> existNewExtreme = existNewExtreme(index, d, 2, 3, stockContainsTables);
        if (existNewExtreme.get(1) == Boolean.TRUE) {
            index = (int) existNewExtreme.get(0);
            return judge(preIndex, index, d, biDtos, stockContainsTables);
        } else {
            int k = 4;
            if (index + k + 1 >= stockContainsTables.size() - 1) {
                return null;
            }
            while (!existOpposite(index, d, k, stockContainsTables)) {
                List<Object> exist = existNewExtreme(index, d, k, k, stockContainsTables);
                if (exist.get(1) == Boolean.TRUE) {
                    index = (int) exist.get(0);
                    return judge(preIndex, index, d, biDtos, stockContainsTables);
                } else {
                    k++;
                    if (index + k >= stockContainsTables.size() - 1) {
                        return null;
                    }
                }

            }
            preIndex = index;
            index = index + k;
            StockBiDto preDto = biDtos.get(biDtos.size() - 1);
            StockContainsTable klineContainsDto = stockContainsTables.get(preIndex);
            preDto.setEndTime(klineContainsDto.getDt());
            preDto.setEndPrice(klineContainsDto.getFenxingType() == FenxingType.TOP_PART.getValue() ? klineContainsDto.getHigh() :
                    klineContainsDto.getLow());
            preDto.setEndIndex(preIndex);
            preDto.setType(d);
            StockBiDto biDto = new StockBiDto();
            biDto.setSymbol(klineContainsDto.getSymbol());
            biDto.setStartTime(preDto.getEndTime());
            biDto.setStartPrice(preDto.getEndPrice());
            biDto.setStartIndex(preDto.getEndIndex());
            biDto.setType(getOtherType(d));
            biDtos.add(biDto);
            return Arrays.asList(preIndex, index, getOtherType(d));
        }
    }

    private boolean existOpposite(int index, int d, int pos, List<StockContainsTable> stockContainsTables) {
        return stockContainsTables.get(index + pos).getFenxingType() == getOtherType(d) &&
                sameFenXing(stockContainsTables.get(index), stockContainsTables.get(index + pos), d);
    }

    private boolean sameFenXing(StockContainsTable kline1, StockContainsTable kline2, int d) {
        if (d == FenxingType.TOP_PART.getValue()) {
            return kline1.getLow().compareTo(kline2.getLow()) > 0 &&
                    kline1.getHigh().compareTo(kline2.getHigh()) > 0;
        }
        return kline1.getLow().compareTo(kline2.getLow()) < 0 &&
                kline1.getHigh().compareTo(kline2.getHigh()) < 0;
    }

    private List<Object> existNewExtreme(int index, int d, int start, int end, List<StockContainsTable> stockContainsTables) {
        int j = start;
        while (j <= end) {
            if (newExtreme(stockContainsTables.get(index), stockContainsTables.get(index + j), d)) {
                return Arrays.asList(index + j, true);
            }
            j++;
        }
        return Arrays.asList(index, false);
    }

    private boolean newExtreme(StockContainsTable kline1, StockContainsTable kline2, int d) {
        if (d == FenxingType.TOP_PART.getValue()) {
            return kline2.getHigh().compareTo(kline1.getHigh()) >= 0;
        }
        return kline1.getLow().compareTo(kline2.getLow()) >= 0;
    }
}
