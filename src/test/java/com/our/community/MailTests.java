package com.our.community;

import com.our.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    //    发送邮件组件注入
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //普通邮件
    @Test
    public void testTextMail() {
        mailClient.sendMail("193733757@qq.com",
                "Test", "这是测试");
    }

    //使用模板引擎发送邮件
    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","Jiang");

        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("193733757@qq.com",
                "Test2", content);
    }

}
