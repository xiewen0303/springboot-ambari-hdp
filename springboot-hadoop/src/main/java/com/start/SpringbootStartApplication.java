package com.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan(basePackages = "com.mybatis.demo.mapper")
@ComponentScan(basePackages = "com.mybatis.demo")
public class SpringbootStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootStartApplication.class,args);
    }
}
