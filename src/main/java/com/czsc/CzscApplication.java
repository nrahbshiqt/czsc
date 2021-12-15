package com.czsc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.czsc", "com.czsc.service.impl"})
@MapperScan("com.czsc.dao")
public class CzscApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CzscApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CzscApplication.class);
    }
}
