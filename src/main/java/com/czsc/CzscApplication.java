package com.czsc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.czsc", "com.czsc.service.impl"})
@MapperScan("com.czsc.dao")
public class CzscApplication {

    public static void main(String[] args) {
        SpringApplication.run(CzscApplication.class, args);
    }

}
