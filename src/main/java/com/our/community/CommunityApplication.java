package com.our.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ComponentScan注解，扫描配置类所在的包以及子包的bean，
 * 业务组件最好用Service注解标明，
 * 处理请求组件使用Controller注解标明，
 * 数据库访问组件使用Repository注解标明,
 * 通用标明注解Component
 */
@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
