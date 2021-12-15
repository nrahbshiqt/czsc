package com.czsc.task;

import com.czsc.entity.StockTable;
import com.czsc.service.StockTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class DealExcelTask {
    @Autowired
    private StockTableService stockTableService;

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void dealData() throws ParseException {
        File file = new File("E:\\300672");
        File[] files = file.listFiles();
        List<StockTable> stockTables = new ArrayList<>();
        for (File f : files) {
            List<String> excel = importCsv(f);
            if (excel.size() > 1) {
                for (int i = 1; i < excel.size(); i++) {
                    String s = excel.get(i);
                    String[] split = s.split(",");
                    StockTable stockTable = new StockTable();
                    stockTable.setSymbol("300672.XSHG");
                    stockTable.setDt(df.parse(split[1]));
                    stockTable.setOpen(new BigDecimal(split[2]));
                    stockTable.setClose(new BigDecimal(split[3]));
                    stockTable.setHigh(new BigDecimal(split[4]));
                    stockTable.setLow(new BigDecimal(split[5]));
                    stockTable.setVolume(new BigDecimal(split[6]));
                    stockTable.setPeriod("1min");
                    stockTable.setUpdateTime(stockTable.getDt());
                    stockTables.add(stockTable);
                }
            }
        }
        stockTableService.saveBatch(stockTables);


    }

    /**
     * 写入
     *
     * @param file csv文件(路径+文件)
     * @return
     */
    public static List<String> importCsv(File file) {
        List<String> dataList = new ArrayList<String>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataList;
    }

}