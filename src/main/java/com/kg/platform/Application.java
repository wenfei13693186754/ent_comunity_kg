package com.kg.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.kg.platform")
@EnableTransactionManagement
public class Application{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}