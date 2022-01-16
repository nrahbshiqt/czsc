package com.czsc.util;

import com.czsc.dto.StockContainsDto;
import com.czsc.dto.StockDto;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ContainsUtils {
    public List<StockContainsDto> getStockContainsDtos(List<StockDto> stockDtos) {
        if (CollectionUtils.isEmpty(stockDtos)) {
            return null;
        }
        if (stockDtos.size() < 100) {
            return null;
        }
        //使用他人方式处理合并问题
        List<StockContainsDto> stockContainsDtos = doStockContainsDto(stockDtos);
        for (int i = 0; i < stockContainsDtos.size(); i++) {
            StockContainsDto stockContainsDto = stockContainsDtos.get(i);
            stockContainsDto.setIndex(i);
        }
        return stockContainsDtos;
    }


    private List<StockContainsDto> doStockContainsDto(List<StockDto> stockDtos) {
        List<StockContainsDto> stockContainsDtos = new ArrayList<>();
        for (StockDto stockDto : stockDtos) {
            StockContainsDto stockContainsTable = new StockContainsDto();
            BeanUtils.copyProperties(stockDto, stockContainsTable);
            stockContainsDtos.add(stockContainsTable);
        }
        while (true) {
            int tempSize = stockContainsDtos.size();
            int i = 0;
            while (i <= stockContainsDtos.size() - 4) {
                StockContainsDto stockContainsTable = stockContainsDtos.get(i);
                StockContainsDto stockContainsTable1 = stockContainsDtos.get(i + 1);
                StockContainsDto stockContainsTable2 = stockContainsDtos.get(i + 2);
                if ((stockContainsTable2.getHigh().compareTo(stockContainsTable1.getHigh()) >= 0 && stockContainsTable2.getLow().compareTo(stockContainsTable1.getLow()) <= 0)
                        || (stockContainsTable2.getHigh().compareTo(stockContainsTable1.getHigh()) <= 0 && stockContainsTable2.getLow().compareTo(stockContainsTable1.getLow()) >= 0)) {
                    if (stockContainsTable1.getHigh().compareTo(stockContainsTable.getHigh()) > 0) {
                        //取高高
                        stockContainsTable2.setHigh(stockContainsTable2.getHigh().max(stockContainsTable1.getHigh()));
                        stockContainsTable2.setLow(stockContainsTable2.getLow().max(stockContainsTable1.getLow()));
                    } else {
                        //取高高
                        stockContainsTable2.setHigh(stockContainsTable2.getHigh().min(stockContainsTable1.getHigh()));
                        stockContainsTable2.setLow(stockContainsTable2.getLow().min(stockContainsTable1.getLow()));
                    }
                    stockContainsTable2.setDt(stockContainsTable1.getDt());
                    stockContainsDtos.remove(stockContainsTable1);
                    continue;
                }
                i++;
            }
            if (tempSize == stockContainsDtos.size()) {
                break;
            }
        }
        return stockContainsDtos;
    }
}
