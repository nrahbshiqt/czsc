package com.czsc.task;

import com.czsc.chanlun.kline.chanlun.ChanLunUtil;
import com.czsc.chanlun.kline.dto.BiDto;
import com.czsc.chanlun.kline.dto.KlineContainsDto;
import com.czsc.entity.StockTable;
import com.czsc.service.StockTableService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChanLunTask {
    @Autowired
    private StockTableService stockTableService;

    public void dealData() {
        List<StockTable> stockTables = stockTableService.getStockTablesBySymbolAndPeriod("000001.XSHG", "1day");
        if (CollectionUtils.isEmpty(stockTables)) {
            return;
        }
        List<KlineContainsDto> klineContainsDtos = new ArrayList<>();
        for (StockTable stockTable : stockTables) {
            KlineContainsDto klineContainsDto = new KlineContainsDto();
            BeanUtils.copyProperties(stockTable, klineContainsDto);
            klineContainsDtos.add(klineContainsDto);
        }
        ChanLunUtil.getKlineContains(klineContainsDtos);
        ChanLunUtil.getFenXing(klineContainsDtos);
        List<BiDto> bi = ChanLunUtil.getBi(klineContainsDtos);
        ChanLunUtil.getSeg(bi, klineContainsDtos);
    }
}
