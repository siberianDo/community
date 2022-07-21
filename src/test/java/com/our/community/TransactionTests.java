package com.our.community;


import com.our.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;


    @Test
    public void save1(){
        Object obj=alphaService.save1();
        System.out.println(obj);
    }

    @Test
    public void save2(){
        Object obj=alphaService.save2();
        System.out.println(obj);
    }

}
