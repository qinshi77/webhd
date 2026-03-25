package com.example.webhd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.webhd.mapper")
public class WebhdApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhdApplication.class, args);
    }

}
