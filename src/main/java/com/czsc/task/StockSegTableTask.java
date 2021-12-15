package com.czsc.task;

import com.czsc.common.EndFlagType;
import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockDto;
import com.czsc.dto.StockSegDto;
import com.czsc.entity.StockContainsTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class StockSegTableTask {
    private static final BigDecimal _1 = new BigDecimal("1");
    private static final BigDecimal _1000 = new BigDecimal("1000");

    public List<StockSegDto> dealData(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables, List<StockDto> stockTables) {
//        List<StockBiDto> stockBiDtos = stockBiTableService.getStockBiDtoBySymbol("000001.XSHG");
        if (CollectionUtils.isEmpty(stockBiDtos)) {
            return null;
        }
        if (stockBiDtos.size() < 5) {
            return null;
        }
        deaLastBiDto(stockBiDtos, stockContainsTables, stockTables);
        int start = 0;
        while (start < stockBiDtos.size() - 5) {
            if (checkInitSeg(new ArrayList<>(stockBiDtos.subList(start, start + 4)))) {
                break;
            }
            start++;
        }
        int i = start;
        boolean end = Boolean.FALSE;
        List<StockSegDto> stockSegDtos = new ArrayList<>();
        int index = start;
        while (i <= stockBiDtos.size() - 4) {
            StockSegDto stockSegDto = new StockSegDto(new ArrayList<>(stockBiDtos.subList(i, i + 4)));
            boolean label = Boolean.FALSE;
            while (label == Boolean.FALSE && i <= stockBiDtos.size() - 6) {
                i = i + 2;
                label = growSeg(stockSegDto, new ArrayList<>(stockBiDtos.subList(i + 2, i + 4)), stockContainsTables);
                List<StockBiDto> dtos = stockSegDto.getStockBiDtos();
                if (dtos.get(dtos.size() - 1).getStartTime().getTime() > stockBiDtos.get(stockBiDtos.size() - 3).getStartTime().getTime()) {
                    end = Boolean.TRUE;
                    stockSegDto.setFinished(Boolean.FALSE);
                    filterStockBiDto(stockSegDto);
                    stockSegDto.setEndFlag(getEndFlag(stockSegDto.getFinished()));
                    stockSegDto.setEndPrice(stockSegDto.getCurExtreme());
                    stockSegDto.setEndTime(stockSegDto.getCurExtremePos());
                    stockSegDto.setForce(segForce(stockSegDto, stockContainsTables));
                    stockSegDtos.add(stockSegDto);
                    break;
                }
            }
            if (end) {
                break;
            }
            index += stockSegDto.getStockBiDtos().size();
            i = index;
            stockSegDtos.add(stockSegDto);
        }
        if (CollectionUtils.isEmpty(stockSegDtos)) {
            return null;
        }
        StockSegDto stockSegDto = stockSegDtos.get(stockSegDtos.size() - 1);
        if (stockSegDto.getFinished() == Boolean.FALSE) {
            stockSegDto.setEndFlag(getEndFlag(stockSegDto.getFinished()));
            filterStockBiDto(stockSegDto);
            stockSegDto.setEndPrice(stockSegDto.getCurExtreme());
            stockSegDto.setEndTime(stockSegDto.getCurExtremePos());
            stockSegDto.setForce(segForce(stockSegDto, stockContainsTables));
        }
//        List<StockSegTable> stockSegTables = new ArrayList<>();
//        for (StockSegDto stockSegDto : stockSegDtos) {
//            StockSegTable stockSegTable = new StockSegTable();
//            BeanUtils.copyProperties(stockSegDto, stockSegTable);
//            stockSegTable.setEndFlag(getEndFlag(stockSegDto.getFinished()));
//            stockSegTable.setUpdateTime(stockSegTable.getEndTime());
//            stockSegTables.add(stockSegTable);
//        }
//        stockSegTableService.saveBatch(stockSegTables);
        return stockSegDtos;
    }

    private void deaLastBiDto(List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables, List<StockDto> stockTables) {
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
            List<StockDto> tables =
                    getStockTableByStartTimeAndEndTime(stockTables, stockBiDto.getStartTime(), containsTableList.get(containsTableList.size() -1).getUpdateTime());
            stockBiDto.setStockTables(tables);
            stockBiDto.setForce(StockBiTableTask.biForce(stockBiDto));
        }
    }

    private List<StockDto> getStockTableByStartTimeAndEndTime(List<StockDto> stockTables, Date startTime, Date endTime) {
        List<StockDto> result = new ArrayList<>();
        if (endTime == null) {
            endTime = new Date();
        }
        for (StockDto stockTable : stockTables) {
            Date dt = stockTable.getDt();
            if (dt.getTime() >= startTime.getTime() && dt.getTime() <= endTime.getTime()) {
                result.add(stockTable);
            }
        }
        return result;
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

    private Integer getEndFlag(Boolean finished) {
        if (finished) {
            return EndFlagType.FINISHED.getValue();
        }
        return EndFlagType.UNFINISHED.getValue();
    }

    private boolean growSeg(StockSegDto stockSegDto, List<StockBiDto> stockBiDtos, List<StockContainsTable> stockContainsTables) {
        Integer type = stockSegDto.getType();
        StockBiDto lastStockBiDto1 = stockSegDto.getStockBiDtos().get(stockSegDto.getStockBiDtos().size() - 1);
        StockBiDto lastStockBiDto2 = stockSegDto.getStockBiDtos().get(stockSegDto.getStockBiDtos().size() - 2);
        StockBiDto lastStockBiDto3 = stockSegDto.getStockBiDtos().get(stockSegDto.getStockBiDtos().size() - 3);
        StockBiDto lastStockBiDto4 = stockSegDto.getStockBiDtos().get(stockSegDto.getStockBiDtos().size() - 4);
        if (type == FenxingType.TOP_PART.getValue()) {
            if (stockBiDtos.get(1).getStartPrice().compareTo(stockSegDto.getCurExtreme()) >= 0) {
                if (stockBiDtos.get(0).getStartPrice().compareTo(stockSegDto.getPrevExtreme()) > 0) {
                    stockSegDto.setGap(Boolean.TRUE);
                } else {
                    stockSegDto.setGap(Boolean.FALSE);
                }
                stockSegDto.setPrevExtreme(stockSegDto.getCurExtreme());
                stockSegDto.setCurExtreme(stockBiDtos.get(1).getStartPrice());
                stockSegDto.setCurExtremePos(stockBiDtos.get(1).getStartTime());
            } else {
                if ((stockSegDto.getGap() == Boolean.FALSE && stockBiDtos.get(1).getStockContainsTables().get(0).getLow().compareTo(lastStockBiDto1.getStockContainsTables().get(0).getLow()) < 0)
                        || ((stockSegDto.getGap() == Boolean.TRUE && lastStockBiDto1.getStartPrice().compareTo(lastStockBiDto3.getStartPrice()) < 0)
                        && (lastStockBiDto2.getStartPrice().compareTo(lastStockBiDto4.getStartPrice()) < 0))) {
                    stockSegDto.setFinished(Boolean.TRUE);
                    filterStockBiDto(stockSegDto);
                    stockSegDto.setEndFlag(getEndFlag(stockSegDto.getFinished()));
                    stockSegDto.setEndPrice(stockSegDto.getCurExtreme());
                    stockSegDto.setEndTime(stockSegDto.getCurExtremePos());
                    stockSegDto.setForce(segForce(stockSegDto, stockContainsTables));
                    return Boolean.TRUE;
                }
            }
            stockSegDto.getStockBiDtos().addAll(stockBiDtos);
            return Boolean.FALSE;
        } else {
            if (stockBiDtos.get(1).getStartPrice().compareTo(stockSegDto.getCurExtreme()) <= 0) {
                if (stockBiDtos.get(0).getStartPrice().compareTo(stockSegDto.getPrevExtreme()) < 0) {
                    stockSegDto.setGap(Boolean.TRUE);
                } else {
                    stockSegDto.setGap(Boolean.FALSE);
                }
                stockSegDto.setPrevExtreme(stockSegDto.getCurExtreme());
                stockSegDto.setCurExtreme(stockBiDtos.get(1).getStartPrice());
                stockSegDto.setCurExtremePos(stockBiDtos.get(1).getStartTime());
            } else {
                if ((stockSegDto.getGap() == Boolean.FALSE && stockBiDtos.get(1).getStockContainsTables().get(0).getHigh().compareTo(lastStockBiDto1.getStockContainsTables().get(0).getHigh()) > 0)
                        || (stockSegDto.getGap() == Boolean.TRUE && (lastStockBiDto1.getStartPrice().compareTo(lastStockBiDto3.getStartPrice()) > 0)
                        && lastStockBiDto2.getStartPrice().compareTo(lastStockBiDto4.getStartPrice()) > 0)) {
                    stockSegDto.setFinished(Boolean.TRUE);
                    filterStockBiDto(stockSegDto);
                    stockSegDto.setEndFlag(getEndFlag(stockSegDto.getFinished()));
                    stockSegDto.setEndPrice(stockSegDto.getCurExtreme());
                    stockSegDto.setEndTime(stockSegDto.getCurExtremePos());
                    stockSegDto.setForce(segForce(stockSegDto, stockContainsTables));
                    return Boolean.TRUE;
                }
            }
            stockSegDto.getStockBiDtos().addAll(stockBiDtos);
            return Boolean.FALSE;
        }
    }

    private void filterStockBiDto(StockSegDto stockSegDto) {
        long time = stockSegDto.getCurExtremePos().getTime();
        Iterator<StockBiDto> iterator = stockSegDto.getStockBiDtos().iterator();
        while (iterator.hasNext()) {
            StockBiDto stockBiDto = iterator.next();
            if (stockBiDto.getStartTime().getTime() >= time) {
                iterator.remove();
            }
        }
    }

    private boolean checkInitSeg(List<StockBiDto> stockBiDtos) {
        Integer d = stockBiDtos.get(0).getType();
        if ((d != FenxingType.TOP_PART.getValue() || d != FenxingType.BOTTOM_PART.getValue()) && stockBiDtos.size() != 4) {
            return false;
        }
        if (d == FenxingType.TOP_PART.getValue()) {
            if (stockBiDtos.get(1).getStartPrice().compareTo(stockBiDtos.get(3).getStartPrice()) < 0
                    && stockBiDtos.get(0).getStartPrice().compareTo(stockBiDtos.get(2).getStartPrice()) < 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } else {
            if (stockBiDtos.get(1).getStartPrice().compareTo(stockBiDtos.get(3).getStartPrice()) > 0
                    && stockBiDtos.get(0).getStartPrice().compareTo(stockBiDtos.get(2).getStartPrice()) > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
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
