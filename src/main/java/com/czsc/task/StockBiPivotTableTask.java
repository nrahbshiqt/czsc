package com.czsc.task;

import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockBiPivotDto;
import com.czsc.entity.StockContainsTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class StockBiPivotTableTask {
    private static final BigDecimal _1 = new BigDecimal("1");
    private static final BigDecimal _05 = new BigDecimal("0.5");
    private static final BigDecimal _1000 = new BigDecimal("1000");
    private static final List<Integer> pivotSizeList = Arrays.asList(4, 7, 10, 19, 28);
    private static final List<Integer> pivotLevelSizeList = Arrays.asList(10, 28);

    public List<StockBiPivotDto> dealData(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables) {
//        List<StockBiDto> stockBiDtos = stockBiTableService.getStockBiDtosBySymbol("000001.XSHG");
        if (CollectionUtils.isEmpty(stockBiDtos)) {
            return null;
        }
        deaLastBiDto(stockBiDtos, stockContainsTables);
        List<StockBiPivotDto> stockPivotDtos = getPivot(stockBiDtos, stockContainsTables);
        processPivot(stockPivotDtos);
        return stockPivotDtos;
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

    private void processPivot(List<StockBiPivotDto> stockPivotDtos) {
        for (int i = 0; i < stockPivotDtos.size() - 1; i++) {
            StockBiPivotDto stockPivotDto = stockPivotDtos.get(i);
            StockBiPivotDto stockPivotDto1 = stockPivotDtos.get(i + 1);
            if (stockPivotDto.getLevel() == 1 && stockPivotDto1.getLevel() == 1) {
                if (stockPivotDto.getDd().compareTo(stockPivotDto1.getGg()) > 0) {
                    stockPivotDto1.setTrend(-1);
                } else {
                    if (stockPivotDto.getGg().compareTo(stockPivotDto1.getDd()) < 0) {
                        stockPivotDto1.setTrend(1);
                    } else {
                        stockPivotDto1.setTrend(0);
                    }
                }
            } else {
                if (stockPivotDto.getGg().compareTo(stockPivotDto1.getGg()) > 0
                        && stockPivotDto.getDd().compareTo(stockPivotDto1.getDd()) > 0) {
                    stockPivotDto1.setTrend(-1);
                } else {
                    if (stockPivotDto.getGg().compareTo(stockPivotDto1.getGg()) < 0
                            && stockPivotDto.getDd().compareTo(stockPivotDto1.getDd()) < 0) {
                        stockPivotDto1.setTrend(1);
                    } else {
                        stockPivotDto1.setTrend(0);
                    }
                }
            }
        }
    }

    private List<StockBiPivotDto> getPivot(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables) {
        List<StockBiPivotDto> stockPivotDtos = new ArrayList<>();
        int i = 0;
        while (i < stockBiDtos.size()) {
            StockBiDto stockBiDto = stockBiDtos.get(i);
            Integer type = stockBiDto.getType();
            if (i < stockBiDtos.size() - 3) {
                StockBiDto stockBiDto1 = stockBiDtos.get(i + 1);
                StockBiDto stockBiDto3 = stockBiDtos.get(i + 3);

                if (type == FenxingType.TOP_PART.getValue()) {
                    if (stockBiDto3.getEndPrice().compareTo(stockBiDto1.getStartPrice()) <= 0) {
                        StockBiPivotDto stockPivotDto = buildNormalStockBiPivotDto(new ArrayList<>(stockBiDtos.subList(i, i + 4)), stockContainsTables, type);
                        int i_j = 1;
                        while (i + i_j < stockBiDtos.size() - 3 && stockPivotDto.getFinished() == 0) {
                            pivotGrow(stockPivotDto, stockBiDtos.get(i + i_j + 3), stockContainsTables);
                            i_j++;
                        }
                        if (stockPivotDto.getFinished() != 0.0) {
                            stockPivotDto.setType(stockPivotDto.getEnterBiDto().getType() + "_" + stockPivotDto.getLeaveBiDto().getType());
                        }
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    } else {
                        StockBiPivotDto stockPivotDto = buildSimilarStockBiPivotDto(new ArrayList<>(stockBiDtos.subList(i, i + 3)), stockContainsTables, type);
                        stockPivotDto.setType(stockPivotDto.getEnterBiDto().getType() + "_" + stockPivotDto.getLeaveBiDto().getType());
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    }
                } else {
                    if (stockBiDto3.getEndPrice().compareTo(stockBiDto1.getStartPrice()) >= 0) {
                        StockBiPivotDto stockPivotDto = buildNormalStockBiPivotDto(new ArrayList<>(stockBiDtos.subList(i, i + 4)), stockContainsTables, type);
                        int i_j = 1;
                        while (i + i_j < stockBiDtos.size() - 3 && stockPivotDto.getFinished() == 0) {
                            pivotGrow(stockPivotDto, stockBiDtos.get(i + i_j + 3), stockContainsTables);
                            i_j++;
                        }
                        if (stockPivotDto.getFinished() != 0.0) {
                            stockPivotDto.setType(stockPivotDto.getEnterBiDto().getType() + "_" + stockPivotDto.getLeaveBiDto().getType());
                        }
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    } else {
                        StockBiPivotDto stockPivotDto = buildSimilarStockBiPivotDto(new ArrayList<>(stockBiDtos.subList(i, i + 3)), stockContainsTables, type);
                        stockPivotDto.setType(stockPivotDto.getEnterBiDto().getType() + "_" + stockPivotDto.getLeaveBiDto().getType());
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    }
                }
            } else {
                i++;
            }
        }
        return stockPivotDtos;
    }

    private StockBiPivotDto buildNormalStockBiPivotDto(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables, Integer type) {
        StockBiPivotDto stockPivotDto = new StockBiPivotDto();
        stockPivotDto.setPivotType("normal");
        StockBiDto stockBiDto0 = stockBiDtos.get(0);
        StockBiDto stockBiDto1 = stockBiDtos.get(1);
        StockBiDto stockBiDto2 = stockBiDtos.get(2);
        StockBiDto stockBiDto3 = stockBiDtos.get(3);
        stockPivotDto.setTrend(-2);
        stockPivotDto.setLevel(1);
        stockPivotDto.setEnterBiDto(stockBiDto0);
        if (type.equals(FenxingType.TOP_PART.getValue())) {
            if (stockBiDto3.getEndPrice().compareTo(stockBiDto1.getStartPrice()) <= 0) {
                stockPivotDto.setZg(stockBiDto1.getStartPrice().min(stockBiDto3.getStartPrice()));
                stockPivotDto.setZd(stockBiDto1.getEndPrice().max(stockBiDto3.getEndPrice()));
                stockPivotDto.setDd(stockBiDto2.getStartPrice());
                stockPivotDto.setGg(stockBiDto1.getStartPrice().max(stockBiDto2.getEndPrice()));
            }
        } else {
            if (stockBiDto3.getEndPrice().compareTo(stockBiDto1.getStartPrice()) >= 0) {
                stockPivotDto.setZg(stockBiDto1.getEndPrice().min(stockBiDto3.getEndPrice()));
                stockPivotDto.setZd(stockBiDto1.getStartPrice().max(stockBiDto3.getStartPrice()));
                stockPivotDto.setDd(stockBiDto1.getStartPrice().min(stockBiDto2.getEndPrice()));
                stockPivotDto.setGg(stockBiDto2.getStartPrice());
            }
        }
        stockPivotDto.setStartBiDto(stockBiDto1);
        stockPivotDto.setEndBiDto(stockBiDto2);
        stockPivotDto.setLeaveBiDto(stockBiDto3);
        stockPivotDto.setStartTime(stockBiDto1.getStartTime());
        stockPivotDto.setStartPrice(stockBiDto1.getStartPrice());
        stockPivotDto.setEndTime(stockBiDto2.getEndTime());
        stockPivotDto.setEndPrice(stockBiDto2.getEndPrice());
        stockPivotDto.setFinished(0d);
        stockPivotDto.setEnterForce(BiForce(stockBiDto0));
        stockPivotDto.setLeaveForce(BiForce(stockBiDto3));
        stockPivotDto.setSize(3);
        stockPivotDto.setStockBiDtos(new ArrayList<>(stockBiDtos.subList(0, 3)));
        stockPivotDto.setMean(_05.multiply(stockPivotDto.getZg().add(stockPivotDto.getZd())));
        stockPivotDto.setPrev2Force(BiForce(stockBiDto1));
        stockPivotDto.setPrev2EndPrice(stockBiDto1.getEndPrice());
        stockPivotDto.setPrev1Force(BiForce(stockBiDto2));
        return stockPivotDto;
    }

    private StockBiPivotDto buildSimilarStockBiPivotDto(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables, Integer type) {
        StockBiPivotDto stockPivotDto = new StockBiPivotDto();
        stockPivotDto.setPivotType("similar");
        StockBiDto stockBiDto0 = stockBiDtos.get(0);
        StockBiDto stockBiDto1 = stockBiDtos.get(1);
        StockBiDto stockBiDto2 = stockBiDtos.get(2);
        stockPivotDto.setTrend(-2);
        stockPivotDto.setLevel(1);
        stockPivotDto.setEnterBiDto(stockBiDto0);
        if (type.equals(FenxingType.TOP_PART.getValue())) {
            stockPivotDto.setZg(stockBiDto1.getStartPrice());
            stockPivotDto.setZd(stockBiDto1.getEndPrice());
            stockPivotDto.setDd(stockBiDto1.getEndPrice());
            stockPivotDto.setGg(stockBiDto1.getStartPrice());
        } else {
            stockPivotDto.setZg(stockBiDto1.getEndPrice());
            stockPivotDto.setZd(stockBiDto1.getStartPrice());
            stockPivotDto.setDd(stockBiDto1.getStartPrice());
            stockPivotDto.setGg(stockBiDto1.getEndPrice());
        }
        stockPivotDto.setStartBiDto(stockBiDto1);
        stockPivotDto.setEndBiDto(stockBiDto2);
        stockPivotDto.setLeaveBiDto(stockBiDto2);
        stockPivotDto.setStartTime(stockBiDto1.getStartTime());
        stockPivotDto.setStartPrice(stockBiDto1.getStartPrice());
        stockPivotDto.setEndTime(stockBiDto2.getEndTime());
        stockPivotDto.setEndPrice(stockBiDto2.getEndPrice());
        stockPivotDto.setFinished(1d);
        stockPivotDto.setEnterForce(BiForce(stockBiDto0));
        stockPivotDto.setLeaveForce(BiForce(stockBiDto2));
        stockPivotDto.setSize(2);
        stockPivotDto.setStockBiDtos(new ArrayList<>(stockBiDtos.subList(0, 2)));
        stockPivotDto.setMean(_05.multiply(stockPivotDto.getZg().add(stockPivotDto.getZd())));
        stockPivotDto.setPrev2Force(BiForce(stockBiDto1));
        stockPivotDto.setPrev2EndPrice(stockBiDto1.getEndPrice());
        stockPivotDto.setPrev1Force(BiForce(stockBiDto2));
        return stockPivotDto;
    }

    private void pivotGrow(StockBiPivotDto stockPivotDto, StockBiDto stockBiDto, List<StockContainsTable> stockContainsTables) {
        stockPivotDto.setPrev2Force(stockPivotDto.getPrev1Force());
        stockPivotDto.setPrev2EndPrice(stockPivotDto.getLeaveBiDto().getStartPrice());
        stockPivotDto.setPrev1Force(stockPivotDto.getLeaveForce());
        if (stockBiDto.getType() == FenxingType.TOP_PART.getValue()) {
            if (stockBiDto.getEndPrice().compareTo(stockPivotDto.getZd()) >= 0
                    && stockBiDto.getStartPrice().compareTo(stockPivotDto.getZg()) <= 0
                    && stockPivotDto.getSize() <= 28) {
                stockPivotDto.setEndPrice(stockBiDto.getStartPrice());
                stockPivotDto.setEndTime(stockBiDto.getStartTime());
                stockPivotDto.setEndBiDto(stockPivotDto.getLeaveBiDto());
                stockPivotDto.getStockBiDtos().add(stockPivotDto.getLeaveBiDto());

                stockPivotDto.setSize(stockPivotDto.getSize() + 1);
                stockPivotDto.setDd(stockPivotDto.getDd().min(stockBiDto.getStartPrice()));

                stockPivotDto.setLeaveBiDto(stockBiDto);
                stockPivotDto.setLeaveForce(BiForce(stockBiDto));

                if (pivotSizeList.contains(stockPivotDto.getSize())) {
                    stockPivotDto.setFuture_zd(stockPivotDto.getFuture_zd() == null ? stockPivotDto.getDd() : stockPivotDto.getFuture_zd().max(stockPivotDto.getDd()));
                    stockPivotDto.setFuture_zg(stockPivotDto.getFuture_zg() == null ? stockPivotDto.getGg() : stockPivotDto.getFuture_zg().min(stockPivotDto.getGg()));
                }
                if (pivotLevelSizeList.contains(stockPivotDto.getSize())) {
                    stockPivotDto.setLevel(stockPivotDto.getLevel() + 1);
                    stockPivotDto.setZd(stockPivotDto.getFuture_zd());
                    stockPivotDto.setZg(stockPivotDto.getFuture_zg());
                    stockPivotDto.setFuture_zd(null);
                    stockPivotDto.setFuture_zg(null);
                }
            } else {
                if (stockBiDto.getEndPrice().compareTo(stockPivotDto.getZd()) >= 0 && stockBiDto.getStartPrice().compareTo(stockPivotDto.getZg()) <= 0) {
                    stockPivotDto.setDd(stockPivotDto.getDd().min(stockBiDto.getStartPrice()));
                    stockPivotDto.setFinished(0.5);
                } else {
                    stockPivotDto.setFinished(1.0);
                }
                stockPivotDto.setAft_l_price(stockBiDto.getEndPrice());
                stockPivotDto.setAft_l_time(stockBiDto.getEndTime());
            }
        } else {
            if (stockBiDto.getEndPrice().compareTo(stockPivotDto.getZg()) <= 0
                    && stockBiDto.getStartPrice().compareTo(stockPivotDto.getZd()) >= 0
                    && stockPivotDto.getSize() <= 28) {
                stockPivotDto.setEndPrice(stockBiDto.getStartPrice());
                stockPivotDto.setEndTime(stockBiDto.getStartTime());
                stockPivotDto.setEndBiDto(stockPivotDto.getLeaveBiDto());
                stockPivotDto.getStockBiDtos().add(stockPivotDto.getLeaveBiDto());

                stockPivotDto.setSize(stockPivotDto.getSize() + 1);
                stockPivotDto.setGg(stockPivotDto.getGg().max(stockBiDto.getStartPrice()));

                stockPivotDto.setLeaveBiDto(stockBiDto);
                stockPivotDto.setLeaveForce(BiForce(stockBiDto));

                if (pivotSizeList.contains(stockPivotDto.getSize())) {
                    stockPivotDto.setFuture_zd(stockPivotDto.getFuture_zd() == null ? stockPivotDto.getDd() : stockPivotDto.getFuture_zd().max(stockPivotDto.getDd()));
                    stockPivotDto.setFuture_zg(stockPivotDto.getFuture_zg() == null ? stockPivotDto.getGg() : stockPivotDto.getFuture_zg().min(stockPivotDto.getGg()));
                }
                if (pivotLevelSizeList.contains(stockPivotDto.getSize())) {
                    stockPivotDto.setLevel(stockPivotDto.getLevel() + 1);
                    stockPivotDto.setZd(stockPivotDto.getFuture_zd());
                    stockPivotDto.setZg(stockPivotDto.getFuture_zg());
                    stockPivotDto.setFuture_zd(null);
                    stockPivotDto.setFuture_zg(null);
                }
            } else {
                if (stockBiDto.getEndPrice().compareTo(stockPivotDto.getZg()) <= 0
                        && stockBiDto.getStartPrice().compareTo(stockPivotDto.getZd()) >= 0) {
                    stockPivotDto.setGg(stockPivotDto.getGg().max(stockBiDto.getStartPrice()));
                    stockPivotDto.setFinished(0.5);
                } else {
                    stockPivotDto.setFinished(1.0);
                }
                stockPivotDto.setAft_l_price(stockBiDto.getEndPrice());
                stockPivotDto.setAft_l_time(stockBiDto.getEndTime());
            }
        }
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

    private Date getEndTime(Optional<BigDecimal> price, List<StockContainsTable> stockContainsTables, String type) {
        for (StockContainsTable stockContainsTable : stockContainsTables) {
            if (type.equals("low")) {
                if (price.get().compareTo(stockContainsTable.getLow()) == 0) {
                    return stockContainsTable.getDt();
                }
            }
            if (type.equals("high")) {
                if (price.get().compareTo(stockContainsTable.getHigh()) == 0) {
                    return stockContainsTable.getDt();
                }
            }
        }
        return null;
    }

    public BigDecimal BiForce(StockBiDto stockBiDto) {
        try {
            BigDecimal endPrice = stockBiDto.getEndPrice();
            BigDecimal startPrice = stockBiDto.getStartPrice();
            int count = stockBiDto.getStockContainsTables().size();
            BigDecimal abs = (endPrice.divide(startPrice, 6, BigDecimal.ROUND_HALF_UP).subtract(_1)).abs();
            return abs.divide(new BigDecimal(count), 6, BigDecimal.ROUND_HALF_UP).multiply(_1000);
        } catch (Exception e) {
            log.error("Bi force error. stockBiDto: {}", stockBiDto.toString());
        }
        return null;
    }
}
