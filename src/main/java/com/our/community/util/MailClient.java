package com.our.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件组件
 */
@Component
public class MailClient {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(MailClient.class);

    //发送邮件核心组件JavaMailSender
    @Autowired
    private JavaMailSender javaMailSender;

//    发送者，读取配置文件中的参数
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content){
        try {
            //模板
            MimeMessage message = javaMailSender.createMimeMessage();
            //构建详细内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //发件人
            helper.setFrom(from);
            //收件人
            helper.setTo(to);
            //主题
            helper.setSubject(subject);
            //内容，html:true表示支持html文件，否则为普通文本
            helper.setText(content,true);
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());
        }
    }
}
