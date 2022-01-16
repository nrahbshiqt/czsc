package com.czsc.util;

import com.czsc.common.FenxingType;
import com.czsc.dto.FenxingDto;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class BiUtils {

    public List<BiDto> getBiDtos(List<FenxingDto> fenxingDtos) {
        if (CollectionUtils.isEmpty(fenxingDtos)) {
            return null;
        }
        List<BiDto> biDtos = new ArrayList<>();
        int _dd_total = 0;
        BiDto biDto = null;
        for (FenxingDto fenxingDto : fenxingDtos) {
            if (!fenxingDto.getReal()) {
                _dd_total++;
                continue;
            }
            if (biDto == null) {
                biDto = new BiDto();
                biDto.setStart(fenxingDto);
                continue;
            }
            biDto.setEnd(fenxingDto);
            biDto.setType(FenxingType.TOP_PART.getValue());
            if (biDto.getStart().getType().equals(FenxingType.TOP_PART.getValue())) {
                biDto.setType(FenxingType.BOTTOM_PART.getValue());
            }
            biDto.setHigh(biDto.getStart().getVal().max(biDto.getEnd().getVal()));
            biDto.setLow(biDto.getStart().getVal().min(biDto.getEnd().getVal()));
            biDto.setDone(fenxingDto.getDone());
            biDto.setFxNum(_dd_total);
            biDtos.add(biDto);
            biDto = new BiDto();
            biDto.setStart(fenxingDto);
            _dd_total = 0;
        }
        for (int i = 0; i < biDtos.size(); i++) {
            BiDto biDto1 = biDtos.get(i);
            biDto1.setIndex(i);
        }
        return biDtos;
    }


}
