package com.czsc.util;

import com.czsc.common.FenxingType;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SegUtils {

    public List<SegDto> getSegDtos(List<BiDto> biDtos) {
        findBiXulieFx(FenxingType.TOP_PART.getValue(), biDtos);
    }

    private void findBiXulieFx(Integer type, List<BiDto> biDtos) {
        List<XuLie> xuLies = new ArrayList<>();
        for (int i = 0; i < biDtos.size(); i++) {
            BiDto biDto = biDtos.get(i);

            if ((type == FenxingType.TOP_PART.getValue() && biDto.getType().equals(FenxingType.BOTTOM_PART.getValue()))
                    || (type == FenxingType.BOTTOM_PART.getValue() && biDto.getType().equals(FenxingType.TOP_PART.getValue()))) {
                XuLie xuLie = new XuLie();
                xuLie.setBiDto(biDto);
                xuLie.setMax(biDto.getHigh());
                xuLie.setMin(biDto.getLow());
                if (CollectionUtils.isEmpty(xuLies)) {
                    xuLies.add(xuLie);
                    continue;
                }
                XuLie last = xuLies.get(xuLies.size() - 1);
                if ((last.getMax().compareTo(xuLie.getMax()) >= 0 && last.getMin().compareTo(xuLie.getMin()) <= 0)
                        || (last.getMin().compareTo(xuLie.getMax()) <= 0 && last.getMin().compareTo(xuLie.getMin()) >= 0)) {
                    if (type.equals(FenxingType.TOP_PART.getValue())) {
                        if (xuLie.getMax().compareTo(last.getMax()) < 0) {
                            xuLie.setBiDto(last.getBiDto());
                        }
                        xuLie.setMax(last.getMax().max(xuLie.getMax()));
                        xuLie.setMin(last.getMin().max(xuLie.getMin()));
                    } else {
                        if (xuLie.getMin().compareTo(last.getMin()) > 0) {
                            xuLie.setBiDto(last.getBiDto());
                        }
                        xuLie.setMax(last.getMax().min(xuLie.getMax()));
                        xuLie.setMin(last.getMin().min(xuLie.getMin()));
                    }
                    xuLies.remove(last);
                    xuLies.add(xuLie);
                } else {
                    xuLies.add(xuLie);
                }
            }
        }
    }

    @Data
    class XuLie {
        private BigDecimal max;
        private BigDecimal min;
        private BiDto biDto;
    }
}
