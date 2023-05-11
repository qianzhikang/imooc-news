package com.imooc.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description 启动类
 * @Date 2023-05-08-14-43
 * @Author qianzhikang
 */
@SpringBootApplication
@MapperScan(basePackages = "com.imooc.admin.mapper")
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
