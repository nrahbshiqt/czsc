package com.czsc.controller;

import com.czsc.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result initStock() {
        return new Result(1, "success");
    }
}
