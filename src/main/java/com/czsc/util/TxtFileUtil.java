package com.czsc.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.czsc.entity.StockTable;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TxtFileUtil {
    private static final List<Integer> StockYearList = new ArrayList<>();
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            File filename = new File(filePath); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                sb.append(line); // 一次读入一行数据
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static void main(String[] args) throws ParseException {
        String data = TxtFileUtil.readFile("E:\\000001\\000001_1day.txt");
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        JSONObject json = JSONObject.parseObject(data);
        JSONArray sortYear = json.getJSONArray("sortYear");
        for (int i = 0; i < sortYear.size(); i++) {
            StockYearList.add(sortYear.getJSONArray(i).getInteger(0));
        }
        Integer total = json.getInteger("total");
        String priceStr = json.getString("price");
        String volumnStr = json.getString("volumn");
        String dateStr = json.getString("dates");
        String priceFactor = json.getString("priceFactor");
        List<StockTable> stockTables = buildStockTable(total, dateStr, priceStr, priceFactor, volumnStr);
        System.out.println("a");
    }

    private static List<StockTable> buildStockTable(Integer total, String dateStr, String priceStr, String priceFactor, String volumnStr) throws ParseException {
        BigDecimal priceFactorBd = new BigDecimal(priceFactor);
        List<StockTable> stockTables = new ArrayList<>();
        String[] dateArr = dateStr.split(",");
        String[] priceArr = priceStr.split(",");
        List<Object[]> priceList = splitArray(priceArr, 4);
        String[] volumnArr = volumnStr.split(",");
        assert dateArr.length == total;
        assert dateArr.length == volumnArr.length;
        assert dateArr.length == priceList.size();
        for (int i = 0; i < dateArr.length; i++) {
            String date = dateArr[i];
            StockTable stockTable = new StockTable();
            stockTable.setSymbol("000001_pre");
            stockTable.setPeriod("1day");
            String dtStr = StockYearList.get(0) + date + " 15:00:00";
            Date dt = sd.parse(dtStr);
            if (stockTables.size() > 0 && stockTables.get(stockTables.size() - 1).getDt().getTime() > dt.getTime()) {
                StockYearList.remove(0);
                dtStr = StockYearList.get(0) + date + " 15:00:00";
                dt = sd.parse(dtStr);
            }
            stockTable.setDt(dt);
            Object[] prices = priceList.get(i);
            //1932,31,42,18 open,high,close
            stockTable.setLow(new BigDecimal(prices[0].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP));
            stockTable.setOpen(stockTable.getLow().add(new BigDecimal(prices[1].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP)));
            stockTable.setHigh(stockTable.getLow().add(new BigDecimal(prices[2].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP)));
            stockTable.setClose(stockTable.getLow().add(new BigDecimal(prices[3].toString()).divide(priceFactorBd, 2, BigDecimal.ROUND_HALF_UP)));
            stockTable.setVolume(new BigDecimal(volumnArr[i]));
            stockTable.setUpdateTime(stockTable.getDt());
            stockTables.add(stockTable);
        }
        return stockTables;
    }


    /**
     * 分割数组
     *
     * @param array 原数组
     * @param Size  分割后每个数组的最大长度
     * @param <T>   原数组的类型
     * @return
     */
    public static <T> List<Object[]> splitArray(T[] array, int Size) {
        List<Object[]> list = new ArrayList<Object[]>();
        int i = (array.length) % Size == 0 ? (array.length) / Size : (array.length) / Size + 1;
        for (int j = 0; j < i; j++) {
            List<T> list1 = new ArrayList<T>();
            for (int k = 0; k < Size; k++) {
                if ((j * Size + k) >= array.length) {
                    break;
                } else {
                    list1.add((array[j * Size + k]));
                }
            }
            /**
             * list.toArray()之后只能转成Object类型的数组，不能转成int和Integer等
             * 否则会报：java.lang.ClassCastException:
             * [Ljava.lang.Object; cannot be cast to [Ljava.lang.Integer;
             * 非检测性异常
             * 所以只能是List<Object[]>，不能是List<T[]>
             */
            list.add(list1.toArray());
        }
        return list;
    }
}
