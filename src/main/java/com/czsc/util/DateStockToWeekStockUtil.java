package com.czsc.util;

import com.czsc.dto.StockDto;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DateStockToWeekStockUtil {

    public static List<StockDto> dateToWeekStockDto(List<StockDto> stockDtos) {
        List<StockDto> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(stockDtos) || stockDtos.size() < 2) {
            return stockDtos;
        }
        StockDto stockDto = new StockDto();
        BeanUtils.copyProperties(stockDtos.get(0), stockDto);
        stockDto.setPeriod("1week");
        result.add(stockDto);
        for (int i = 1; i < stockDtos.size(); i++) {
            buildWeekStockDto(result, stockDtos.get(i));
        }
        return result;
    }

    private static void buildWeekStockDto(List<StockDto> result, StockDto stockDto) {
        StockDto pre = result.get(result.size() - 1);
        if (DateUtil.isWeekSame(pre.getDt(), stockDto.getDt())) {
            pre.setDt(stockDto.getDt());
            pre.setClose(stockDto.getClose());
            pre.setLow(pre.getLow().min(stockDto.getLow()));
            pre.setHigh(pre.getHigh().max(stockDto.getHigh()));
            pre.setVolume(pre.getVolume().add(stockDto.getVolume()));
        } else {
            StockDto newStock = new StockDto();
            BeanUtils.copyProperties(stockDto, newStock);
            newStock.setPeriod("1week");
            result.add(newStock);
        }
    }
}
