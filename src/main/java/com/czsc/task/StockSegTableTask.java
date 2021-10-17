package com.czsc.task;

import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.czsc.common.FenxingType;
import com.czsc.dto.StockBiDto;
import com.czsc.dto.StockSegDto;
import com.czsc.service.StockBiTableService;
import com.czsc.service.StockSegTableService;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockSegTableTask {
    @Autowired
    private StockBiTableService stockBiTableService;

    @Autowired
    private StockSegTableService stockSegTableService;

    public void dealData() {
        List<StockBiDto> stockBiDtos = stockBiTableService.getStockBiDtoBySymbol("000001.XSHG");
        if (CollectionUtils.isEmpty(stockBiDtos)) {
            return;
        }

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
        int index = start -1;
        while (i <= stockBiDtos.size() - 4) {
            StockSegDto stockSegDto = new StockSegDto(new ArrayList<>(stockBiDtos.subList(i, i + 4)));
            boolean label = Boolean.FALSE;
            while (label == Boolean.FALSE && i <= stockBiDtos.size() - 6) {
                i = i + 2;
                label = growSeg(stockSegDto, new ArrayList<>(stockBiDtos.subList(i + 2, i + 4)));
                List<StockBiDto> dtos = stockSegDto.getStockBiDtos();
                if (dtos.get(dtos.size() - 1).getStartTime().getTime() > stockBiDtos.get(stockBiDtos.size() -3).getStartTime().getTime()) {
                    end = Boolean.TRUE;
                    stockSegDtos.add(stockSegDto);
                    break;
                }
            }
            if (end) {
                break;
            }
            index += (stockSegDto.getStockBiDtos().size() - (stockSegDtos.size() >= 1 ? 1:0));
            i = index;
            stockSegDtos.add(stockSegDto);
        }
        System.out.println("a");
    }

    private boolean growSeg(StockSegDto stockSegDto, List<StockBiDto> stockBiDtos) {
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
                        &&(lastStockBiDto2.getStartPrice().compareTo(lastStockBiDto4.getStartPrice()) < 0 ))) {
                    stockSegDto.setFinished(Boolean.TRUE);
                    filterStockBiDto(stockSegDto);
                    stockSegDto.setEndPrice(stockSegDto.getCurExtreme());
                    stockSegDto.setEndTime(stockSegDto.getCurExtremePos());
                    return Boolean.TRUE;
                }
            }
            stockSegDto.getStockBiDtos().addAll(stockBiDtos);
            return Boolean.FALSE;
        } else {
            if (stockBiDtos.get(1).getStartPrice().compareTo(stockSegDto.getCurExtreme()) <= 0){
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
                    stockSegDto.setEndPrice(stockSegDto.getCurExtreme());
                    stockSegDto.setEndTime(stockSegDto.getCurExtremePos());
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
            if(stockBiDto.getStartTime().getTime() > time) {
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
}
