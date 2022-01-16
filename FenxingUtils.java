package com.czsc.util;

import com.czsc.common.FenxingLevel;
import com.czsc.common.FenxingType;
import com.czsc.dto.FenxingDto;
import com.czsc.dto.StockContainsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FenxingUtils {
    private List<FenxingDto> getFenxingDtos(List<StockContainsDto> stockContainsDtos) {
        List<FenxingDto> fenxingDtos = new ArrayList<>();
        for (int i = 1; i < stockContainsDtos.size() - 1; i++) {
            StockContainsDto preContains = stockContainsDtos.get(i - 1);
            StockContainsDto containsTable = stockContainsDtos.get(i);
            StockContainsDto nextContains = stockContainsDtos.get(i + 1);

            if (containsTable.getHigh().compareTo(preContains.getHigh()) > 0
                    && containsTable.getHigh().compareTo(nextContains.getHigh()) > 0
                    && containsTable.getLow().compareTo(preContains.getLow()) > 0
                    && containsTable.getLow().compareTo(nextContains.getLow()) > 0) {
                //顶分型
                FenxingDto fenxingDto = buildFenxingDto(preContains, containsTable, nextContains, FenxingType.TOP_PART.getValue());
                setFengxingReal(fenxingDto, fenxingDtos);
                continue;
            } else if (containsTable.getHigh().compareTo(preContains.getHigh()) < 0
                    && containsTable.getHigh().compareTo(nextContains.getHigh()) < 0
                    && containsTable.getLow().compareTo(preContains.getLow()) < 0
                    && containsTable.getLow().compareTo(nextContains.getLow()) < 0) {
                //底分型
                FenxingDto fenxingDto = buildFenxingDto(preContains, containsTable, nextContains, FenxingType.BOTTOM_PART.getValue());
                setFengxingReal(fenxingDto, fenxingDtos);
                continue;
            }
        }

        for (int i = 0; i < fenxingDtos.size(); i++) {
            FenxingDto fenxingDto = fenxingDtos.get(i);
            fenxingDto.setIndex(i);
        }
        return fenxingDtos;
    }

    private FenxingDto buildFenxingDto(StockContainsDto preContains, StockContainsDto containsTable, StockContainsDto nextContains, int type) {
        FenxingDto fenxingDto = new FenxingDto();
        fenxingDto.setType(type);
        fenxingDto.setStockContainsDto(containsTable);
        fenxingDto.setStockContainsDtos(Arrays.asList(preContains, containsTable, nextContains));
        fenxingDto.setVal(containsTable.getLow());
        if (type == FenxingType.TOP_PART.getValue()) {
            fenxingDto.setVal(containsTable.getHigh());
        }
        fenxingDto.setReal(true);
        fenxingDto.setDone(true);
        setFenxingPower(fenxingDto);
        return fenxingDto;
    }


    private void setFengxingReal(FenxingDto fenxingDto, List<FenxingDto> fenxingDtos) {
        if (fenxingDtos.size() == 0) {
            fenxingDtos.add(fenxingDto);
            return;
        }
        FenxingDto last = getLastRealFenxing(fenxingDtos);
        if (last == null) {
            return;
        }
        if (fenxingDto.getType().equals(FenxingType.TOP_PART.getValue())
                && last.getType().equals(FenxingType.TOP_PART.getValue())
                && last.getStockContainsDto().getHigh().compareTo(fenxingDto.getStockContainsDto().getHigh()) <= 0) {
            last.setReal(false);
            fenxingDtos.add(fenxingDto);
            return;
        }
        if (fenxingDto.getType().equals(FenxingType.BOTTOM_PART.getValue())
                && last.getType().equals(FenxingType.BOTTOM_PART.getValue())
                && last.getStockContainsDto().getLow().compareTo(fenxingDto.getStockContainsDto().getLow()) >= 0) {
            last.setReal(false);
            fenxingDtos.add(fenxingDto);
            return;
        }
        if (fenxingDto.getType().equals(last.getType())) {
            fenxingDto.setReal(false);
            fenxingDtos.add(fenxingDto);
            return;
        }
        if (fenxingDto.getType().equals(FenxingType.TOP_PART.getValue())
                && last.getType().equals(FenxingType.BOTTOM_PART.getValue())
                && (fenxingDto.getStockContainsDto().getHigh().compareTo(last.getStockContainsDto().getLow()) <= 0 |
                fenxingDto.getStockContainsDto().getLow().compareTo(last.getStockContainsDto().getHigh()) <= 0)) {
            fenxingDto.setReal(false);
            fenxingDtos.add(fenxingDto);
            return;
        }
        if (fenxingDto.getType().equals(FenxingType.BOTTOM_PART.getValue())
                && last.getType().equals(FenxingType.TOP_PART.getValue())
                && (fenxingDto.getStockContainsDto().getLow().compareTo(last.getStockContainsDto().getHigh()) >= 0 |
                fenxingDto.getStockContainsDto().getHigh().compareTo(last.getStockContainsDto().getLow()) >= 0)) {
            fenxingDto.setReal(false);
            fenxingDtos.add(fenxingDto);
            return;
        }
        if (fenxingDto.getStockContainsDto().getIndex() - last.getStockContainsDto().getIndex() >= 4) {
            fenxingDtos.add(fenxingDto);
            return;
        }
        fenxingDto.setReal(false);
        fenxingDtos.add(fenxingDto);
    }

    private FenxingDto getLastRealFenxing(List<FenxingDto> fenxingDtos) {
        for (int i = fenxingDtos.size() - 1; i >= 0; i--) {
            FenxingDto fenxingDto = fenxingDtos.get(i);
            if (fenxingDto.getReal() == true) {
                return fenxingDto;
            }
        }
        return null;
    }

    private void setFenxingPower(FenxingDto fenxingDto) {
        List<StockContainsDto> stockContainsDtos = fenxingDto.getStockContainsDtos();
        StockContainsDto pre = stockContainsDtos.get(0);
        StockContainsDto current = stockContainsDtos.get(1);
        StockContainsDto next = stockContainsDtos.get(2);
        Integer type = fenxingDto.getType();
        if (FenxingType.TOP_PART.getValue() == type) {
            if (next.getLow().compareTo(pre.getLow()) < 0 && current.getLow().compareTo(next.getHigh()) > 0) {
                fenxingDto.setPower(FenxingLevel.STRONGEST.getValue());
                return;
            }
            if (next.getLow().compareTo(pre.getLow()) < 0) {
                fenxingDto.setPower(FenxingLevel.STRONG.getValue());
                return;
            }
            if (next.getLow().compareTo(pre.getLow()) == 0) {
                fenxingDto.setPower(FenxingLevel.WEAKER.getValue());
                return;
            }
            if (next.getLow().compareTo(pre.getLow()) > 0) {
                fenxingDto.setPower(FenxingLevel.WEAKEST.getValue());
                return;
            }
        }
        if (FenxingType.BOTTOM_PART.getValue() == type) {
            if (next.getHigh().compareTo(pre.getHigh()) > 0 && current.getHigh().compareTo(next.getHigh()) < 0) {
                fenxingDto.setPower(FenxingLevel.STRONGEST.getValue());
                return;
            }
            if (next.getHigh().compareTo(pre.getHigh()) > 0) {
                fenxingDto.setPower(FenxingLevel.STRONG.getValue());
                return;
            }
            if (next.getHigh().compareTo(pre.getHigh()) == 0) {
                fenxingDto.setPower(FenxingLevel.WEAKER.getValue());
                return;
            }
            if (next.getHigh().compareTo(pre.getHigh()) < 0) {
                fenxingDto.setPower(FenxingLevel.WEAKEST.getValue());
                return;
            }
        }
    }
}
