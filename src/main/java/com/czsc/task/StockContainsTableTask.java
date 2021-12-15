package com.czsc.task;

import com.czsc.common.FenxingLevel;
import com.czsc.common.FenxingType;
import com.czsc.common.StockContainsType;
import com.czsc.dto.StockDto;
import com.czsc.entity.StockContainsTable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class StockContainsTableTask {
    public List<StockContainsTable> dealData(List<StockDto> stockTables) {
        if (CollectionUtils.isEmpty(stockTables)) {
            return null;
        }
        if (stockTables.size() < 100) {
            return null;
        }
        //使用他人方式处理合并问题
        List<StockContainsTable> stockContainsTables = doStockContainsTable(stockTables);

        StockContainsTable stockContainsTable = stockContainsTables.get(0);
        stockContainsTable.setFenxingType(FenxingType.NONE_PART.getValue());
        stockContainsTable.setFenxingPower(FenxingLevel.NONE.getValue());
        for (int i = 1; i < stockContainsTables.size() - 1; i++) {
            StockContainsTable preContains = stockContainsTables.get(i - 1);
            StockContainsTable containsTable = stockContainsTables.get(i);
            StockContainsTable nextContains = stockContainsTables.get(i + 1);

            if (containsTable.getHigh().compareTo(preContains.getHigh()) > 0
                    && containsTable.getHigh().compareTo(nextContains.getHigh()) > 0
                    && containsTable.getLow().compareTo(preContains.getLow()) > 0
                    && containsTable.getLow().compareTo(nextContains.getLow()) > 0) {
                //顶分型
                containsTable.setFenxingType(FenxingType.TOP_PART.getValue());
                setFenxingPower(Arrays.asList(preContains, containsTable, nextContains));
                continue;
            } else if (containsTable.getHigh().compareTo(preContains.getHigh()) < 0
                    && containsTable.getHigh().compareTo(nextContains.getHigh()) < 0
                    && containsTable.getLow().compareTo(preContains.getLow()) < 0
                    && containsTable.getLow().compareTo(nextContains.getLow()) < 0) {
                //底分型
                containsTable.setFenxingType(FenxingType.BOTTOM_PART.getValue());
                setFenxingPower(Arrays.asList(preContains, containsTable, nextContains));
                continue;
            }
            containsTable.setFenxingType(FenxingType.NONE_PART.getValue());
            containsTable.setFenxingPower(FenxingLevel.NONE.getValue());
        }
//        stockContainsTableService.saveBatch(stockContainsTables);
        return stockContainsTables;
    }

    private List<StockContainsTable> doStockContainsTable1(List<StockDto> stockTables) {
        List<StockContainsTable> stockContainsTables = new ArrayList<>();

        if (CollectionUtils.isEmpty(stockTables)) {
            return stockContainsTables;
        }
        for (StockDto stockTable : stockTables) {
            StockContainsTable stockContainsTable = new StockContainsTable();
            BeanUtils.copyProperties(stockTable, stockContainsTable);
            stockContainsTable.setType(StockContainsType.NOT_MERGED.getValue());
            stockContainsTable.setId(UUID.randomUUID().toString());
            stockContainsTable.setType(stockTable.getClose().compareTo(stockTable.getOpen()) >=0 ? FenxingType.TOP_PART.getValue() : FenxingType.BOTTOM_PART.getValue());
            stockContainsTable.setUpdateTime(stockTable.getDt());
            stockContainsTables.add(stockContainsTable);
        }
        return getContainsTables(stockContainsTables);
    }

    private List<StockContainsTable> getContainsTables(List<StockContainsTable> stockContainsTables) {
        int preSize = stockContainsTables.size();
        for (int i = 2; i < stockContainsTables.size(); i++) {
            StockContainsTable preStockContainsTable = stockContainsTables.get(i - 1);
            StockContainsTable stockContainsTable = stockContainsTables.get(i);
            BigDecimal preHigh = preStockContainsTable.getHigh();
            BigDecimal preLow = preStockContainsTable.getLow();

            BigDecimal high = stockContainsTable.getHigh();
            BigDecimal low = stockContainsTable.getLow();

            if ((preHigh.compareTo(high) >= 0 && preLow.compareTo(low) <= 0) ||
                    (preHigh.compareTo(high) <= 0 && preLow.compareTo(low) >= 0)) {
                StockContainsTable superPreKlineContainsDto = stockContainsTables.get(i - 2);
                if (superPreKlineContainsDto.getHigh().compareTo(preHigh) <= 0) {
                    // 向上处理   两根K线中最高点为高点，较低点为地点
                    preStockContainsTable.setHigh(preHigh.compareTo(high) >= 0 ? preHigh : high);
                    preStockContainsTable.setLow(preLow.compareTo(low) >= 0 ? preLow : low);
                } else {
                    // 向下处理
                    preStockContainsTable.setHigh(preHigh.compareTo(high) <= 0 ? preHigh : high);
                    preStockContainsTable.setLow(preLow.compareTo(low) <= 0 ? preLow : low);
                }
                preStockContainsTable.setType(stockContainsTable.getType());
                preStockContainsTable.setUpdateTime(stockContainsTable.getDt());
                stockContainsTables.remove(i);
                i--;
            }
        }
        int size = stockContainsTables.size();
        if (preSize != size) {
            return getContainsTables(stockContainsTables);
        }
        return stockContainsTables;
    }

    /**
     * 自己做的合并
     *
     * @param stockTables
     * @return
     */
    private List<StockContainsTable> doStockContainsTable(List<StockDto> stockTables) {
        List<StockContainsTable> stockContainsTables = new ArrayList<>();
        initStockContains(stockContainsTables, stockTables);
        for (int i = 2; i < stockTables.size(); i++) {
            StockDto stockTable = stockTables.get(i);
            StockContainsTable stockContainsTable = new StockContainsTable();
            BeanUtils.copyProperties(stockTable, stockContainsTable);
            stockContainsTable.setType(stockTable.getClose().compareTo(stockTable.getOpen()) >=0 ? FenxingType.TOP_PART.getValue() : FenxingType.BOTTOM_PART.getValue());
            stockContainsTable.setId(UUID.randomUUID().toString());
            stockContainsTable.setUpdateTime(stockTable.getDt());
            dealContainsData(stockContainsTables, stockContainsTable);
        }
        return stockContainsTables;
    }

    private void setFenxingPower(List<StockContainsTable> stockContainsTables) {
        StockContainsTable pre = stockContainsTables.get(0);
        StockContainsTable current = stockContainsTables.get(1);
        StockContainsTable next = stockContainsTables.get(2);
        Integer type = current.getFenxingType();
        if (FenxingType.TOP_PART.getValue() == type) {
            if (next.getLow().compareTo(pre.getLow()) < 0 && current.getLow().compareTo(next.getHigh()) > 0) {
                current.setFenxingPower(FenxingLevel.STRONGEST.getValue());
                return;
            }
            if (next.getLow().compareTo(pre.getLow()) < 0) {
                current.setFenxingPower(FenxingLevel.STRONG.getValue());
                return;
            }
            if (next.getLow().compareTo(pre.getLow()) == 0) {
                current.setFenxingPower(FenxingLevel.WEAKER.getValue());
                return;
            }
            if (next.getLow().compareTo(pre.getLow()) > 0) {
                current.setFenxingPower(FenxingLevel.WEAKEST.getValue());
                return;
            }
        }
        if (FenxingType.BOTTOM_PART.getValue() == type) {
            if (next.getHigh().compareTo(pre.getHigh()) > 0 && current.getHigh().compareTo(next.getHigh()) < 0) {
                current.setFenxingPower(FenxingLevel.STRONGEST.getValue());
                return;
            }
            if (next.getHigh().compareTo(pre.getHigh()) > 0) {
                current.setFenxingPower(FenxingLevel.STRONG.getValue());
                return;
            }
            if (next.getHigh().compareTo(pre.getHigh()) == 0) {
                current.setFenxingPower(FenxingLevel.WEAKER.getValue());
                return;
            }
            if (next.getHigh().compareTo(pre.getHigh()) < 0) {
                current.setFenxingPower(FenxingLevel.WEAKEST.getValue());
                return;
            }
        }
    }

    private void dealContainsData(List<StockContainsTable> stockContainsTables, StockContainsTable stockContainsTable) {
        StockContainsTable containsTable = stockContainsTables.get(stockContainsTables.size() - 1);
        BigDecimal preHigh = containsTable.getHigh();
        BigDecimal preLow = containsTable.getLow();
        BigDecimal high = stockContainsTable.getHigh();
        BigDecimal low = stockContainsTable.getLow();

        if ((high.compareTo(preHigh) >= 0 && low.compareTo(preLow) <= 0) ||
                (high.compareTo(preHigh) <= 0 && low.compareTo(preLow) >= 0)) {
            StockContainsTable preContainsTable = stockContainsTables.get(stockContainsTables.size() - 2);
            if (preContainsTable.getHigh().compareTo(preHigh) >= 0) {
                //向下处理,取低低
                containsTable.setHigh(preHigh.compareTo(high) >= 0 ? high : preHigh);
                containsTable.setLow(preLow.compareTo(low) >= 0 ? preLow : low);
                containsTable.setClose(stockContainsTable.getClose());
            } else {
                //向上处理,取高高
                containsTable.setHigh(preHigh.compareTo(high) <= 0 ? high : preHigh);
                containsTable.setLow(preLow.compareTo(low) <= 0 ? preLow : low);
                containsTable.setClose(stockContainsTable.getClose());
            }
            containsTable.setUpdateTime(stockContainsTable.getDt());
            if (stockContainsTables.size() == 2) {
                return;
            }
            stockContainsTables.remove(stockContainsTables.size() - 1);
            dealContainsData(stockContainsTables, containsTable);
            return;
        }
        stockContainsTables.add(stockContainsTable);
    }

    private void initStockContains(List<StockContainsTable> stockContainsTables, List<StockDto> stockTables) {
        StockContainsTable stockContainsTable1 = new StockContainsTable();
        StockDto stockTable1 = stockTables.get(0);
        BeanUtils.copyProperties(stockTable1, stockContainsTable1);
        stockContainsTable1.setId(UUID.randomUUID().toString());
        stockContainsTable1.setType(stockTable1.getClose().compareTo(stockTable1.getOpen()) >=0 ? FenxingType.TOP_PART.getValue() : FenxingType.BOTTOM_PART.getValue());
        stockContainsTable1.setUpdateTime(stockTable1.getDt());
        stockContainsTables.add(stockContainsTable1);

        StockContainsTable stockContainsTable2 = new StockContainsTable();
        StockDto stockTable2 = stockTables.get(1);
        BeanUtils.copyProperties(stockTable2, stockContainsTable2);
        stockContainsTable2.setId(UUID.randomUUID().toString());
        stockContainsTable2.setType(stockTable2.getClose().compareTo(stockTable2.getOpen()) >=0 ? FenxingType.TOP_PART.getValue() : FenxingType.BOTTOM_PART.getValue());
        stockContainsTable2.setUpdateTime(stockTable2.getDt());
        stockContainsTables.add(stockContainsTable2);
    }
}