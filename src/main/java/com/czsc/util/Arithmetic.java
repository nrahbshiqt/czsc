package com.czsc.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arithmetic {
    private static final BigDecimal _2 = new BigDecimal("2");

    // n日均线MA, 一般计算5，10，20，30
    public static List<BigDecimal> getMA(List<BigDecimal> entries, int n) {
        BigDecimal _n = new BigDecimal(n);
        List<BigDecimal> result = new ArrayList<>();
        for (int i = 0, len = entries.size(); i < len; i++) {
            if (i < n - 1) {
                result.add(entries.get(i));
                continue;
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = 0; j < n; j++) {
                sum = sum.add(entries.get(i - j));
            }
            result.add(sum.divide(_n, 4, BigDecimal.ROUND_HALF_UP));
        }
        return result;
    }

    /**
     * MACD算法：
     * DIF：EMA(short) - EMA(long) 一般short取12，long取26
     * DEA: EMA(DIF, mid), mid一般取9
     * MACD:(DIF-DEA)*2
     *
     * @param entries
     * @param s
     * @return
     */
    public static List<List<BigDecimal>> getMACD(List<BigDecimal> entries, int s, int l, int m) {
        ArrayList<BigDecimal> listDIF = new ArrayList<>();
        ArrayList<BigDecimal> listDEA = new ArrayList<>();
        ArrayList<BigDecimal> listMACD = new ArrayList<>();

        BigDecimal lastEmaS = entries.get(0);
        BigDecimal lastEmaL = lastEmaS;
        BigDecimal lastDIF = new BigDecimal("0");
        listDIF.add(new BigDecimal("0"));
        listDEA.add(new BigDecimal("0"));
        listMACD.add(new BigDecimal("0"));

        float[] factorShort = getEMAFactor(s);
        float[] factorLong = getEMAFactor(l);
        float[] factorMid = getEMAFactor(m);
        for (int i = 1; i < entries.size(); i++) {
            // 短线EMA
            BigDecimal valueS = entries.get(i).multiply(new BigDecimal(factorShort[0])).add(lastEmaS.multiply(new BigDecimal(factorShort[1])));
            lastEmaS = valueS;
            // 长线EMA
            BigDecimal valueL = entries.get(i).multiply(new BigDecimal(factorLong[0])).add(lastEmaL.multiply(new BigDecimal(factorLong[1])));
            lastEmaL = valueL;
            // DIF：EMA(short) - EMA(long)
            BigDecimal valueDIF = valueS.subtract(valueL).setScale(2, BigDecimal.ROUND_HALF_UP);
            listDIF.add(valueDIF);
            // EMA(DIF, mid)
            BigDecimal valueDEA = valueDIF.multiply(new BigDecimal(factorMid[0])).add(lastDIF.multiply(new BigDecimal(factorMid[1]))).setScale(2, BigDecimal.ROUND_HALF_UP);
            listDEA.add(valueDEA);
            lastDIF = valueDEA;
            // MACD:(DIF-DEA)*2
            listMACD.add((valueDIF.subtract(valueDEA)).multiply(_2));
        }
        return Arrays.asList(listDIF, listDEA, listMACD);
    }

    /**
     * 获取EMA计算时的相关系数
     *
     * @param n
     * @return
     */
    private static float[] getEMAFactor(int n) {
        return new float[]{2f / (n + 1), (n - 1) * 1.0f / (n + 1)};
    }
}
