package com.czsc.chanlun.kline.chanlun;

import com.czsc.chanlun.kline.dto.BiDto;
import com.czsc.chanlun.kline.dto.KlineContainsDto;
import com.czsc.chanlun.kline.dto.SegDto;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zzw on 2021/4/2.
 */
public class ChanLunUtil {

    public static List<KlineContainsDto> getKlineContains(List<KlineContainsDto> klineContainsDtos) {
        if (CollectionUtils.isEmpty(klineContainsDtos)) {
            return klineContainsDtos;
        }
        int preSize = klineContainsDtos.size();
        for (int i = 2; i < klineContainsDtos.size(); i++) {
            KlineContainsDto preKlineContainsDto = klineContainsDtos.get(i - 1);
            KlineContainsDto klineContainsDto = klineContainsDtos.get(i);
            BigDecimal preHigh = preKlineContainsDto.getHigh();
            BigDecimal preLow = preKlineContainsDto.getLow();

            BigDecimal high = klineContainsDto.getHigh();
            BigDecimal low = klineContainsDto.getLow();

            if ((preHigh.compareTo(high) >= 0 && preLow.compareTo(low) <= 0) ||
                    (preHigh.compareTo(high) <= 0 && preLow.compareTo(low) >= 0)) {
                KlineContainsDto superPreKlineContainsDto = klineContainsDtos.get(i - 2);
                if (superPreKlineContainsDto.getHigh().compareTo(preHigh) <= 0) {
                    // 向上处理   两根K线中最高点为高点，较低点为地点
                    preKlineContainsDto.setHigh(preHigh.compareTo(high) >= 0 ? preHigh : high);
                    preKlineContainsDto.setLow(preLow.compareTo(low) >= 0 ? preLow : low);
                } else {
                    // 向下处理
                    preKlineContainsDto.setHigh(preHigh.compareTo(high) <= 0 ? preHigh : high);
                    preKlineContainsDto.setLow(preLow.compareTo(low) <= 0 ? preLow : low);
                }
                klineContainsDtos.remove(i);
                i--;
            }
        }
        int size = klineContainsDtos.size();
        if (preSize != size) {
            return getKlineContains(klineContainsDtos);
        }
        return klineContainsDtos;
    }

    public static List<KlineContainsDto> getFenXing(List<KlineContainsDto> klineContainsDtos) {
        if (CollectionUtils.isEmpty(klineContainsDtos)) {
            return klineContainsDtos;
        }
        klineContainsDtos.get(0).setFenXing(0);
        for (int i = 1; i < klineContainsDtos.size() - 1; i++) {
            KlineContainsDto preKlineContainsDto = klineContainsDtos.get(i - 1);
            BigDecimal preHigh = preKlineContainsDto.getHigh();
            BigDecimal preLow = preKlineContainsDto.getLow();
            KlineContainsDto klineContainsDto = klineContainsDtos.get(i);
            BigDecimal high = klineContainsDto.getHigh();
            BigDecimal low = klineContainsDto.getLow();
            KlineContainsDto nextKlineContainsDto = klineContainsDtos.get(i + 1);
            BigDecimal nextHigh = nextKlineContainsDto.getHigh();
            BigDecimal nextLow = nextKlineContainsDto.getLow();
            // 中间K线最高点，最高为顶分型
            if (high.compareTo(preHigh) >= 0 && high.compareTo(nextHigh) >= 0) {
                klineContainsDto.setFenXing(1);
                continue;
                // 中间K线最低点，最低为底分型
            } else if (low.compareTo(preLow) <= 0 && low.compareTo(nextLow) <= 0) {
                klineContainsDto.setFenXing(-1);
                continue;
            } else {
                klineContainsDto.setFenXing(0);
            }
        }
        klineContainsDtos.get(klineContainsDtos.size() - 1).setFenXing(0);
        return klineContainsDtos;
    }


    public static List<BiDto> getBi(List<KlineContainsDto> klineContainsDtos) {
        List<BiDto> biDtos = new ArrayList<>();
        if (CollectionUtils.isEmpty(klineContainsDtos)) {
            return biDtos;
        }
        checkInitBi(klineContainsDtos);
        if (klineContainsDtos.size() <= 60) {
            return biDtos;
        }
        BiDto biDto = new BiDto();
        KlineContainsDto klineContainsDto = klineContainsDtos.get(0);
        BeanUtils.copyProperties(klineContainsDto, biDto);
        biDto.setStartPrice(klineContainsDto.getHigh());
        biDto.setStartTime(klineContainsDto.getDt());
        biDto.setStartIndex(0);
        List<KlineContainsDto> list = new ArrayList<>();
        list.add(klineContainsDto);
        biDto.setKlineContainsDtos(list);
        biDtos.add(biDto);
        List<Integer> judge = Arrays.asList(0, 0, 1);
        while (true) {
            judge = judge(judge.get(0), judge.get(1), judge.get(2), biDtos, klineContainsDtos);
            if (CollectionUtils.isEmpty(judge)) {
                break;
            }
        }
        return biDtos;
    }

    private static void checkInitBi(List<KlineContainsDto> klineContainsDtos) {
        for (int i = 0; i < klineContainsDtos.size(); i++) {
            if (klineContainsDtos.get(i).getFenXing() == 0) {
                klineContainsDtos.remove(i);
                i--;
            } else {
                break;
            }
        }
        for (int i = 0; i < klineContainsDtos.size(); i++) {
            KlineContainsDto klineContainsDto = klineContainsDtos.get(i);
            if (klineContainsDto.getFenXing() != 0) {
                KlineContainsDto klineContainsDto1 = klineContainsDtos.get(i + 1);
                KlineContainsDto klineContainsDto2 = klineContainsDtos.get(i + 2);
                KlineContainsDto klineContainsDto3 = klineContainsDtos.get(i + 3);
                if (klineContainsDto1.getFenXing() == 0
                        && klineContainsDto2.getFenXing() == 0
                        && klineContainsDto3.getFenXing() == 0) {
                    break;
                }
            }
            klineContainsDtos.remove(i);
            i--;
        }
    }

    private static List<Integer> judge(int preIndex, int index, int d, List<BiDto> biDtos, List<KlineContainsDto> klineContainsDtos) {
        if (index + 4 >= klineContainsDtos.size() - 1) {
            return null;
        }
        if (index - preIndex < 4 || klineContainsDtos.get(index).getFenXing() != d) {
            index++;
            return judge(preIndex, index, d, biDtos, klineContainsDtos);
        }
        List<Object> existNewExtreme = existNewExtreme(index, d, 2, 3, klineContainsDtos);
        if (existNewExtreme.get(1) == Boolean.TRUE) {
            index = (int) existNewExtreme.get(0);
            return judge(preIndex, index, d, biDtos, klineContainsDtos);
        } else {
            int k = 4;
            if (index + k + 1 >= klineContainsDtos.size() - 1) {
                return null;
            }
            while (!existOpposite(index, d, k, klineContainsDtos)) {
                List<Object> exist = existNewExtreme(index, d, k, k, klineContainsDtos);
                if (exist.get(1) == Boolean.TRUE) {
                    index = (int) exist.get(0);
                    return judge(preIndex, index, d, biDtos, klineContainsDtos);
                } else {
                    k++;
                    if (index + k >= klineContainsDtos.size() - 1) {
                        return null;
                    }
                }

            }
            preIndex = index;
            index = index + k;
            BiDto preDto = biDtos.get(biDtos.size() - 1);
            KlineContainsDto klineContainsDto = klineContainsDtos.get(preIndex);
            preDto.setEndTime(klineContainsDto.getDt());
            preDto.setEndPrice(klineContainsDto.getFenXing() == 1 ? klineContainsDto.getHigh() :
                    klineContainsDto.getLow());
            preDto.setEndIndex(preIndex);
            preDto.setType(d);
            BiDto biDto = new BiDto();
            BeanUtils.copyProperties(klineContainsDto, biDto);
            biDto.setStartTime(preDto.getEndTime());
            biDto.setStartPrice(preDto.getEndPrice());
            biDto.setStartIndex(preDto.getEndIndex());
            biDtos.add(biDto);
            return Arrays.asList(preIndex, index, -d);
        }
    }

    private static boolean existOpposite(int index, int d, int pos, List<KlineContainsDto> klineContainsDtos) {
        return klineContainsDtos.get(index + pos).getFenXing() == -d &&
                sameFenXing(klineContainsDtos.get(index), klineContainsDtos.get(index + pos), d);
    }

    private static boolean sameFenXing(KlineContainsDto kline1, KlineContainsDto kline2, int d) {
        if (d == 1) {
            return kline1.getLow().compareTo(kline2.getLow()) > 0 &&
                    kline1.getHigh().compareTo(kline2.getHigh()) > 0;
        }
        return kline1.getLow().compareTo(kline2.getLow()) < 0 &&
                kline1.getHigh().compareTo(kline2.getHigh()) < 0;
    }

    private static List<Object> existNewExtreme(int index, int d, int start, int end, List<KlineContainsDto> klineContainsDtos) {
        int j = start;
        while (j <= end) {
            if (newExtreme(klineContainsDtos.get(index), klineContainsDtos.get(index + j), d)) {
                return Arrays.asList(index + j, true);
            }
            j++;
        }
        return Arrays.asList(index, false);
    }

    private static boolean newExtreme(KlineContainsDto kline1, KlineContainsDto kline2, int d) {
        if (d == 1) {
            return kline2.getHigh().compareTo(kline1.getHigh()) >= 0;
        }
        return kline1.getLow().compareTo(kline2.getLow()) >= 0;
    }


    private static boolean checkInitSeg(List<BiDto> biDtos, List<KlineContainsDto> klineContainsDtos) {
        if (CollectionUtils.isEmpty(biDtos) || biDtos.size() < 3) {
            return false;
        }

        int d = -biDtos.get(0).getType();
        KlineContainsDto klineContainsDto0 = klineContainsDtos.get(biDtos.get(0).getStartIndex());
        KlineContainsDto klineContainsDto1 = klineContainsDtos.get(biDtos.get(1).getStartIndex());
        KlineContainsDto klineContainsDto2 = klineContainsDtos.get(biDtos.get(2).getStartIndex());
        KlineContainsDto klineContainsDto3 = klineContainsDtos.get(biDtos.get(2).getEndIndex());
        if (d == 1) {
            if (klineContainsDto1.getHigh().compareTo(klineContainsDto3.getHigh()) < 0 &&
                    klineContainsDto0.getLow().compareTo(klineContainsDto2.getLow()) < 0) {
                return true;
            }
            return false;
        }
        if (klineContainsDto1.getLow().compareTo(klineContainsDto3.getLow()) > 0 &&
                klineContainsDto0.getHigh().compareTo(klineContainsDto2.getHigh()) > 0) {
            return true;
        }
        return false;
    }

    public static void getSeg(List<BiDto> biDtos, List<KlineContainsDto> klineContainsDtos) {
        int start = 0;
        while (start < biDtos.size() - 4) {
            if (checkInitSeg(biDtos.subList(start, start + 3), klineContainsDtos)) {
                break;
            }
            start++;
        }
        for (int i = start; i < biDtos.size() - 3; i++) {
            SegDto segDto = new SegDto(biDtos.subList(i, i + 3), klineContainsDtos);
            boolean label = Boolean.FALSE;
            while (!label && i <= biDtos.size() -6) {
                i+=2;
                segDto.grow(biDtos.subList(i + 2, i + 4), klineContainsDtos);
            }
            System.out.println("a");
        }
        System.out.println("a");
    }
}
