package com.czsc.chanlun.kline.dto;

import com.czsc.common.FenxingType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zzw on 2021/4/11.
 */
@Data
public class SegDto  extends TableKey {
    private Date startDate;

    private Integer startIndex;
    private Integer type;
    private Boolean finished;
    private List<BiDto> biDtos;
    private Boolean gap;

    private BigDecimal curExtreme;
    private Integer curExtremePos;
    private Date curExtremeDate;
    private BigDecimal prevExtreme;

    public SegDto(List<BiDto> biDtos, List<KlineContainsDto> klineContainsDtos) {
        this.startDate = biDtos.get(0).getStartTime();
        this.startIndex = biDtos.get(0).getStartIndex();
        this.type = -biDtos.get(0).getType();
        this.finished = false;
        this.biDtos = biDtos;
        this.gap = false;
        KlineContainsDto klineContainsDto3 = klineContainsDtos.get(biDtos.get(2).getEndIndex());
        KlineContainsDto klineContainsDto1 = klineContainsDtos.get(biDtos.get(0).getEndIndex());
        if (this.type == 1) {
            this.curExtreme = klineContainsDto3.getHigh();
            this.curExtremePos = biDtos.get(2).getEndIndex();
            this.curExtremeDate = biDtos.get(2).getEndTime();
            this.prevExtreme = klineContainsDto1.getHigh();
        } else {
            this.curExtreme = klineContainsDto3.getLow();
            this.curExtremePos = biDtos.get(2).getEndIndex();
            this.curExtremeDate = biDtos.get(2).getEndTime();
            this.prevExtreme = klineContainsDto1.getLow();
        }
    }

    public void grow(List<BiDto> biDtos, List<KlineContainsDto> klineContainsDtos) {
        if (1 == this.type) {
            System.out.println("a");
        } else {
            if (klineContainsDtos.get(biDtos.get(0).getEndIndex()).getLow().compareTo(this.curExtreme) <= 0) {
                if (klineContainsDtos.get(biDtos.get(0).getStartIndex()).getHigh().compareTo(this.prevExtreme) < 0) {
                    this.gap = true;
                } else {
                    this.gap = false;
                }
                this.biDtos.addAll(biDtos);
                this.prevExtreme=this.curExtreme;
                this.curExtreme = klineContainsDtos.get(biDtos.get(0).getEndIndex()).getLow();
                this.curExtremePos = biDtos.get(0).getEndIndex();
                this.curExtremeDate = klineContainsDtos.get(biDtos.get(0).getEndIndex()).getDt();
            } else {
                if ((this.gap == false && klineContainsDtos.get(biDtos.get(0).getEndIndex()).getHigh().compareTo(klineContainsDtos.get(this.biDtos.get(this.biDtos.size() - 1).getStartIndex()).getHigh()) > 0)
                    ) {
                    
                }
            }
            System.out.println("a");
        }
        System.out.println("a");
    }
}
