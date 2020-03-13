package com.tongzy.concurrency.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
class TestControllerTest {

    @Resource
    private TestController testController;

    @Test
    void test() {
        String test = testController.test();
        log.info("method return: {}", test);
        Assert.notNull(test, "[Assertion failed] - call TestController test() and return null");
    }
}