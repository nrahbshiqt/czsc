package com.czsc;

import com.czsc.task.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CzscApplicationTests {

    @Autowired
    private BiChanLunUtilTask stockTableTask;

    @Test
    void contextLoads() throws Exception {
        stockTableTask.dealData11();
    }

}
