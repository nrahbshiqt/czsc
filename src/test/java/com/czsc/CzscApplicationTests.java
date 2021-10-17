package com.czsc;

import com.czsc.task.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

@SpringBootTest
class CzscApplicationTests {

    @Autowired
    private StockSegTableTask stockTableTask;

    @Test
    void contextLoads() throws ParseException {
        stockTableTask.dealData();
    }

}
