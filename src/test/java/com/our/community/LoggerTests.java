package com.our.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {

    private static final Logger logger= LoggerFactory.getLogger(LoggerTests.class);

    //测试日志打印输出
    // 详细配置见该项目目录下templates目录下的logback-spring.xml配置文件
    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("log debug");
        logger.info("log info");
        logger.warn("log warn");
        logger.error("log error");
    }

}
