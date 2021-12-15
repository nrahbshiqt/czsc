package com.czsc.task;

import com.czsc.entity.StockTable;
import com.czsc.service.StockTableService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelToDbTask {
    private SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private StockTableService stockTableService;

    public void dealData() throws Exception {
        File file = new File("E:\\allstock");
        File[] files = file.listFiles();
        for (File f : files) {
            String filePath = f.getCanonicalPath();
            List<StockTable> stockTables = readExcel(filePath);
            stockTableService.saveBatch(stockTables);
            System.out.println("a");
        }
    }


    public List<StockTable> readExcel(String filePath) throws Exception {
        List<StockTable> stockTables = new ArrayList<>();
        //用流的方式先读取到你想要的excel的文件
        FileInputStream fis = new FileInputStream(new File(filePath));
        //解析excel
        POIFSFileSystem pSystem = new POIFSFileSystem(fis);
        //获取整个excel
        HSSFWorkbook hb = new HSSFWorkbook(pSystem);
        System.out.println(hb.getNumCellStyles());
        //获取第一个表单sheet
        HSSFSheet sheet = hb.getSheetAt(0);
        //获取第一行
        int firstrow = sheet.getFirstRowNum();
        //获取最后一行
        int lastrow = sheet.getLastRowNum();
        //循环行数依次获取列数
        for (int i = firstrow + 1; i < lastrow + 1; i++) {
            //获取哪一行i
            HSSFRow row = sheet.getRow(i);
            if (row != null) {
                //获取这一行的第一列
                int firstcell = row.getFirstCellNum();
                //获取这一行的最后一列
                int lastcell = row.getLastCellNum();
                //创建一个集合，用处将每一行的每一列数据都存入集合中
                List<String> list = new ArrayList<>();
                for (int j = firstcell; j < lastcell; j++) {
                    //获取第j列
                    HSSFCell cell = row.getCell(j);

                    if (cell != null) {
                        list.add(cell.toString());
                    }
                }

                if (list.size() > 0) {
                    StockTable stockTable = new StockTable();
                    stockTable.setSymbol(list.get(0).split("\\.")[0]);
                    stockTable.setDt(sp.parse(list.get(1)));
                    stockTable.setOpen(new BigDecimal(list.get(2)));
                    stockTable.setHigh(new BigDecimal(list.get(3)));
                    stockTable.setLow(new BigDecimal(list.get(4)));
                    stockTable.setClose(new BigDecimal(list.get(5)));
                    stockTable.setVolume(new BigDecimal(list.get(9)));
                    stockTable.setPeriod("1day");
                    stockTable.setUpdateTime(stockTable.getDt());
                    stockTables.add(stockTable);
                }
            }
        }
        fis.close();
        return stockTables;
    }
}
