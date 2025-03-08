package com.wp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@SpringBootApplication
public class WPApplication {
    public static void main(String[] args) {
        SpringApplication.run(WPApplication.class, args);
        log.info("server started");
    }
}
