package com.czsc.task;

import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockSegPivotDto;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.StockContainsTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StockSegPivotTableTask {
    private static final BigDecimal _1 = new BigDecimal("1");
    private static final BigDecimal _05 = new BigDecimal("0.5");
    private static final BigDecimal _1000 = new BigDecimal("1000");
    private static final List<Integer> pivotSizeList = Arrays.asList(4, 7, 10, 19, 28);
    private static final List<Integer> pivotLevelSizeList = Arrays.asList(10, 28);

    public List<StockSegPivotDto> dealData(List<StockSegDto> stockSegDtos, List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables) {
//        List<StockSegDto> stockSegDtos = stockSegTableService.getStockSegDtosBySymbol("000001.XSHG");
        if (CollectionUtils.isEmpty(stockSegDtos)) {
            return null;
        }
        dealLastSegDto(stockSegDtos, stockBiDtos, stockContainsTables);
        List<StockSegPivotDto> stockPivotDtos = getPivot(stockSegDtos, stockContainsTables);
        processPivot(stockPivotDtos);
        return stockPivotDtos;
    }

    private void processPivot(List<StockSegPivotDto> stockPivotDtos) {
        for (int i = 0; i < stockPivotDtos.size() - 1; i++) {
            StockSegPivotDto stockPivotDto = stockPivotDtos.get(i);
            StockSegPivotDto stockPivotDto1 = stockPivotDtos.get(i + 1);
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

    private List<StockSegPivotDto> getPivot(List<StockSegDto> stockSegDtos, List<StockContainsTable> stockContainsTables) {
        List<StockSegPivotDto> stockPivotDtos = new ArrayList<>();
        int i = 0;
        while (i < stockSegDtos.size()) {
            StockSegDto stockSegDto = stockSegDtos.get(i);
            Integer type = stockSegDto.getType();
            if (i < stockSegDtos.size() - 3) {
                StockSegDto stockSegDto1 = stockSegDtos.get(i + 1);
                StockSegDto stockSegDto3 = stockSegDtos.get(i + 3);

                if (type == FenxingType.TOP_PART.getValue()) {
                    if (stockSegDto3.getEndPrice().compareTo(stockSegDto1.getStartPrice()) <= 0) {
                        StockSegPivotDto stockPivotDto = buildNormalStockSegPivotDto(new ArrayList<>(stockSegDtos.subList(i, i + 4)), stockContainsTables, type);
                        int i_j = 1;
                        while (i + i_j < stockSegDtos.size() - 3 && stockPivotDto.getFinished() == 0) {
                            pivotGrow(stockPivotDto, stockSegDtos.get(i + i_j + 3), stockContainsTables);
                            i_j++;
                        }
                        if (stockPivotDto.getFinished() != 0.0) {
                            stockPivotDto.setType(stockPivotDto.getEnterSegDto().getType() + "_" + stockPivotDto.getLeaveSegDto().getType());
                        }
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    } else {
                        StockSegPivotDto stockPivotDto = buildSimilarStockSegPivotDto(new ArrayList<>(stockSegDtos.subList(i, i + 3)), stockContainsTables, type);
                        stockPivotDto.setType(stockPivotDto.getEnterSegDto().getType() + "_" + stockPivotDto.getLeaveSegDto().getType());
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    }
                } else {
                    if (stockSegDto3.getEndPrice().compareTo(stockSegDto1.getStartPrice()) >= 0) {
                        StockSegPivotDto stockPivotDto = buildNormalStockSegPivotDto(new ArrayList<>(stockSegDtos.subList(i, i + 4)), stockContainsTables, type);
                        int i_j = 1;
                        while (i + i_j < stockSegDtos.size() - 3 && stockPivotDto.getFinished() == 0) {
                            pivotGrow(stockPivotDto, stockSegDtos.get(i + i_j + 3), stockContainsTables);
                            i_j++;
                        }
                        if (stockPivotDto.getFinished() != 0.0) {
                            stockPivotDto.setType(stockPivotDto.getEnterSegDto().getType() + "_" + stockPivotDto.getLeaveSegDto().getType());
                        }
                        i += stockPivotDto.getSize();
                        stockPivotDtos.add(stockPivotDto);
                        continue;
                    } else {
                        StockSegPivotDto stockPivotDto = buildSimilarStockSegPivotDto(new ArrayList<>(stockSegDtos.subList(i, i + 3)), stockContainsTables, type);
                        stockPivotDto.setType(stockPivotDto.getEnterSegDto().getType() + "_" + stockPivotDto.getLeaveSegDto().getType());
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

    private StockSegPivotDto buildNormalStockSegPivotDto(List<StockSegDto> stockSegDtos, List<StockContainsTable> stockContainsTables, Integer type) {
        StockSegPivotDto stockPivotDto = new StockSegPivotDto();
        stockPivotDto.setPivotType("normal");
        StockSegDto stockSegDto0 = stockSegDtos.get(0);
        StockSegDto stockSegDto1 = stockSegDtos.get(1);
        StockSegDto stockSegDto2 = stockSegDtos.get(2);
        StockSegDto stockSegDto3 = stockSegDtos.get(3);
        stockPivotDto.setTrend(-2);
        stockPivotDto.setLevel(1);
        stockPivotDto.setEnterSegDto(stockSegDto0);
        if (type.equals(FenxingType.TOP_PART.getValue())) {
            if (stockSegDto3.getEndPrice().compareTo(stockSegDto1.getStartPrice()) <= 0) {
                stockPivotDto.setZg(stockSegDto1.getStartPrice().min(stockSegDto3.getStartPrice()));
                stockPivotDto.setZd(stockSegDto1.getEndPrice().max(stockSegDto3.getEndPrice()));
                stockPivotDto.setDd(stockSegDto2.getStartPrice());
                stockPivotDto.setGg(stockSegDto1.getStartPrice().max(stockSegDto2.getEndPrice()));
            }
        } else {
            if (stockSegDto3.getEndPrice().compareTo(stockSegDto1.getStartPrice()) >= 0) {
                stockPivotDto.setZg(stockSegDto1.getEndPrice().min(stockSegDto3.getEndPrice()));
                stockPivotDto.setZd(stockSegDto1.getStartPrice().max(stockSegDto3.getStartPrice()));
                stockPivotDto.setDd(stockSegDto1.getStartPrice().min(stockSegDto2.getEndPrice()));
                stockPivotDto.setGg(stockSegDto2.getStartPrice());
            }
        }
        stockPivotDto.setStartSegDto(stockSegDto1);
        stockPivotDto.setEndSegDto(stockSegDto2);
        stockPivotDto.setLeaveSegDto(stockSegDto3);
        stockPivotDto.setStartTime(stockSegDto1.getStartTime());
        stockPivotDto.setStartPrice(stockSegDto1.getStartPrice());
        stockPivotDto.setEndTime(stockSegDto2.getEndTime());
        stockPivotDto.setEndPrice(stockSegDto2.getEndPrice());
        stockPivotDto.setFinished(0d);
        stockPivotDto.setEnterForce(segForce(stockSegDto0, stockContainsTables));
        stockPivotDto.setLeaveForce(segForce(stockSegDto3, stockContainsTables));
        stockPivotDto.setSize(3);
        stockPivotDto.setStockSegDtos(new ArrayList<>(stockSegDtos.subList(0, 3)));
        stockPivotDto.setMean(_05.multiply(stockPivotDto.getZg().add(stockPivotDto.getZd())));
        stockPivotDto.setPrev2Force(segForce(stockSegDto1, stockContainsTables));
        stockPivotDto.setPrev2EndPrice(stockSegDto1.getEndPrice());
        stockPivotDto.setPrev1Force(segForce(stockSegDto2, stockContainsTables));
        return stockPivotDto;
    }

    private StockSegPivotDto buildSimilarStockSegPivotDto(List<StockSegDto> stockSegDtos, List<StockContainsTable> stockContainsTables, Integer type) {
        StockSegPivotDto stockPivotDto = new StockSegPivotDto();
        stockPivotDto.setPivotType("similar");
        StockSegDto stockSegDto0 = stockSegDtos.get(0);
        StockSegDto stockSegDto1 = stockSegDtos.get(1);
        StockSegDto stockSegDto2 = stockSegDtos.get(2);
        stockPivotDto.setTrend(-2);
        stockPivotDto.setLevel(1);
        stockPivotDto.setEnterSegDto(stockSegDto0);
        if (type.equals(FenxingType.TOP_PART.getValue())) {
            stockPivotDto.setZg(stockSegDto1.getStartPrice());
            stockPivotDto.setZd(stockSegDto1.getEndPrice());
            stockPivotDto.setDd(stockSegDto1.getEndPrice());
            stockPivotDto.setGg(stockSegDto1.getStartPrice());
        } else {
            stockPivotDto.setZg(stockSegDto1.getEndPrice());
            stockPivotDto.setZd(stockSegDto1.getStartPrice());
            stockPivotDto.setDd(stockSegDto1.getStartPrice());
            stockPivotDto.setGg(stockSegDto1.getEndPrice());
        }
        stockPivotDto.setStartSegDto(stockSegDto1);
        stockPivotDto.setEndSegDto(stockSegDto2);
        stockPivotDto.setLeaveSegDto(stockSegDto2);
        stockPivotDto.setStartTime(stockSegDto1.getStartTime());
        stockPivotDto.setStartPrice(stockSegDto1.getStartPrice());
        stockPivotDto.setEndTime(stockSegDto2.getEndTime());
        stockPivotDto.setEndPrice(stockSegDto2.getEndPrice());
        stockPivotDto.setFinished(1d);
        stockPivotDto.setEnterForce(segForce(stockSegDto0, stockContainsTables));
        stockPivotDto.setLeaveForce(segForce(stockSegDto2, stockContainsTables));
        stockPivotDto.setSize(2);
        stockPivotDto.setStockSegDtos(new ArrayList<>(stockSegDtos.subList(0, 2)));
        stockPivotDto.setMean(_05.multiply(stockPivotDto.getZg().add(stockPivotDto.getZd())));
        stockPivotDto.setPrev2Force(segForce(stockSegDto1, stockContainsTables));
        stockPivotDto.setPrev2EndPrice(stockSegDto1.getEndPrice());
        stockPivotDto.setPrev1Force(segForce(stockSegDto2, stockContainsTables));
        return stockPivotDto;
    }

    private void pivotGrow(StockSegPivotDto stockPivotDto, StockSegDto stockSegDto, List<StockContainsTable> stockContainsTables) {
        stockPivotDto.setPrev2Force(stockPivotDto.getPrev1Force());
        stockPivotDto.setPrev2EndPrice(stockPivotDto.getLeaveSegDto().getStartPrice());
        stockPivotDto.setPrev1Force(stockPivotDto.getLeaveForce());
        if (stockSegDto.getType() == FenxingType.TOP_PART.getValue()) {
            if (stockSegDto.getEndPrice().compareTo(stockPivotDto.getZd()) >= 0
                    && stockSegDto.getStartPrice().compareTo(stockPivotDto.getZg()) <= 0
                    && stockPivotDto.getSize() <= 28) {
                stockPivotDto.setEndPrice(stockSegDto.getStartPrice());
                stockPivotDto.setEndTime(stockSegDto.getStartTime());
                stockPivotDto.setEndSegDto(stockPivotDto.getLeaveSegDto());
                stockPivotDto.getStockSegDtos().add(stockPivotDto.getLeaveSegDto());

                stockPivotDto.setSize(stockPivotDto.getSize() + 1);
                stockPivotDto.setDd(stockPivotDto.getDd().min(stockSegDto.getStartPrice()));

                stockPivotDto.setLeaveSegDto(stockSegDto);
                stockPivotDto.setLeaveForce(segForce(stockSegDto, stockContainsTables));

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
                if (stockSegDto.getEndPrice().compareTo(stockPivotDto.getZd()) >= 0 && stockSegDto.getStartPrice().compareTo(stockPivotDto.getZg()) <= 0) {
                    stockPivotDto.setDd(stockPivotDto.getDd().min(stockSegDto.getStartPrice()));
                    stockPivotDto.setFinished(0.5);
                } else {
                    stockPivotDto.setFinished(1.0);
                }
                stockPivotDto.setAft_l_price(stockSegDto.getEndPrice());
                stockPivotDto.setAft_l_time(stockSegDto.getEndTime());
            }
        } else {
            if (stockSegDto.getEndPrice().compareTo(stockPivotDto.getZg()) <= 0
                    && stockSegDto.getStartPrice().compareTo(stockPivotDto.getZd()) >= 0
                    && stockPivotDto.getSize() <= 28) {
                stockPivotDto.setEndPrice(stockSegDto.getStartPrice());
                stockPivotDto.setEndTime(stockSegDto.getStartTime());
                stockPivotDto.setEndSegDto(stockPivotDto.getLeaveSegDto());
                stockPivotDto.getStockSegDtos().add(stockPivotDto.getLeaveSegDto());

                stockPivotDto.setSize(stockPivotDto.getSize() + 1);
                stockPivotDto.setGg(stockPivotDto.getGg().max(stockSegDto.getStartPrice()));

                stockPivotDto.setLeaveSegDto(stockSegDto);
                stockPivotDto.setLeaveForce(segForce(stockSegDto, stockContainsTables));

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
                if (stockSegDto.getEndPrice().compareTo(stockPivotDto.getZg()) <= 0
                        && stockSegDto.getStartPrice().compareTo(stockPivotDto.getZd()) >= 0) {
                    stockPivotDto.setGg(stockPivotDto.getGg().max(stockSegDto.getStartPrice()));
                    stockPivotDto.setFinished(0.5);
                } else {
                    stockPivotDto.setFinished(1.0);
                }
                stockPivotDto.setAft_l_price(stockSegDto.getEndPrice());
                stockPivotDto.setAft_l_time(stockSegDto.getEndTime());
            }
        }
    }

    private void dealLastSegDto(List<StockSegDto> stockSegDtos, List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables) {
        StockSegDto stockSegDto1 = stockSegDtos.get(stockSegDtos.size() - 1);
        if (CollectionUtils.isEmpty(stockContainsTables)) {
            return;
        }
        long endTime = stockSegDto1.getEndTime().getTime();
        List<StockBiDto> biDtos = stockBiDtos.stream().filter(stockBiDto -> stockBiDto.getStartTime().getTime() >= endTime).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(biDtos)) {
            return;
        }
        if (biDtos.size() == 2) {
            StockBiDto stockBiDto = biDtos.get(1);
            Integer type = stockSegDto1.getType();
            if (type.equals(FenxingType.BOTTOM_PART.getValue()) && stockBiDto.getEndPrice().compareTo(stockSegDto1.getEndPrice()) <= 0) {
                stockSegDto1.setEndPrice(stockBiDto.getEndPrice());
                stockSegDto1.setEndTime(stockBiDto.getEndTime());
                List<StockBiDto> stockBiDtoList = stockSegDto1.getStockBiDtos();
                stockBiDtoList.addAll(biDtos);
                stockSegDto1.setStockBiDtos(stockBiDtoList);
                segForce(stockSegDto1, stockContainsTables);
            } else if (type.equals(FenxingType.TOP_PART.getValue()) && stockBiDto.getEndPrice().compareTo(stockSegDto1.getEndPrice()) >= 0) {
                stockSegDto1.setEndPrice(stockBiDto.getEndPrice());
                stockSegDto1.setEndTime(stockBiDto.getEndTime());
                List<StockBiDto> stockBiDtoList = stockSegDto1.getStockBiDtos();
                stockBiDtoList.addAll(biDtos);
                stockSegDto1.setStockBiDtos(stockBiDtoList);
                segForce(stockSegDto1, stockContainsTables);
            }
            return;
        }
        if (biDtos.size() >= 3) {
            if (checkNewSeg(stockSegDto1, biDtos)) {
                StockSegDto stockSegDto = new StockSegDto();
                StockBiDto startBiDto = biDtos.get(0);
                stockSegDto.setType(startBiDto.getType());
                stockSegDto.setStartTime(startBiDto.getStartTime());
                stockSegDto.setStartPrice(startBiDto.getStartPrice());

                StockBiDto endBiDto = biDtos.get(biDtos.size() - 1);
                stockSegDto.setEndTime(endBiDto.getEndTime());
                stockSegDto.setEndPrice(endBiDto.getEndPrice());
                stockSegDto.setStockBiDtos(biDtos);
                stockSegDto.setForce(segForce(stockSegDto, stockContainsTables));
                stockSegDtos.add(stockSegDto);
            }
        }
//        List<StockContainsTable> tables =
//                stockContainsTables.stream().filter(stockContainsTable -> stockContainsTable.getDt().getTime() >= endTime).collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(tables)) {
//            return;
//        }
//        List<BigDecimal> lowList = tables.stream().map(StockContainsTable::getLow).collect(Collectors.toList());
//        List<BigDecimal> highList = tables.stream().map(StockContainsTable::getHigh).collect(Collectors.toList());
//        Optional<BigDecimal> low_extre = lowList.stream().min(BigDecimal::compareTo);
//        Optional<BigDecimal> high_extre = highList.stream().max(BigDecimal::compareTo);
//        Date highTime = getEndTime(high_extre, tables, "high");
//        Date lowTime = getEndTime(low_extre, tables, "low");
//        if (stockSegDto1.getEndFlag() == EndFlagType.FINISHED.getValue()) {
//            StockSegDto stockSegDto = new StockSegDto();
//            stockSegDto.setStartPrice(stockSegDto1.getEndPrice());
//            stockSegDto.setStartTime(stockSegDto1.getEndTime());
//            stockSegDto.setSymbol(stockSegDto1.getSymbol());
//            if (stockSegDto1.getType() == FenxingType.TOP_PART.getValue()) {
//                stockSegDto.setEndTime(lowTime);
//                stockSegDto.setEndPrice(low_extre.get());
//                setStockBiDtos(stockSegDto, stockBiDtos);
//                stockSegDto.setType(FenxingType.BOTTOM_PART.getValue());
//            } else {
//                stockSegDto.setEndTime(highTime);
//                stockSegDto.setEndPrice(high_extre.get());
//                setStockBiDtos(stockSegDto, stockBiDtos);
//                stockSegDto.setType(FenxingType.TOP_PART.getValue());
//            }
//            stockSegDtos.add(stockSegDto);
//        } else {
//            if (stockSegDto1.getType() == FenxingType.TOP_PART.getValue()) {
//                if (low_extre.get().compareTo(stockSegDto1.getEndPrice()) > 0) {
//                    stockSegDto1.setEndPrice(high_extre.get());
//                    stockSegDto1.setEndTime(highTime);
//                    setStockBiDtos(stockSegDto1, stockBiDtos);
//                } else {
//                    if (getCountByStartTimeAndEndTime(stockContainsTables, stockSegDto1.getEndTime(), lowTime) >= 10) {
//                        StockSegDto stockSegDto = new StockSegDto();
//                        stockSegDto.setStartPrice(stockSegDto1.getEndPrice());
//                        stockSegDto.setStartTime(stockSegDto1.getEndTime());
//                        stockSegDto.setSymbol(stockSegDto1.getSymbol());
//                        stockSegDto.setEndTime(lowTime);
//                        stockSegDto.setEndPrice(low_extre.get());
//                        stockSegDto.setType(FenxingType.BOTTOM_PART.getValue());
//                        setStockBiDtos(stockSegDto, stockBiDtos);
//                        stockSegDtos.add(stockSegDto);
//                    }
//                }
//            } else {
//                if (high_extre.get().compareTo(stockSegDto1.getStartPrice()) < 0) {
//                    stockSegDto1.setEndPrice(low_extre.get());
//                    stockSegDto1.setEndTime(lowTime);
//                    setStockBiDtos(stockSegDto1, stockBiDtos);
//                } else {
//                    if (getCountByStartTimeAndEndTime(stockContainsTables, stockSegDto1.getEndTime(), highTime) >= 10) {
//                        StockSegDto stockSegDto = new StockSegDto();
//                        stockSegDto.setStartPrice(stockSegDto1.getEndPrice());
//                        stockSegDto.setStartTime(stockSegDto1.getEndTime());
//                        stockSegDto.setSymbol(stockSegDto1.getSymbol());
//                        stockSegDto.setEndTime(highTime);
//                        stockSegDto.setEndPrice(high_extre.get());
//                        setStockBiDtos(stockSegDto, stockBiDtos);
//                        stockSegDto.setType(FenxingType.TOP_PART.getValue());
//                        stockSegDtos.add(stockSegDto);
//                    }
//                }
//            }
//        }
    }

    private boolean checkNewSeg(StockSegDto stockSegDto, List<StockBiDto> stockBiDtos) {
        List<StockBiDto> preBiDtos = stockSegDto.getStockBiDtos();
        StockBiDto preBiDto = preBiDtos.get(preBiDtos.size() - 1);
        StockBiDto stockBiDto = stockBiDtos.get(1);
        if (!preBiDto.getType().equals(stockBiDto.getType())) {
            return false;
        }
        if (preBiDto.getType().equals(FenxingType.TOP_PART.getValue())) {
            if (preBiDto.getEndPrice().compareTo(stockBiDto.getEndPrice()) > 0) {
                return true;
            }
            return false;
        }
        if (preBiDto.getType().equals(FenxingType.BOTTOM_PART.getValue())) {
            if (preBiDto.getEndPrice().compareTo(stockBiDto.getEndPrice()) < 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void setStockBiDtos(StockSegDto stockSegDto, List<StockBiDto> stockBiDtos) {
        Date startTime = stockSegDto.getStartTime();
        Date endTime = stockSegDto.getEndTime();
        List<StockBiDto> biDtos = new ArrayList<>();
        for (StockBiDto stockBiDto : stockBiDtos) {
            Date time = stockBiDto.getStartTime();
            if (startTime.getTime() <= time.getTime() && endTime.getTime() > time.getTime()) {
                biDtos.add(stockBiDto);
            }
        }
        stockSegDto.setStockBiDtos(biDtos);
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

    private int getCount(StockSegDto stockSegDto, List<StockContainsTable> stockContainsTables) {
        int total = 0;
        List<StockBiDto> stockBiDtos = stockSegDto.getStockBiDtos();
        if (CollectionUtils.isEmpty(stockBiDtos)) {
            return getCountByStartTimeAndEndTime(stockContainsTables, stockSegDto.getStartTime(), stockSegDto.getEndTime());
        }
        for (StockBiDto stockBiDto : stockBiDtos) {
            total += stockBiDto.getStockContainsTables().size();
            total -= 1;
        }
        return total;
    }

    public BigDecimal segForce(StockSegDto stockSegDto, List<StockContainsTable> stockContainsTables) {
        try {
            BigDecimal endPrice = stockSegDto.getEndPrice();
            BigDecimal startPrice = stockSegDto.getStartPrice();
            int count = getCount(stockSegDto, stockContainsTables);
            BigDecimal abs = (endPrice.divide(startPrice, 6, BigDecimal.ROUND_HALF_UP).subtract(_1)).abs();
            return abs.divide(new BigDecimal(count), 6, BigDecimal.ROUND_HALF_UP).multiply(_1000);
        } catch (Exception e) {
            log.error("seg force error. stockSegDto: {}", stockSegDto.toString());
        }
        return null;
    }
}
